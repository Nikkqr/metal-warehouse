package com.warehouse.controllers;

import com.warehouse.DTO.FilterDTO;
import com.warehouse.DTO.MetalRollDTO;
import com.warehouse.DTO.RollStatisticsDTO;
import com.warehouse.requests.CreateRollRequest;
import com.warehouse.services.MetalRollService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rolls")
public class MetalRollController {

    private final MetalRollService rollService;

    public MetalRollController(MetalRollService rollService) {
        this.rollService = rollService;
    }

    @PostMapping
    public ResponseEntity<MetalRollDTO> addRoll(@RequestBody CreateRollRequest request) {
        MetalRollDTO saved =  rollService.addMetalRoll(request.getLength(), request.getWeight());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MetalRollDTO> deleteRoll(@PathVariable Integer id) {
        MetalRollDTO removed =  rollService.removeMetalRoll(id);
        return ResponseEntity.ok(removed);
    }

    @GetMapping
    public ResponseEntity<List<MetalRollDTO>> getRolls(FilterDTO filter) {
        List<MetalRollDTO> rolls = rollService.getFilteredMetalRolls(filter);
        return ResponseEntity.ok(rolls);
    }

    @GetMapping("/stats")
    public ResponseEntity<RollStatisticsDTO> getStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        if (start.isAfter(end)) {
            return ResponseEntity.badRequest().build();
        }

        RollStatisticsDTO stats = rollService.getStatistics(start, end);
        return ResponseEntity.ok(stats);
    }
}
