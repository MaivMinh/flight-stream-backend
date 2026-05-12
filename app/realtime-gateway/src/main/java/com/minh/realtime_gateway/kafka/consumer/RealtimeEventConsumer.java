package com.minh.realtime_gateway.kafka.consumer;

import com.minh.common.kafka.KafkaTopics;
import com.minh.common.model.Target;
import com.minh.realtime_gateway.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RealtimeEventConsumer {
    private final RedisPublisher redisPublisher;

    @KafkaListener(
            topics = KafkaTopics.SIMULATION,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(List<Target> targets, Acknowledgment acknowledgment) {
        redisPublisher.publish(targets);
        acknowledgment.acknowledge();
    }
}