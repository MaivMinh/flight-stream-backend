package com.minh.realtime_gateway.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class RedisListenerConfig {

    private final RedisConnectionFactory connectionFactory;
    private final RedisSubscriber consumer;
    private final ChannelTopic realtimeDataTopic;

    @Bean
    public RedisMessageListenerContainer realtimeDataTopicContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(consumer, realtimeDataTopic);

        return container;
    }
}
