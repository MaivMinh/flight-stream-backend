package com.minh.simulator_service.config;

import com.minh.common.model.Point;
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
    private Double angularVelocity;

    // straight
    private Point start;
    private Point end;

    // zigzag
    private List<Point> points;
}
