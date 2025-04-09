package com.example.util;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JsonProcessor {
    private static final Logger logger = LoggerFactory.getLogger(JsonProcessor.class);

    public Map<String, String> extractKeyValuePairs(String jsonContent) {
        Map<String, String> pairs = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);
            for (String key : jsonObject.keySet()) {
                pairs.put(key, jsonObject.get(key).toString());
            }
        } catch (Exception e) {
            logger.error("Error processing JSON content", e);
            throw new RuntimeException("Failed to process JSON", e);
        }
        return pairs;
    }
}

    
