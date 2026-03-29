package com.minh.realtime_gateway.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.common.model.Target;
import com.minh.realtime_gateway.session.SessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SessionRegistry sessionRegistry;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            byte[] payload = message.getBody();
            String channel = new String(message.getChannel());
            if ("realtime-data".equals(channel)) {
                List<Target> targets = objectMapper.readValue(payload, new TypeReference<List<Target>>() {
                });
                sessionRegistry.broadcast(targets);
            } else {
                log.warn("Received message from unknown channel '{}'", channel);
            }
        } catch (Exception e) {
            log.error("Có lỗi xảy ra khi xử lý message: {}", e.getMessage(), e);
        }
    }
}
