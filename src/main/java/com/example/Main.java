package com.example;

import com.example.service.S3Service;
import com.example.service.MemoryDBService;
import com.example.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            AppConfig config = AppConfig.getInstance();
            S3Service s3Service = new S3Service(config);
            MemoryDBService memoryDBService = new MemoryDBService(config);

            // Process files
            s3Service.listJsonFiles().forEach(jsonFile -> {
                String content = s3Service.readJsonContent(jsonFile);
                memoryDBService.processAndStoreJson(content);
            });

            logger.info("Processing completed successfully");
        } catch (Exception e) {
            logger.error("Error in main process", e);
            System.exit(1);
        }
    }
}

    
