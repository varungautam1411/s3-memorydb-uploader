package com.example.service;

import com.example.config.AppConfig;
import com.example.util.JsonProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import javax.net.ssl.SSLSocketFactory;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MemoryDBService {
    private static final Logger logger = LoggerFactory.getLogger(MemoryDBService.class);
    private final JedisCluster jedisCluster;
    private final JsonProcessor jsonProcessor;

    public MemoryDBService(AppConfig config) {
        this.jedisCluster = createClusterConnection(config);
        this.jsonProcessor = new JsonProcessor();
    }

    private JedisCluster createClusterConnection(AppConfig config) {
        Set<String> nodes = new HashSet<>();
        nodes.add(config.getMemoryDbEndpoint() + ":" + config.getMemoryDbPort());

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(32);
        poolConfig.setMaxIdle(16);

        return new JedisCluster(
                nodes,
                2000, // connection timeout
                2000, // socket timeout
                5,    // max attempts
                config.getMemoryDbAuthToken(),
                "default",
                poolConfig,
                true,
                null,
                null,
                null,
                SSLSocketFactory.getDefault()
        );
    }

    public void processAndStoreJson(String jsonContent) {
        try {
            Map<String, String> keyValuePairs = jsonProcessor.extractKeyValuePairs(jsonContent);
            keyValuePairs.forEach((key, value) -> {
                try {
                    jedisCluster.set(key, value);
                    logger.debug("Stored key: {}", key);
                } catch (Exception e) {
                    logger.error("Error storing key-value pair: {}", key, e);
                }
            });
        } catch (Exception e) {
            logger.error("Error processing JSON content", e);
            throw new RuntimeException("Failed to process JSON", e);
        }
    }
}

