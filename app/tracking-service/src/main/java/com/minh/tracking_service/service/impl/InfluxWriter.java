package com.minh.tracking_service.service.impl;

import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.minh.common.model.Target;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxWriter {
    private final BufferServiceImpl bufferService;
    private final WriteApi writeApi;
    private static final int BATCH_SIZE = 2000;


    @PostConstruct
    public void start() {
        new Thread(this::runWriter).start();
    }

    private void runWriter() {
        while (true) {
            try {
                List<Target> events = bufferService.drainTo(BATCH_SIZE);
                if (events.isEmpty()) {
                    Thread.sleep(50);
                    continue;
                }
                List<Point> points = new ArrayList<>();
                for (Target e : events) {
                    Point p = Point
                            .measurement("positions")
                            .addTag("id", String.valueOf(e.getId()))
                            .addField("lat", e.getLat())
                            .addField("lon", e.getLon())
                            .addField("alt", e.getAlt())
                            .addField("status", e.getStatus())
                            .addField("type", e.getType())
                            .addField("velocity", e.getVelocity())
                            .addField("timestamp", e.getTimestamp())
                            .time(e.getTimestamp(), WritePrecision.MS);
                    points.add(p);
                }
                writeWithRetry(points);
                log.info("Wrote batch of {} points to InfluxDB", points.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeWithRetry(List<Point> points) {
        int retry = 0;
        while (retry < 3) {
            try {
                writeApi.writePoints(points);
                return;
            } catch (Exception e) {
                retry++;
                try {
                    Thread.sleep(100L * retry);
                } catch (InterruptedException ignored) {
                }
            }
        }

        log.error("Lỗi khi thực hiện ghi vào InfluxDB sau 3 lần thử: {}", points.size());
    }
}