package com.minh.tracking_service.service.impl;

import com.minh.common.model.Target;
import io.questdb.client.Sender;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestDbWriter {
    private static final int BATCH_SIZE = 2000;

    private final BufferServiceImpl bufferService;
    private final Sender questDbSender;

    @Value("${questdb.table}")
    private String tableName;

    private volatile boolean running = true;

    @PostConstruct
    public void start() {
        Thread.ofVirtual().name("questdb-writer").start(this::runWriter);
    }

    @PreDestroy
    public void stop() {
        running = false;
    }

    private void runWriter() {
        while (running) {
            try {
                List<Target> events = bufferService.drainTo(BATCH_SIZE);
                if (events.isEmpty()) {
                    Thread.sleep(50);
                    continue;
                }

                for (Target event : events) {
                    writeRow(event);
                }

                questDbSender.flush();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                log.error("Failed to write batch to QuestDB", e);
            }
        }
    }

    private void writeRow(Target target) {
        if (Objects.isNull(target) || Objects.isNull(target.getId()) || Objects.isNull(target.getTimestamp())) {
            log.error("Invalid target data: {}", target);
            return;
        }
        try {
            Sender row = questDbSender.table(tableName).symbol("id", String.valueOf(target.getId()));
            row = addSymbol(row, "type", target.getType());
            row = addSymbol(row, "status", target.getStatus());
            row = addDouble(row, "lat", target.getLat());
            row = addDouble(row, "lon", target.getLon());
            row = addDouble(row, "alt", target.getAlt());
            row = addDouble(row, "velocity", target.getVelocity());
            row = addDouble(row, "angular_velocity", target.getAngularVelocity());
            row.at(target.getTimestamp() * 1_000, ChronoUnit.MICROS);
        } catch (Exception e) {
            log.error("Failed to write target to QuestDB: {}", target, e);
        }
    }

    private Sender addDouble(Sender row, String columnName, Double value) {
        return Objects.isNull(value) ? row : row.doubleColumn(columnName, value);
    }

    private Sender addSymbol(Sender row, String columnName, String value) {
        return !StringUtils.hasText(value) ? row : row.symbol(columnName, value);
    }
}
