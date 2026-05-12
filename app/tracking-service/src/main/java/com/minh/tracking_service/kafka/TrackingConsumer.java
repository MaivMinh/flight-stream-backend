package com.minh.tracking_service.kafka;

import com.minh.common.kafka.KafkaTopics;
import com.minh.common.model.Target;
import com.minh.tracking_service.service.BufferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingConsumer {
    private final BufferService bufferService;

    @KafkaListener(
            topics = KafkaTopics.SIMULATION,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(List<Target> targets, Acknowledgment acknowledgment) {
        bufferService.addBatch(targets);
        acknowledgment.acknowledge();
    }
}
