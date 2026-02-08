package com.warehouse.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FilterDTO {
    private Integer idFrom;

    private Integer idTo;

    private Double weightFrom;

    private Double weightTo;

    private Double lengthFrom;

    private Double lengthTo;

    private LocalDate addedFrom;

    private LocalDate addedTo;

    private LocalDate removedFrom;

    private LocalDate removedTo;

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
