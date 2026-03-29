package com.minh.simulator_service.config;

import com.minh.common.model.Target;
import com.minh.simulator_service.trajectory.Trajectory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SimulationTarget {
    public Target target; // payload
    public Trajectory trajectory; // behavior
}