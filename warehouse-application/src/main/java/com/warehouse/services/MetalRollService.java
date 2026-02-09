package com.warehouse.services;

import com.warehouse.DTO.FilterDTO;
import com.warehouse.DTO.MetalRollDTO;
import com.warehouse.DTO.RollStatisticsDTO;

import java.time.LocalDate;
import java.util.List;

public interface MetalRollService {

    MetalRollDTO addMetalRoll(double length, double weight);

    MetalRollDTO removeMetalRoll(int id);

    List<MetalRollDTO> getFilteredMetalRolls(FilterDTO filter);

    RollStatisticsDTO getStatistics(LocalDate periodStart, LocalDate periodEnd);
}
