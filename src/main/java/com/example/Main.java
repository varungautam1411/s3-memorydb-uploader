package com.example;

import com.example.config.AppConfig;
import com.example.service.S3Service;
import com.example.service.MemoryDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        MemoryDBService memoryDBService = null;
        try {
            logger.info("Starting application...");
            
            AppConfig config = AppConfig.getInstance();
            S3Service s3Service = new S3Service(config);
            memoryDBService = new MemoryDBService(config);

            if (!memoryDBService.testConnection()) {
                logger.error("Failed to connect to MemoryDB");
                System.exit(1);
            }

            logger.info("Processing files from S3...");
            s3Service.listJsonFiles().forEach(jsonFile -> {
                try {
                    String jsonContent = s3Service.readJsonContent(jsonFile);
                    if (jsonContent != null && !jsonContent.isEmpty()) {
                        memoryDBService.processAndStoreJson(jsonContent);
                        logger.info("Processed file: {}", jsonFile);
                    }
                } catch (Exception e) {
                    logger.error("Error processing file {}: {}", jsonFile, e.getMessage(), e);
                }
            });

            logger.info("Processing completed successfully");

        } catch (Exception e) {
            logger.error("Application error: {}", e.getMessage(), e);
            System.exit(1);
        } finally {
            if (memoryDBService != null) {
                memoryDBService.close();
            }
        }
    }

    // Test method
    private static void testWithSampleData(MemoryDBService memoryDBService) {
        String sampleJson = "{"
            + "\"watchedBy\": [14910,14911,14912],"
            + "\"title\": \"What the Do We Know!?\","
            + "\"movieId\": \"1025045\","
            + "\"yearOfRelease\": 2004"
            + "}";
        
        memoryDBService.processAndStoreJson(sampleJson);
        String retrieved = memoryDBService.getMovie("1025045");
        logger.info("Retrieved data: {}", retrieved);
    }
}
