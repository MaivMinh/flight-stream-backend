package com.minh.tracking_service.kafka;

import com.minh.common.kafka.KafkaTopics;
import com.minh.common.model.Target;
import com.minh.tracking_service.service.BufferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationConsumer {
    private final BufferService bufferService;

    @KafkaListener(
            topics = KafkaTopics.SIMULATION,
            groupId = "tracking-service",
            containerFactory = "kafkaListenerContainerFactory",
            concurrency = "4"
    )
    public void consume(List<Target> targets) {
        bufferService.addBatch(targets);
    }
}
