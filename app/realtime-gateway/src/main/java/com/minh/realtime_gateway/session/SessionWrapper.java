package com.minh.realtime_gateway.session;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Getter
@Setter
public class SessionWrapper {
    private final ConcurrentWebSocketSessionDecorator session;
    private final ExecutorService senderExecutor;
    private final BlockingQueue<TextMessage> messagesQueue = new LinkedBlockingQueue<>(5000);
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private Future<?> senderTask;

    public SessionWrapper(ConcurrentWebSocketSessionDecorator session, ExecutorService senderExecutor) {
        this.session = session;
        this.senderExecutor = senderExecutor;
        startWorker();
    }

    private void startWorker() {
        senderTask = senderExecutor.submit(() -> {
            try {
                while (isRunning.get() && session.isOpen()) {
                    TextMessage msg = messagesQueue.take();
                    session.sendMessage(msg);
                }
            } catch (InterruptedException | IOException e) {
                log.error("Worker thread interrupted for session {}: {}", session.getId(), e.getMessage(), e);
            } catch (Exception e) {
                log.error("Error sending message for session {}: {}", session.getId(), e.getMessage(), e);
            } finally {
                isRunning.set(false);
                try {
                    session.close();
                    stop();
                } catch (Exception ignored) {
                    log.error("Error when closing session {}: {}", session.getId(), ignored.getMessage(), ignored);
                }
            }
        });
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
        if (Objects.nonNull(senderTask)) {
            senderTask.cancel(true);
        }
    }
}
