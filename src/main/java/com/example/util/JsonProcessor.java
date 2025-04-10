package com.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.model.Movie;

public class JsonProcessor {
    private static final Logger logger = LoggerFactory.getLogger(JsonProcessor.class);
    private final ObjectMapper objectMapper;

    public JsonProcessor() {
        this.objectMapper = new ObjectMapper();
    }

    public MovieData processJsonContent(String jsonContent) {
        try {
            // Parse the JSON into a Movie object
            Movie movie = objectMapper.readValue(jsonContent, Movie.class);
            
            // Create a new JSON object without the movieId
            ObjectNode movieData = objectMapper.createObjectNode();
            movieData.put("title", movie.getTitle());
            movieData.put("yearOfRelease", movie.getYearOfRelease());
            
            // Process watchedBy list
            ArrayNode watchedByNode = movieData.putArray("watchedBy");
            if (movie.getWatchedBy() != null) {
                for (Movie.WatchedBy watchedBy : movie.getWatchedBy()) {
                    ObjectNode watchedByObject = objectMapper.createObjectNode();
                    watchedByObject.put("customer-id", watchedBy.getCustomerId());
                    watchedByObject.put("movie-id", watchedBy.getMovieId());
                    watchedByObject.put("rating", watchedBy.getRating());
                    watchedByObject.put("date", watchedBy.getDate());
                    watchedByNode.add(watchedByObject);
                }
            }

            return new MovieData(
                movie.getMovieId(),
                objectMapper.writeValueAsString(movieData)
            );
        } catch (Exception e) {
            logger.error("Error processing JSON content: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process JSON content", e);
        }
    }

    public static class MovieData {
        private final String key;
        private final String value;

        public MovieData(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() { return key; }
        public String getValue() { return value; }
    }
}
