package com.minh.realtime_gateway.redis;

import com.minh.realtime_gateway.session.SessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final SessionRegistry sessionRegistry;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            byte[] payload = message.getBody();
            String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
            if ("realtime-data".equals(channel)) {
                String json = new String(payload, StandardCharsets.UTF_8);
                sessionRegistry.broadcast(json);
            } else {
                log.warn("Received message from unknown channel '{}'", channel);
            }
        } catch (Exception e) {
            log.error("Có lỗi xảy ra khi xử lý message: {}", e.getMessage(), e);
        }
    }
}
