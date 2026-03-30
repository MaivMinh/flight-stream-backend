package com.minh.realtime_gateway.session;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class SessionWrapperFactory {
    private final ExecutorService sessionSenderExecutor;

    public SessionWrapper create(WebSocketSession session) {
        return new SessionWrapper(session, sessionSenderExecutor);
    }
}
