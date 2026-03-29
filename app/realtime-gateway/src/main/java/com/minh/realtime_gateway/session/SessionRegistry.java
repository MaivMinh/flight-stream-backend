package com.minh.realtime_gateway.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.common.model.Target;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionRegistry {
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session.getId(), session);
    }

    public List<WebSocketSession> getAllSessions() {
        return sessions.values().stream().toList();
    }

    public void broadcast(List<Target> payload) {
        String json;
        try {
            json = Objects.requireNonNull(objectMapper.writeValueAsString(payload));
        } catch (Exception e) {
            log.error("Serialize broadcast payload failed: {}", e.getMessage(), e);
            return;
        }
        TextMessage message = new TextMessage(json);
        for (WebSocketSession session : getAllSessions()) {
            if (!session.isOpen()) {
                continue;   /// Chuyển sang session tiếp.
            }
            try {
                synchronized (session) {
                    session.sendMessage(message);
                }
            } catch (Exception e) {
                log.error("Broadcast message to session {} failed: {}", session.getId(), e.getMessage(), e);
            }
        }
    }
}