package com.minh.tracking_service.service.impl;

import com.minh.common.model.Target;
import com.minh.tracking_service.service.BufferService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@RequiredArgsConstructor
@Service
public class BufferServiceImpl implements BufferService {
    private final BlockingDeque<Target> queue = new LinkedBlockingDeque<>(100_000);

    @Async
    public void addBatch(List<Target> targets) {
        for (Target target : targets) {
            if (!queue.offer(target)) {
                // Nếu queue đầy, lấy phần tử cũ nhất ra để nhường chỗ cho phần tử mới.
                queue.poll();
                queue.offer(target);
            }
        }
    }

    public List<Target> drainTo(int batchSize) {
        List<Target> batch = new java.util.ArrayList<>(batchSize);
        queue.drainTo(batch, batchSize);  // Lấy tối đa batchSize phần tử từ queue.
        return batch;
    }
}