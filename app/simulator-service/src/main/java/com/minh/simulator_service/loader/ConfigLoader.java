package com.minh.simulator_service.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.simulator_service.config.SimulatorConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigLoader {

    public static SimulatorConfig loadConfig() {
        ObjectMapper mapper = new ObjectMapper();
        try (java.io.InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream("simulator_config.json")) {
            if (is == null) {
                throw new RuntimeException("Config file not found on classpath: simulator_config.json");
            }
            return mapper.readValue(is, SimulatorConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Failed to load simulator configuration: ", e);
        }
        return null;
    }
}