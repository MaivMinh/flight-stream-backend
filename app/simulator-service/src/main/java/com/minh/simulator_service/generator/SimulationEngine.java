package com.minh.simulator_service.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.common.kafka.KafkaTopics;
import com.minh.common.model.Target;
import com.minh.simulator_service.config.SimulationTarget;
import com.minh.simulator_service.config.SimulatorConfig;
import com.minh.simulator_service.enums.TargetStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SimulationEngine {
    private final List<SimulationTarget> simulationTargets;
    private final ExecutorService workerPool;
    private final KafkaTemplate<String, Target> producer;
    private final ObjectMapper objectMapper;

    public SimulationEngine(KafkaTemplate<String, Target> producer, ObjectMapper objectMapper) {
        this.producer = producer;
        this.objectMapper = objectMapper;

        String data = """
                 {
                   "scenarios": [
                     {
                       "name": "ALLY_CIRCLE",
                       "type": "ALLY",
                       "targetCount": 2000,
                       "trajectoryConfig": {
                         "type": "CIRCLE",
                         "center": {
                           "lat": 21.0285,
                           "lon": 105.8542,
                           "alt": 1000
                         },
                         "radius": 1000,
                         "angularVelocityRange": {
                             "min": 0.01,\s
                             "max": 0.04
                         }
                       }
                     },
                     {
                       "name": "ALLY_STRAIGHT",
                       "type": "ALLY",
                       "targetCount": 1000,
                       "trajectoryConfig": {
                         "type": "STRAIGHT",
                         "start": {
                           "lat": 21.01,
                           "lon": 105.80,
                           "alt": 1000
                         },
                         "end": {
                           "lat": 21.05,
                           "lon": 105.90,
                           "alt": 1000
                         }
                       },
                       "velocityRange": {
                         "min": 10,
                         "max": 50
                       }
                     },
                     {
                       "name": "ALLY_ZIGZAG",
                       "type": "ALLY",
                       "targetCount": 1000,
                       "trajectoryConfig": {
                         "type": "ZIGZAG",
                         "points": [
                           {
                             "lat": 21.01,
                             "lon": 105.80,
                             "alt": 1000
                           },
                           {
                             "lat": 21.03,
                             "lon": 105.85,
                             "alt": 1000
                           },
                           {
                             "lat": 21.00,
                             "lon": 105.90,
                             "alt": 1000
                           }
                         ]
                       },
                       "velocityRange": {
                         "min": 10,
                         "max": 50
                       }
                     },
                     {
                       "name": "ENEMY_CIRCLE",
                       "type": "ENEMY",
                       "targetCount": 1000,
                       "trajectoryConfig": {
                         "type": "CIRCLE",
                         "center": {
                           "lat": 21.04,
                           "lon": 105.88,
                           "alt": 1000
                         },
                         "radius": 15000,
                         "angularVelocityRange": {
                             "min": 0.01,\s
                             "max": 0.04
                         }
                       }
                     },
                     {
                       "name": "ENEMY_STRAIGHT",
                       "type": "ENEMY",
                       "targetCount": 1000,
                       "trajectoryConfig": {
                         "type": "STRAIGHT",
                         "start": {
                           "lat": 21.00,
                           "lon": 105.75,
                           "alt": 1000
                         },
                         "end": {
                           "lat": 21.06,
                           "lon": 105.95,
                           "alt": 1000
                         }
                       },
                       "velocityRange": {
                         "min": 10,
                         "max": 50
                       }
                     },
                     {
                       "name": "ENEMY_ZIGZAG",
                       "type": "ENEMY",
                       "targetCount": 1000,
                       "trajectoryConfig": {
                         "type": "ZIGZAG",
                         "points": [
                           {
                             "lat": 21.02,
                             "lon": 105.82,
                             "alt": 1000
                           },
                           {
                             "lat": 21.05,
                             "lon": 105.87,
                             "alt": 1000
                           },
                           {
                             "lat": 21.01,
                             "lon": 105.92,
                             "alt": 1000
                           }
                         ]
                       },
                       "velocityRange": {
                         "min": 10,
                         "max": 50
                       }
                     },
                     {
                       "name": "UNDEFINED_CIRCLE",
                       "type": "UNDEFINED",
                       "targetCount": 1000,
                       "trajectoryConfig": {
                         "type": "CIRCLE",
                         "center": {
                           "lat": 21.03,
                           "lon": 105.86,
                           "alt": 1000
                         },
                         "radius": 2000,
                         "angularVelocityRange": {
                             "min": 0.01,\s
                             "max": 0.04
                         }
                       }
                     },
                     {
                       "name": "UNDEFINED_STRAIGHT",
                       "type": "UNDEFINED",
                       "targetCount": 1000,
                       "trajectoryConfig": {
                         "type": "STRAIGHT",
                         "start": {
                           "lat": 21.02,
                           "lon": 105.78,
                           "alt": 1000
                         },
                         "end": {
                           "lat": 21.07,
                           "lon": 105.93,
                           "alt": 1000
                         }
                       },
                       "velocityRange": {
                         "min": 10,
                         "max": 50
                       }
                     },
                     {
                       "name": "UNDEFINED_ZIGZAG",
                       "type": "UNDEFINED",
                       "targetCount": 1000,
                       "trajectoryConfig": {
                         "type": "ZIGZAG",
                         "points": [
                           {
                             "lat": 21.01,
                             "lon": 105.79,
                             "alt": 1000
                           },
                           {
                             "lat": 21.04,
                             "lon": 105.84,
                             "alt": 1000
                           },
                           {
                             "lat": 21.02,
                             "lon": 105.91,
                             "alt": 1000
                           }
                         ]
                       },
                       "velocityRange": {
                         "min": 10,
                         "max": 50
                       }
                     }
                   ]
                 }
                \s""";

        List<SimulationTarget> parsedTargets = new ArrayList<>();

        try {
            SimulatorConfig config = objectMapper.readValue(data, SimulatorConfig.class);
            parsedTargets = TargetGenerator.generateTargets(config);
        } catch (Exception e) {
            log.error("Failed to parse simulator configuration: ", e);
        }
        this.workerPool = Executors.newFixedThreadPool(8);
        this.simulationTargets = parsedTargets;
    }


    public void start(long intervalMs, int shardNum) {
        List<List<SimulationTarget>> shards = sharding(simulationTargets, shardNum);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            for (List<SimulationTarget> shard : shards) {
                workerPool.submit(() -> {
                    processShard(shard, intervalMs);
                });
            }
        }, 0, intervalMs, TimeUnit.MILLISECONDS);
    }

    private void processShard(List<SimulationTarget> shard, long intervalMs) {
        for (SimulationTarget simulation : shard) {
            Target target = simulation.getTarget();
            if (!TargetStatus.COMPLETED.name().equals(target.status)) {
                simulation.getTrajectory().update(target, intervalMs);
                target.setTimestamp(System.currentTimeMillis());
                producer.send(KafkaTopics.SIMULATION, target.getId().toString(), target);
            }
        }
    }

    private List<List<SimulationTarget>> sharding(List<SimulationTarget> simulationTargets, Integer threads) {
        List<List<SimulationTarget>> shards = new ArrayList<>();
        int size = simulationTargets.size();
        int segment = (size + threads - 1) / threads;
        for (int i = 0; i < size; i += segment) {
            shards.add(simulationTargets.subList(i, Math.min(i + segment, size)));
        }
        return shards;
    }

    public void stop() {
        workerPool.shutdown();
        try {
            if (!workerPool.awaitTermination(5, TimeUnit.SECONDS)) {
                List<Runnable> dropped = workerPool.shutdownNow();
                log.warn("Worker pool didn't terminate, cancelled {} tasks", dropped.size());
                if (!workerPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.error("Worker pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            workerPool.shutdownNow();
        }
    }
}
