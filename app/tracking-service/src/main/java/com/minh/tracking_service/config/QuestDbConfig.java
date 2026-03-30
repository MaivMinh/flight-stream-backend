package com.minh.tracking_service.config;

import io.questdb.client.Sender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class QuestDbConfig {
    @Bean
    public DataSource questDbDataSource(
            @Value("${questdb.pg.host}") String host,
            @Value("${questdb.pg.port}") int port,
            @Value("${questdb.database}") String database,
            @Value("${questdb.username}") String username,
            @Value("${questdb.password}") String password
    ) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public JdbcTemplate questDbJdbcTemplate(DataSource questDbDataSource) {
        return new JdbcTemplate(questDbDataSource);
    }

    @Bean(destroyMethod = "close")
    public Sender questDbSender(@Value("${questdb.sender-config}") String senderConfig) {
        return Sender.fromConfig(senderConfig);
    }
}
