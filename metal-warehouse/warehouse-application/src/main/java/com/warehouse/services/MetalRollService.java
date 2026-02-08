package com.warehouse.services;

import com.warehouse.DTO.FilterDTO;
import com.warehouse.DTO.MetalRollDTO;
import com.warehouse.DTO.RollStatisticsDTO;
import com.warehouse.entities.MetalRoll;
import com.warehouse.exceptions.ResourceNotFoundException;
import com.warehouse.repositories.MetalRollRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetalRollService {

    private final MetalRollRepository repository;

    public MetalRollService(MetalRollRepository repository) {
        this.repository = repository;
    }

    public MetalRollDTO addMetalRoll(double length, double weight) {
        if (length <= 0 || weight <= 0) {
            throw new IllegalArgumentException("Length and weight must be positive numbers.");
        }

        MetalRoll roll = new MetalRoll(length, weight, LocalDate.now(), null);
        return new MetalRollDTO(repository.save(roll));
    }

    public MetalRollDTO removeMetalRoll(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be positive numbers.");
        }

        MetalRoll roll = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("MetalRoll with id " + id + " not found"));
        repository.delete(roll);
        return new MetalRollDTO(roll);
    }

    public List<MetalRollDTO> getFilteredMetalRolls(FilterDTO filter) {
        List<MetalRoll> rolls = repository.findAll();

        if (filter.hasIdFilter()) {
            rolls = rolls.stream()
                    .filter(r -> r.getId() >= filter.getIdFrom() && r.getId() <= filter.getIdTo())
                    .toList();
        }

        if (filter.hasLengthFilter()) {
            rolls = rolls.stream()
                    .filter(r -> r.getLength() >= filter.getLengthFrom() && r.getLength() <= filter.getLengthTo())
                    .toList();
        }

        if (filter.hasWeightFilter()) {
            rolls = rolls.stream()
                    .filter(r -> r.getWeight() >= filter.getWeightFrom() && r.getWeight() <= filter.getWeightTo())
                    .toList();
        }

        if (filter.hasAddedDateFilter()) {
            rolls = rolls.stream()
                    .filter(r -> !r.getDateAdded().isBefore(filter.getAddedFrom()) &&
                            !r.getDateAdded().isAfter(filter.getAddedTo()))
                    .toList();
        }

        if (filter.hasRemovedDateFilter()) {
            rolls = rolls.stream()
                    .filter(r -> r.getDateOfDeletion() != null &&
                            !r.getDateOfDeletion().isBefore(filter.getRemovedFrom()) &&
                            !r.getDateOfDeletion().isAfter(filter.getRemovedTo()))
                    .toList();
        }

        return rolls.stream().map(MetalRollDTO::new).toList();
    }

    public RollStatisticsDTO getStatistics(LocalDate periodStart, LocalDate periodEnd) {
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

        return stats;
    }
}
