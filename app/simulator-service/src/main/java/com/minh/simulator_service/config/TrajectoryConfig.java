package com.minh.simulator_service.config;

import com.minh.common.model.Point;
import com.minh.simulator_service.trajectory.AngularVelocityRange;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrajectoryConfig {
    private String type; // CIRCLE / STRAIGHT / ZIGZAG

    // circle
    private Point center;
    private Double radius;
    private AngularVelocityRange angularVelocityRange;

    // straight
    private Point start;
    private Point end;

    // zigzag
    private List<Point> points;
}
