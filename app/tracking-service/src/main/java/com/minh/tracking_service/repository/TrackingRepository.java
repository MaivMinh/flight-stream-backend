package com.minh.tracking_service.repository;

import com.minh.common.model.Target;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TrackingRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${questdb.table}")
    private String tableName;

    public List<Target> findRecentHistoryByTargetIdAndDays(Integer targetId, Timestamp fromTimeStamp, Timestamp toTimeStamp) {
        String sql = """
                SELECT id, lat, lon, alt, velocity, angular_velocity, type, status, timestamp
                FROM %s
                WHERE id = ?
                  AND timestamp BETWEEN ? AND ?
                ORDER BY timestamp DESC
                """.formatted(tableName);

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapTarget(rs), String.valueOf(targetId), fromTimeStamp, toTimeStamp);
    }

    private Target mapTarget(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("timestamp");
        return Target.builder()
                .id(parseInteger(rs.getString("id")))
                .lat(rs.getObject("lat", Double.class))
                .lon(rs.getObject("lon", Double.class))
                .alt(rs.getObject("alt", Double.class))
                .velocity(rs.getObject("velocity", Double.class))
                .angularVelocity(rs.getObject("angular_velocity", Double.class))
                .type(rs.getString("type"))
                .status(rs.getString("status"))
                .timestamp(timestamp == null ? null : timestamp.toInstant().toEpochMilli())
                .build();
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Integer.parseInt(value);
    }
}
