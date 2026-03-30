package com.minh.realtime_gateway.websocket;

import com.minh.realtime_gateway.session.SessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketHandler implements WebSocketHandler {
    private final SessionRegistry registry;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("=== New WebSocket Connection ===");
        log.info("Session ID: {}", session.getId());
        log.info("================================");
        registry.addSession(session);
    }


    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Transport error in session {}: {}", session.getId(), exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("WebSocket connection closed. Session ID: {}, Close Status: {}", session.getId(), closeStatus);
        registry.removeSession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
