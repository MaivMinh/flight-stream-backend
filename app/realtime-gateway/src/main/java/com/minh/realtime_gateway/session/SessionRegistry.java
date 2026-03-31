package com.minh.realtime_gateway.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.common.model.Target;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionRegistry {
    private final ObjectMapper objectMapper;
    private final SessionWrapperFactory sessionWrapperFactory;
    private final Map<String, SessionWrapper> sessions = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session) {
        ConcurrentWebSocketSessionDecorator sessionDecorator = new ConcurrentWebSocketSessionDecorator(session, 5000, 8000000);
        sessions.put(session.getId(), sessionWrapperFactory.create(sessionDecorator));
    }

    public void removeSession(WebSocketSession session) {
        SessionWrapper wrapper = sessions.remove(session.getId());
        if (wrapper != null) {
            wrapper.stop();
        }
    }

    public List<SessionWrapper> getAllSessions() {
        return sessions.values().stream().toList();
    }

    public void broadcast(List<Target> payload) {
        for (Target target : payload) {
            target.setTimestamp(System.currentTimeMillis());
        }
        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return;
        }
        TextMessage message = new TextMessage(json);
        for (SessionWrapper wrapper : getAllSessions()) {
            try {
                wrapper.send(message);
            } catch (Exception e) {
                removeSession(wrapper.getSession());
            }
        }
    }
}
