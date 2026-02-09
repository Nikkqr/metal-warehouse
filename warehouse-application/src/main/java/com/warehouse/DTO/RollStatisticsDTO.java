package com.warehouse.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RollStatisticsDTO {
    private long addedCount;

    private long removedCount;

    private double avgLength;

    private double avgWeight;

    private double maxLength;

    private double minLength;

    private double maxWeight;

    private double minWeight;

    private double totalWeight;

    private long maxIntervalDays;

    private long minIntervalDays;

    private LocalDate dayWithMinCount;

    private LocalDate dayWithMaxCount;

    private LocalDate dayWithMinWeight;

    private LocalDate dayWithMaxWeight;
}
