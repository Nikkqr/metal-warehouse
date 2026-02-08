package com.warehouse.DTO;

import lombok.Data;

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
}
