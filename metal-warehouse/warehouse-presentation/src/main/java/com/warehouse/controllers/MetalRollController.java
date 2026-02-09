package com.warehouse.controllers;

import com.warehouse.DTO.FilterDTO;
import com.warehouse.DTO.MetalRollDTO;
import com.warehouse.DTO.RollStatisticsDTO;
import com.warehouse.requests.CreateRollRequest;
import com.warehouse.services.MetalRollService;
import com.warehouse.services.MetalRollServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rolls")
public class MetalRollController {

    private final MetalRollService rollService;

    public MetalRollController(MetalRollServiceImpl rollService) {
        this.rollService = rollService;
    }

    @Operation(summary = "Добавление нового рулона метала")
    @PostMapping
    public ResponseEntity<MetalRollDTO> addRoll(@RequestBody CreateRollRequest request) {
        MetalRollDTO saved =  rollService.addMetalRoll(request.getLength(), request.getWeight());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Удаление рулона метала")
    @DeleteMapping("/{id}")
    public ResponseEntity<MetalRollDTO> deleteRoll(@PathVariable Integer id) {
        MetalRollDTO removed =  rollService.removeMetalRoll(id);
        return ResponseEntity.ok(removed);
    }

    @Operation(summary = "Получение рулонов метала с фильтрами")
    @GetMapping
    public ResponseEntity<List<MetalRollDTO>> getRolls(FilterDTO filter) {
        List<MetalRollDTO> rolls = rollService.getFilteredMetalRolls(filter);
        return ResponseEntity.ok(rolls);
    }

    @Operation(summary = "Получение статистики по рулонам метала за период")
    @GetMapping("/stats")
    public ResponseEntity<RollStatisticsDTO> getStats(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        RollStatisticsDTO stats = rollService.getStatistics(start, end);
        return ResponseEntity.ok(stats);
    }
}
