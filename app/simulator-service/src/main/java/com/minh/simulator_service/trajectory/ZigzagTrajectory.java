package com.minh.simulator_service.trajectory;

import com.minh.common.model.Point;
import com.minh.common.model.Target;
import com.minh.simulator_service.enums.TargetStatus;

import java.util.List;

public class ZigzagTrajectory implements Trajectory {
    private final List<Point> points;

    public ZigzagTrajectory(List<Point> points) {
        this.points = points;
    }

    @Override
    public void update(Target target, Long deltaMs) {
        if (!TargetStatus.FLYING.name().equals(target.getStatus())) return;
        if (points == null || points.size() < 2) return;

        int idx = target.currentWaypointIndex;

        // đã tới điểm cuối
        if (idx >= points.size() - 1) {
            target.status = TargetStatus.COMPLETED.name();
            return;
        }

        Point next = points.get(idx + 1);

        double lat1 = Math.toRadians(target.lat);
        double lon1 = Math.toRadians(target.lon);
        double lat2 = Math.toRadians(next.getLat());
        double lon2 = Math.toRadians(next.getLon());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double earthRadius = 6371000; // meters
        double dist = earthRadius * c;

        double move = target.velocity * deltaMs / 1000.0;

        double epsilon = Math.max(0.5, move); // dynamic threshold

        if (dist < epsilon) {
            target.lat = next.getLat();
            target.lon = next.getLon();
            target.currentWaypointIndex++;
            target.nextLat = (target.currentWaypointIndex + 1 < points.size()) ? points.get(target.currentWaypointIndex + 1).getLat() : points.get(target.currentWaypointIndex).getLat();
            target.nextLon = (target.currentWaypointIndex + 1 < points.size()) ? points.get(target.currentWaypointIndex + 1).getLon() : points.get(target.currentWaypointIndex).getLon();
            return;
        }

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);

        double bearing = Math.atan2(y, x);
        double angularDistance = move / earthRadius;
        double newLat = Math.asin(Math.sin(lat1) * Math.cos(angularDistance) + Math.cos(lat1) * Math.sin(angularDistance) * Math.cos(bearing));
        double newLon = lon1 + Math.atan2(
                Math.sin(bearing) * Math.sin(angularDistance) * Math.cos(lat1),
                Math.cos(angularDistance) - Math.sin(lat1) * Math.sin(newLat)
        );

        target.lat = Math.toDegrees(newLat);
        target.lon = Math.toDegrees(newLon);
        target.timestamp = System.currentTimeMillis();
    }
}
