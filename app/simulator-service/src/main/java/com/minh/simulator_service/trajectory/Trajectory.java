package com.minh.simulator_service.trajectory;

import com.minh.common.model.Target;

public interface Trajectory {
    void update(Target target, Long deltaTimeMs);
}
