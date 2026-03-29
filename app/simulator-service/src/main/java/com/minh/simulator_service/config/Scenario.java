package com.minh.simulator_service.config;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Scenario {
    private String name;
    private String type;
    private Integer targetCount;
    private TrajectoryConfig trajectoryConfig;
    private VelocityRange velocityRange;
}
