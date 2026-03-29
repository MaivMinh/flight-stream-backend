package com.minh.realtime_gateway.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.realtime_gateway.session.SessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketHandler implements WebSocketHandler {
    private final SessionRegistry registry;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("=== New WebSocket Connection ===");
        log.info("Session ID: {}", session.getId());
        log.info("================================");

        registry.addSession(session);

        // ✅ GỬI WELCOME MESSAGE NGAY SAU KHI KẾT NỐI
        try {
            Map<String, Object> welcomeMsg = new HashMap<>();
            welcomeMsg.put("type", "CONNECTED");
            welcomeMsg.put("message", "Kết nối thành công. Vui lòng đợi trong giây lát!");
            welcomeMsg.put("timestamp", System.currentTimeMillis());

            String json = objectMapper.writeValueAsString(welcomeMsg);
            session.sendMessage(new TextMessage(json));
            log.info("✅ Welcome message sent successfully");
        } catch (Exception e) {
            log.error("❌ Error sending welcome message", e);
        }
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
