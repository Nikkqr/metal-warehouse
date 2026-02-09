package com.warehouse.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "metalroll")
public class MetalRoll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private double length;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private LocalDate dateAdded;

    @Column(nullable = true)
    private LocalDate dateOfDeletion;

    public MetalRoll() {}

    public MetalRoll(double length, double weight, LocalDate dateAdded, LocalDate dateOfDeletion) {
        this.length = length;
        this.weight = weight;
        this.dateAdded = dateAdded;
        this.dateOfDeletion = dateOfDeletion;
    }
}
