package com.example.service;

import com.example.config.AppConfig;
import com.example.util.JsonProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.ConnectionPoolConfig;
import com.example.util.JsonProcessor.MovieData;

import java.util.HashSet;
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
            nodes.add(new HostAndPort(
                config.getMemoryDbEndpoint(),
                config.getMemoryDbPort()
            ));

            ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
            poolConfig.setMaxTotal(32);
            poolConfig.setMaxIdle(16);

            return new JedisCluster(nodes, 2000, 2000, 5, poolConfig);
        } catch (Exception e) {
            logger.error("Error creating MemoryDB connection: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create MemoryDB connection", e);
        }
    }

    public void processAndStoreJson(String jsonContent) {
        try {
            MovieData movieData = jsonProcessor.processJsonContent(jsonContent);
            jedisCluster.set(movieData.getKey(), movieData.getValue());
            logger.info("Stored movie data for ID: {}", movieData.getKey());
        } catch (Exception e) {
            logger.error("Error processing and storing JSON content: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process and store JSON", e);
        }
    }

    public String getMovie(String movieId) {
        try {
            return jedisCluster.get(movieId);
        } catch (Exception e) {
            logger.error("Error retrieving movie {}: {}", movieId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve movie", e);
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

    
