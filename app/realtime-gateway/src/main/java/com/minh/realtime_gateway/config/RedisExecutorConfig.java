package com.minh.realtime_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class RedisExecutorConfig {

    @Bean(destroyMethod = "close")
    public ExecutorService redisExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
