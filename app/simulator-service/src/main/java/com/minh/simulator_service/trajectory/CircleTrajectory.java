package com.minh.simulator_service.trajectory;

import com.minh.common.model.Target;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CircleTrajectory implements Trajectory {
    private final Double centerLat;
    private final Double centerLon;
    private final Double radius;
    private final Double angularVelocity;
    /// Vận tốc góc.
    private Double angle;

    @Override
    public void update(Target target, Long deltaTimeMs) {
        double dt = deltaTimeMs / 1000.0;
        angle += angularVelocity * dt;

        double earthRadius = 6371000; // meters

        double lat1 = Math.toRadians(centerLat);
        double lon1 = Math.toRadians(centerLon);
        double angularDistance = radius / earthRadius;

        double newLat = Math.asin(
                Math.sin(lat1) * Math.cos(angularDistance)
                        + Math.cos(lat1) * Math.sin(angularDistance) * Math.cos(angle)
        );

        double newLon = lon1 + Math.atan2(
                Math.sin(angle) * Math.sin(angularDistance) * Math.cos(lat1),
                Math.cos(angularDistance) - Math.sin(lat1) * Math.sin(newLat)
        );

        target.lat = Math.toDegrees(newLat);
        target.lon = Math.toDegrees(newLon);
    }
}