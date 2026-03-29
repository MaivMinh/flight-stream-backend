package com.minh.realtime_gateway.session;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Getter
@Setter
public class SessionWrapper {
    private final WebSocketSession session;
    private final BlockingQueue<TextMessage> messagesQueue = new LinkedBlockingQueue<>(500);
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private Thread workerThread;

    public SessionWrapper(WebSocketSession session) {
        this.session = session;
        startWorker();
    }

    private void startWorker() {
        workerThread = new Thread(() -> {
            try {
                while (isRunning.get() && session.isOpen()) {
                    TextMessage msg = messagesQueue.take();
                    session.sendMessage(msg);
                }
            } catch (InterruptedException e) {
                log.error("Worker thread interrupted for session {}: {}", session.getId(), e.getMessage(), e);
            } catch (Exception e) {
                log.error("Error sending message for session {}: {}", session.getId(), e.getMessage(), e);
            } finally {
                isRunning.set(false);
                try {
                    session.close();
                } catch (Exception ignored) {
                }
            }
        });
        workerThread.start();
    }

    public void send(TextMessage message) {
        if (isRunning.get()) {
            if (!messagesQueue.offer(message)) {    /// Lấy event cũ ra.
                messagesQueue.poll();
                messagesQueue.offer(message);
            }
        }
    }

    public void stop() {
        isRunning.set(false);
        if (Objects.nonNull(workerThread)) {
            workerThread.interrupt();
        }
    }
}