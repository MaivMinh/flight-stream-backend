package com.minh.simulator_service.config;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimulatorConfig {
    private List<Scenario> scenarios;
}