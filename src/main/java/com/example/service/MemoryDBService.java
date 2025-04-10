package com.example.service;

import com.example.config.AppConfig;
import com.example.util.JsonProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.ConnectionPoolConfig;

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
        try {
            Set<HostAndPort> nodes = new HashSet<>();
            nodes.add(new HostAndPort(config.getMemoryDbEndpoint(), config.getMemoryDbPort()));

            ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
            poolConfig.setMaxTotal(32);
            poolConfig.setMaxIdle(16);

            // Using the correct constructor for JedisCluster
            return new JedisCluster(
                nodes,                          // Set of nodes
                2000,                          // Connection timeout
                2000,                          // Socket timeout
                5,                             // Max attempts
                config.getMemoryDbAuthToken(), // Auth token
                poolConfig                     // Pool config
            );
        } catch (Exception e) {
            logger.error("Error creating MemoryDB connection", e);
            throw new RuntimeException("Failed to create MemoryDB connection", e);
        }
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

    public void close() {
        if (jedisCluster != null) {
            try {
                jedisCluster.close();
            } catch (Exception e) {
                logger.error("Error closing JedisCluster", e);
            }
        }
    }
}
