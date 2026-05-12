package com.minh.realtime_gateway.redis;

import com.minh.common.model.Target;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;
    private final ExecutorService redisExecutor;

    public void publish(List<Target> targets) {
        redisExecutor.submit(() -> {
            redisTemplate.convertAndSend(topic.getTopic(), targets);
        });
    }
}