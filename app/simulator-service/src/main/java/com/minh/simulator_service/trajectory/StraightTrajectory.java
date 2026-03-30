package com.minh.simulator_service.trajectory;

import com.minh.common.model.Point;
import com.minh.common.model.Target;
import com.minh.simulator_service.enums.TargetStatus;

public class StraightTrajectory implements Trajectory {
    private final double endLat;
    private final double endLon;

    private final double dirLat;
    private final double dirLon;

    public StraightTrajectory(Point start, Point end) {
        this.endLat = end.getLat();
        this.endLon = end.getLon();

        double dx = end.getLat() - start.getLat();
        double dy = end.getLon() - start.getLon();

        double length = Math.sqrt(dx * dx + dy * dy);

        this.dirLat = dx / length;
        this.dirLon = dy / length;
    }

    @Override
    public void update(Target t, Long deltaMs) {
        if (!TargetStatus.FLYING.name().equals(t.getStatus())) return;
        double distance = t.velocity * deltaMs / 1000.0; /// velocity (m/s). -> distance (m) = velocity (m/s) * time (s)

        /// Vì vị trí của thiết bị bay đang được biểu diễn bằng tọa độ (lat, lon), nên phải chuyển đổi từ mét -> đô.
        /// Theo lý thuyết thì 1 độ lat = 111_111 m.
        /// Còn độ lon thì phụ thuộc vào vĩ độ, tại xích đạo thì 1 độ lon cũng bằng 111_111 m, nhưng khi càng gần cực thì 1 độ lon sẽ tương đương với khoảng cách nhỏ hơn.
        double metersPerLat = 111_000.0;
        double metersPerLon = 111_000.0 * Math.cos(Math.toRadians(t.lat));

        /// Dựa vào độ dài của đoạn đường vừa di chuyển,ta tính được số độ lat và lon mà thiết bị bay đã di chuyển trên quỹ đạo thẳng.
        double dLat = (distance / metersPerLat) * dirLat;
        double dLon = (distance / metersPerLon) * dirLon;

        double nextLat = t.lat + dLat;
        double nextLon = t.lon + dLon;

        double dxEnd = endLat - t.lat;
        double dyEnd = endLon - t.lon;

        double dxNext = endLat - nextLat;
        double dyNext = endLon - nextLon;

        if ((dxEnd * dxNext < 0) || (dyEnd * dyNext < 0)) {
            t.lat = endLat;
            t.lon = endLon;
            t.status = TargetStatus.COMPLETED.name();
            return;
        }

        t.lat = nextLat;
        t.lon = nextLon;
    }
}
