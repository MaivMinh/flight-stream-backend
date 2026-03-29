package com.minh.tracking_service.config;

import com.influxdb.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfig {
    @Value("${influxdb.token}")
    private String token;
    @Value("${influxdb.url}")
    private String url;
    @Value("${influxdb.org}")
    private String org;
    @Value("${influxdb.bucket}")
    private String bucket;

    @Bean
    public InfluxDBClient influxDBClient() {
        InfluxDBClientOptions clientOptions = InfluxDBClientOptions.builder()
                .url(url)
                .authenticateToken(token.toCharArray())
                .org(org)
                .bucket(bucket)
                .build();
        return InfluxDBClientFactory.create(clientOptions);
    }

    @Bean
    public WriteApi writeApi(InfluxDBClient client) {
        WriteOptions options = WriteOptions.builder()
                .batchSize(5000)
                .flushInterval(200)
                .bufferLimit(100_000)
                .build();
        return client.makeWriteApi(options);
    }
}
