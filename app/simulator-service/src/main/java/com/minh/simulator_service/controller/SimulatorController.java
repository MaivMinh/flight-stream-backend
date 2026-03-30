package com.minh.simulator_service.controller;

import com.minh.simulator_service.generator.SimulationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulator")
@RequiredArgsConstructor
public class SimulatorController {
    private final SimulationEngine engine;

    @GetMapping("/start")
    public ResponseEntity<Void> startSimulation(@RequestParam(name = "interval", defaultValue = "280") long interval,
                                                @RequestParam(name = "shard", defaultValue = "4") int shards) {
        engine.start(interval, shards);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/stop")
    public ResponseEntity<Void> stopSimulation() {
        engine.stop();
        return ResponseEntity.ok().build();
    }
}