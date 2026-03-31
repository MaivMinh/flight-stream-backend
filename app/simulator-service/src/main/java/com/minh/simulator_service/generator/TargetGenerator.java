package com.minh.simulator_service.generator;

import com.minh.simulator_service.config.Scenario;
import com.minh.simulator_service.config.SimulationTarget;
import com.minh.simulator_service.config.SimulatorConfig;
import com.minh.simulator_service.config.TrajectoryConfig;
import com.minh.common.model.Target;
import com.minh.simulator_service.enums.TargetStatus;
import com.minh.simulator_service.trajectory.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TargetGenerator {
    private static final Random RANDOM = new Random(36L);

    public static List<SimulationTarget> generateTargets(SimulatorConfig config) {
        List<SimulationTarget> result = new ArrayList<>();
        int id = 0;
        for (Scenario scenario : config.getScenarios()) {
            String trajectoryType = scenario.getTrajectoryConfig().getType();
            for (int i = 0; i < scenario.getTargetCount(); i++) {
                Target target = new Target();
                target.setId(id++);
                target.setType(scenario.getType()); /// Alley, Enemy, Undefined.
                target.setStatus(TargetStatus.FLYING.name());
                if (!"CIRCLE".equals(trajectoryType)) {
                    /// Nếu không phải là quỹ đạo tròn thì mới tính vận tốc ban đầu kiểu này.
                    Double vMin = scenario.getVelocityRange().getMin();
                    Double vMax = scenario.getVelocityRange().getMax();
                    target.setVelocity(vMin + (vMax - vMin) * RANDOM.nextDouble());
                    target.setCenterLat(null);
                    target.setCenterLon(null);
                    target.setRadius(null);
                    if ("STRAIGHT".equals(trajectoryType)) {
                        /// Quỹ đạo thẳng thì set vị trí cuối cùng.
                        target.endLat = scenario.getTrajectoryConfig().getEnd().getLat();
                        target.endLon = scenario.getTrajectoryConfig().getEnd().getLon();
                    } else {
                        /// Quỹ đạo zigzag thì set vị trí điểm tiếp theo.
                        if (scenario.getTrajectoryConfig().getPoints().size() > 1) {
                            target.nextLat = scenario.getTrajectoryConfig().getPoints().get(1).getLat();
                            target.nextLon = scenario.getTrajectoryConfig().getPoints().get(1).getLon();
                        }
                    }
                } else {
                    target.setVelocity(null);  /// Tạm thời gán null.
                    target.setAngularVelocity(null);
                    target.setCenterLat(scenario.getTrajectoryConfig().getCenter().getLat());
                    target.setCenterLon(scenario.getTrajectoryConfig().getCenter().getLon());
                    target.setRadius(scenario.getTrajectoryConfig().getRadius());
                }
                Trajectory trajectory = createTrajectory(scenario.getTrajectoryConfig(), target, i, scenario.getTargetCount());
                SimulationTarget simulationTarget = new SimulationTarget(target, trajectory);
                result.add(simulationTarget);
            }
        }
        return result;
    }

    private static Trajectory createTrajectory(TrajectoryConfig trajectory, Target target, int index, int total) {
        target.timestamp = System.currentTimeMillis();
        switch (trajectory.getType()) {
            case "CIRCLE": {
                double angle = 2 * Math.PI * index / total;
                double centerLat = trajectory.getCenter().getLat();
                double centerLon = trajectory.getCenter().getLon();
                double radius = trajectory.getRadius();
                /// Tính velocity ban đầu dựa vào angular velocity và bán kính.
                AngularVelocityRange range = trajectory.getAngularVelocityRange();
                double angularVelocity = range.min + (range.max - range.min) * RANDOM.nextDouble();
                target.setVelocity(angularVelocity * radius);
                target.setAngularVelocity(angularVelocity);

                target.lat = centerLat + radius * Math.cos(angle);
                target.lon = centerLon + radius * Math.sin(angle);
                target.alt = trajectory.getCenter().getAlt();
                return new CircleTrajectory(
                        trajectory.getCenter().getLat(),
                        trajectory.getCenter().getLon(),
                        trajectory.getRadius(),
                        angularVelocity,
                        angle
                );
            }
            case "STRAIGHT":
                target.lat = trajectory.getStart().getLat();
                target.lon = trajectory.getStart().getLon();
                target.alt = trajectory.getStart().getAlt();
                return new StraightTrajectory(
                        trajectory.getStart(),
                        trajectory.getEnd()
                );
            case "ZIGZAG":
                target.lat = trajectory.getPoints().getFirst().getLat();
                target.lon = trajectory.getPoints().getFirst().getLon();
                target.alt = trajectory.getPoints().getFirst().getAlt();
                return new ZigzagTrajectory(
                        trajectory.getPoints()
                );
            default:
                throw new RuntimeException("Không xác định được loại quỹ đạo {}: " + trajectory.getType());
        }
    }
}
