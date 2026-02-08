package com.warehouse.requests;

import lombok.Data;

@Data
public class CreateRollRequest {
    private Double length;

    private Double weight;
}