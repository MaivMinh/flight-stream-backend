package com.minh.tracking_service.service;

import com.minh.common.model.Target;

import java.util.List;

public interface BufferService {
    void addBatch(List<Target> targets);
    List<Target> drainTo(int batchSize);
}
