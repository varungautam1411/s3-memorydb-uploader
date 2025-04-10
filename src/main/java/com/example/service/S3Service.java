package com.example.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class S3Service {
    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);
    private final AmazonS3 s3Client;
    private final String bucketName;
    private final String prefix;

    public S3Service(AppConfig config) {
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(config.getAwsRegion())
                .build();
        this.bucketName = config.getS3BucketName();
        this.prefix = config.getS3Prefix();
    }

    public List<String> listJsonFiles() {
        List<String> jsonFiles = new ArrayList<>();
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(prefix);

        ListObjectsV2Result result;
        do {
            result = s3Client.listObjectsV2(request);
            jsonFiles.addAll(result.getObjectSummaries().stream()
                    .map(obj -> obj.getKey())
                    .filter(key -> key.endsWith(".json"))
                    .collect(Collectors.toList()));
            request.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());

        return jsonFiles;
    }

    public String readJsonContent(String key) {
        try {
            S3ObjectInputStream inputStream = s3Client.getObject(bucketName, key)
                    .getObjectContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            return content.toString();
        } catch (Exception e) {
            logger.error("Error reading S3 file: {}", key, e);
            throw new RuntimeException("Failed to read S3 file", e);
        }
    }
}
