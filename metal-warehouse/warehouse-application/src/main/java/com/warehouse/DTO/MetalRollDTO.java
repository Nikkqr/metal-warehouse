package com.warehouse.DTO;

import com.warehouse.entities.MetalRoll;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MetalRollDTO {
    private int id;

    private double length;

    private double weight;

    private LocalDate dateAdded;

    private LocalDate dateOfDeletion;

    public MetalRollDTO(MetalRoll mr) {
        id = mr.getId();
        length = mr.getLength();
        weight = mr.getWeight();
        dateAdded = mr.getDateAdded();
        dateOfDeletion = mr.getDateOfDeletion();
    }
}
