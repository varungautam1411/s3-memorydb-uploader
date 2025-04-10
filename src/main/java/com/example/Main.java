package com.example;

import com.example.config.AppConfig;
import com.example.service.S3Service;
import com.example.service.MemoryDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        final MemoryDBService memoryDBService = initializeServices();
        if (memoryDBService == null) {
            logger.error("Failed to initialize services");
            System.exit(1);
        }

        try {
            processFiles(memoryDBService);
        } finally {
            closeServices(memoryDBService);
        }
    }

    private static MemoryDBService initializeServices() {
        try {
            logger.info("Starting application...");
            
            AppConfig config = AppConfig.getInstance();
            MemoryDBService memoryDBService = new MemoryDBService(config);

            

            logger.info("Services initialized successfully");
            return memoryDBService;

        } catch (Exception e) {
            logger.error("Error initializing services: {}", e.getMessage(), e);
            return null;
        }
    }

    private static void processFiles(final MemoryDBService memoryDBService) {
        try {
            AppConfig config = AppConfig.getInstance();
            S3Service s3Service = new S3Service(config);

            logger.info("Processing files from S3...");
            
            s3Service.listJsonFiles().forEach(jsonFile -> {
                try {
                    processFile(jsonFile, s3Service, memoryDBService);
                } catch (Exception e) {
                    logger.error("Error processing file {}: {}", jsonFile, e.getMessage(), e);
                }
            });

            logger.info("Processing completed successfully");

        } catch (Exception e) {
            logger.error("Error during file processing: {}", e.getMessage(), e);
            throw new RuntimeException("File processing failed", e);
        }
    }

    private static void processFile(String jsonFile, S3Service s3Service, MemoryDBService memoryDBService) {
        try {
            String jsonContent = s3Service.readJsonContent(jsonFile);
            if (jsonContent != null && !jsonContent.isEmpty()) {
                memoryDBService.processAndStoreJson(jsonContent);
                logger.info("Processed file: {}", jsonFile);
            } else {
                logger.warn("Empty or null content for file: {}", jsonFile);
            }
        } catch (Exception e) {
            logger.error("Error processing file {}: {}", jsonFile, e.getMessage(), e);
            throw new RuntimeException("Failed to process file: " + jsonFile, e);
        }
    }

    private static void closeServices(MemoryDBService memoryDBService) {
        if (memoryDBService != null) {
            try {
                memoryDBService.close();
                logger.info("Services closed successfully");
            } catch (Exception e) {
                logger.error("Error closing services: {}", e.getMessage(), e);
            }
        }
    }

    // Test method
    private static void testWithSampleData(MemoryDBService memoryDBService) {
        try {
            String sampleJson = "{"
                + "\"watchedBy\": [14910,14911,14912],"
                + "\"title\": \"What the Do We Know!?\","
                + "\"movieId\": \"1025045\","
                + "\"yearOfRelease\": 2004"
                + "}";
            
            memoryDBService.processAndStoreJson(sampleJson);
            String retrieved = memoryDBService.getMovie("1025045");
            logger.info("Retrieved data: {}", retrieved);
            
        } catch (Exception e) {
            logger.error("Error in test data processing: {}", e.getMessage(), e);
        }
    }

    // Add command line argument handling
    private static void handleCommandLineArgs(String[] args, MemoryDBService memoryDBService) {
        if (args != null && args.length > 0) {
            for (String arg : args) {
                switch (arg.toLowerCase()) {
                    case "--test":
                        logger.info("Running in test mode");
                        testWithSampleData(memoryDBService);
                        break;
                    case "--help":
                        printHelp();
                        break;
                    default:
                        logger.warn("Unknown argument: {}", arg);
                        break;
                }
            }
        }
    }

    private static void printHelp() {
        System.out.println("Usage: java -jar s3-memorydb-loader.jar [options]");
        System.out.println("Options:");
        System.out.println("  --test    Run with test data");
        System.out.println("  --help    Show this help message");
    }
}
