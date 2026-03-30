package com.minh.tracking_service.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestDbSchemaInitializer {
    private final JdbcTemplate questDbJdbcTemplate;

    @Value("${questdb.table}")
    private String tableName;

    @Value("${questdb.retention-days}")
    private int retentionDays;

    @PostConstruct
    public void initialize() {
        String sql = """
                CREATE TABLE IF NOT EXISTS %s (
                    timestamp TIMESTAMP,
                    id SYMBOL,
                    lat DOUBLE,
                    lon DOUBLE,
                    alt DOUBLE,
                    velocity DOUBLE,
                    angular_velocity DOUBLE,
                    type SYMBOL,
                    status SYMBOL
                ) TIMESTAMP(timestamp) PARTITION BY DAY TTL %d DAYS
                """.formatted(tableName, retentionDays);

        questDbJdbcTemplate.execute(sql);
        log.info("QuestDB table '{}' is ready with TTL {} days", tableName, retentionDays);
    }
}
