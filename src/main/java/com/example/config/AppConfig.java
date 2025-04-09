package com.example.config;

import java.util.Properties;

public class AppConfig {
    private static AppConfig instance;
    private final Properties properties;

    private AppConfig() {
        this.properties = ConfigLoader.loadProperties();
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public String getAwsRegion() {
        return properties.getProperty("aws.region");
    }

    public String getS3BucketName() {
        return properties.getProperty("s3.bucket.name");
    }

    public String getS3Prefix() {
        return properties.getProperty("s3.prefix");
    }

    public String getMemoryDbEndpoint() {
        return properties.getProperty("memorydb.endpoint");
    }

    public int getMemoryDbPort() {
        return Integer.parseInt(properties.getProperty("memorydb.port", "6379"));
    }

    public String getMemoryDbAuthToken() {
        return properties.getProperty("memorydb.auth.token");
    }
}

    
