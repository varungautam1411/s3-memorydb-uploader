package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    public static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            
            if (input == null) {
                throw new RuntimeException("Unable to find application.properties");
            }
            
            properties.load(input);
        } catch (Exception e) {
            logger.error("Error loading configuration", e);
            throw new RuntimeException("Failed to load configuration", e);
        }
        return properties;
    }
}

  
