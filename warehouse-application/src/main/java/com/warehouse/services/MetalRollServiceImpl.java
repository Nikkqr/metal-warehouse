package com.warehouse.services;

import com.warehouse.DTO.FilterDTO;
import com.warehouse.DTO.MetalRollDTO;
import com.warehouse.DTO.RollStatisticsDTO;
import com.warehouse.entities.MetalRoll;
import com.warehouse.exceptions.InvalidRollDataException;
import com.warehouse.exceptions.RollNotFoundException;
import com.warehouse.repositories.MetalRollRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MetalRollServiceImpl implements MetalRollService{

    private final MetalRollRepository repository;

    public MetalRollServiceImpl(MetalRollRepository repository) {
        this.repository = repository;
    }

    public MetalRollDTO addMetalRoll(double length, double weight) {
        if (length <= 0 || weight <= 0) {
            throw new InvalidRollDataException("Length and weight must be positive numbers.");
        }

        MetalRoll roll = new MetalRoll(length, weight, LocalDate.now(), null);
        return new MetalRollDTO(repository.save(roll));
    }

    public MetalRollDTO removeMetalRoll(int id) {
        if (id <= 0) {
            throw new InvalidRollDataException("Id must be positive numbers.");
        }

        MetalRoll roll = repository.findById(id).orElseThrow(() -> new RollNotFoundException("MetalRoll with id " + id + " not found"));
        roll.setDateOfDeletion(LocalDate.now());
        MetalRoll saved = repository.save(roll);

        return new MetalRollDTO(saved);
    }

    public List<MetalRollDTO> getFilteredMetalRolls(FilterDTO filter) {
        List<MetalRoll> rolls = repository.findAll();

        if (filter.hasIdFilter()) {
            if(filter.getIdFrom() <= 0 || filter.getIdTo() <=0) {
                throw new InvalidRollDataException("Id must be positive numbers.");
            }
            rolls = rolls.stream()
                    .filter(r -> r.getId() >= filter.getIdFrom() && r.getId() <= filter.getIdTo())
                    .toList();
        }

        if (filter.hasLengthFilter()) {
            if(filter.getLengthFrom() < 0 || filter.getLengthTo() < 0) {
                throw new InvalidRollDataException("Length must be positive numbers.");
            }
            rolls = rolls.stream()
                    .filter(r -> r.getLength() >= filter.getLengthFrom() && r.getLength() <= filter.getLengthTo())
                    .toList();
        }

        if (filter.hasWeightFilter()) {
            if(filter.getWeightFrom() < 0 || filter.getWeightTo() < 0) {
                throw new InvalidRollDataException("Weight must be positive numbers.");
            }
            rolls = rolls.stream()
                    .filter(r -> r.getWeight() >= filter.getWeightFrom() && r.getWeight() <= filter.getWeightTo())
                    .toList();
        }

        if (filter.hasAddedDateFilter()) {
            if(filter.getAddedFrom().isAfter(filter.getAddedTo())) {
                throw new InvalidRollDataException("The start date of adding cannot be later than the end date.");
            }
            rolls = rolls.stream()
                    .filter(r -> !r.getDateAdded().isBefore(filter.getAddedFrom()) &&
                            !r.getDateAdded().isAfter(filter.getAddedTo()))
                    .toList();
        }

        if (filter.hasRemovedDateFilter()) {
            if(filter.getRemovedFrom().isAfter(filter.getRemovedTo())) {
                throw new InvalidRollDataException("The deletion start date cannot be later than the end date.");
            }
            rolls = rolls.stream()
                    .filter(r -> r.getDateOfDeletion() != null &&
                            !r.getDateOfDeletion().isBefore(filter.getRemovedFrom()) &&
                            !r.getDateOfDeletion().isAfter(filter.getRemovedTo()))
                    .toList();
        }

        return rolls.stream().map(MetalRollDTO::new).toList();
    }

    public RollStatisticsDTO getStatistics(LocalDate periodStart, LocalDate periodEnd) {
        if(periodStart.isAfter(periodEnd)) {
            throw new InvalidRollDataException("The start date of the period cannot be later than the end date");
        }

        List<MetalRoll> activeRolls = repository.findActiveInPeriod(periodStart, periodEnd);
        List<MetalRoll> addedInPeriod = repository.findByDateAddedBetween(periodStart, periodEnd);
        List<MetalRoll> removedInPeriod = repository.findByDateOfDeletionBetween(periodStart, periodEnd);

        RollStatisticsDTO stats = new RollStatisticsDTO();
        stats.setAddedCount(addedInPeriod.size());
        stats.setRemovedCount(removedInPeriod.size());

        if (activeRolls.isEmpty()) {
            stats.setAvgLength(0.0);
            stats.setAvgWeight(0.0);
            stats.setMaxLength(0.0);
            stats.setMinLength(0.0);
            stats.setMaxWeight(0.0);
            stats.setMinWeight(0.0);
            stats.setTotalWeight(0.0);
            stats.setMaxIntervalDays(0L);
            stats.setMinIntervalDays(-1L);
            return stats;
        }

        double totalLength = 0.0;
        double totalWeight = 0.0;
        double maxLength = Double.NEGATIVE_INFINITY;
        double minLength = Double.POSITIVE_INFINITY;
        double maxWeight = Double.NEGATIVE_INFINITY;
        double minWeight = Double.POSITIVE_INFINITY;

        List<Long> intervals = new ArrayList<>();

        for (MetalRoll r : activeRolls) {
            double length = r.getLength();
            double weight = r.getWeight();

            totalLength += length;
            totalWeight += weight;

            if (length > maxLength) maxLength = length;
            if (length < minLength) minLength = length;
            if (weight > maxWeight) maxWeight = weight;
            if (weight < minWeight) minWeight = weight;

            if (r.getDateOfDeletion() != null) {
                long days = r.getDateOfDeletion().toEpochDay() - r.getDateAdded().toEpochDay();
                intervals.add(days);
            }
        }

        stats.setAvgLength(totalLength / activeRolls.size());
        stats.setAvgWeight(totalWeight / activeRolls.size());
        stats.setMaxLength(maxLength);
        stats.setMinLength(minLength);
        stats.setMaxWeight(maxWeight);
        stats.setMinWeight(minWeight);
        stats.setTotalWeight(totalWeight);

        if (!intervals.isEmpty()) {
            long maxInterval = intervals.stream().mapToLong(Long::longValue).max().orElse(0L);
            long minInterval = intervals.stream().mapToLong(Long::longValue).min().orElse(0L);
            stats.setMaxIntervalDays(maxInterval);
            stats.setMinIntervalDays(minInterval);
        } else {
            stats.setMaxIntervalDays(0L);
            stats.setMinIntervalDays(-1L);
        }

        calculateMinMax(periodStart, periodEnd, activeRolls, stats);


        return stats;
    }

    private void calculateMinMax(LocalDate periodStart, LocalDate periodEnd, List<MetalRoll> activeRolls, RollStatisticsDTO stats) {
        Map<LocalDate, Long> countByDay = new HashMap<>();
        Map<LocalDate, Double> weightByDay = new HashMap<>();

        LocalDate current = periodStart;
        while (!current.isAfter(periodEnd)) {
            long count = 0;
            double weight = 0.0;

            for (MetalRoll r : activeRolls) {
                if (r.getDateAdded().isAfter(current)) continue;
                if (r.getDateOfDeletion() != null && !r.getDateOfDeletion().isAfter(current)) continue;

                count++;
                weight += r.getWeight();
            }

            countByDay.put(current, count);
            weightByDay.put(current, weight);

            current = current.plusDays(1);
        }

        LocalDate dayMinCount = countByDay.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);

        LocalDate dayMaxCount = countByDay.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);

        LocalDate dayMinWeight = weightByDay.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);

        LocalDate dayMaxWeight = weightByDay.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);

        stats.setDayWithMinCount(dayMinCount);
        stats.setDayWithMaxCount(dayMaxCount);
        stats.setDayWithMinWeight(dayMinWeight);
        stats.setDayWithMaxWeight(dayMaxWeight);
    }
}
