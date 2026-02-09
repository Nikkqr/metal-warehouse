package com.warehouse.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FilterDTO {
    private Integer idFrom = null;

    private Integer idTo = null;

    private Double weightFrom = null;

    private Double weightTo = null;

    private Double lengthFrom = null;

    private Double lengthTo = null;

    private LocalDate addedFrom = null;

    private LocalDate addedTo = null;

    private LocalDate removedFrom = null;

    private LocalDate removedTo = null;

    public boolean hasIdFilter() {
        return idFrom != null && idTo != null;
    }

    public boolean hasLengthFilter() {
        return lengthFrom != null && lengthTo != null;
    }

    public boolean hasWeightFilter() {
        return weightFrom != null && weightTo != null;
    }

    public boolean hasAddedDateFilter() {
        return addedFrom != null && addedTo != null;
    }

    public boolean hasRemovedDateFilter() {
        return removedFrom != null && removedTo != null;
    }
}
