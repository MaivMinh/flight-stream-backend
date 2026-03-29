package com.minh.realtime_gateway.redis;

import com.minh.common.model.Target;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    public void publish(List<Target> targets) {
        redisTemplate.convertAndSend(topic.getTopic(), targets);
    }
}