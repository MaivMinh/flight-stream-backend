package com.minh.simulator_service.controller;

import com.minh.simulator_service.generator.SimulationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulator")
@RequiredArgsConstructor
public class SimulatorController {
    private final SimulationEngine engine;

    @PostMapping("/start")
    public ResponseEntity<Void> startSimulation() {
        engine.start();
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/stop")
    public ResponseEntity<Void> stopSimulation() {
        engine.stop();
        return ResponseEntity.ok().build();
    }
}