package com.example.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {
    private List<WatchedBy> watchedBy;
    private String title;
    private String movieId;
    private int yearOfRelease;

    public List<WatchedBy> getWatchedBy() { return watchedBy; }
    public void setWatchedBy(List<WatchedBy> watchedBy) { this.watchedBy = watchedBy; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }
    public int getYearOfRelease() { return yearOfRelease; }
    public void setYearOfRelease(int yearOfRelease) { this.yearOfRelease = yearOfRelease; }

    public static class WatchedBy {
        @JsonProperty("customer-id")
        private String customerId;
        @JsonProperty("movie-id")
        private String movieId;
        private int rating;
        private String date;

        // Getters
        @JsonProperty("customer-id")
        public String getCustomerId() { return customerId; }
        @JsonProperty("movie-id")
        public String getMovieId() { return movieId; }
        public int getRating() { return rating; }
        public String getDate() { return date; }

        // Setters
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        public void setMovieId(String movieId) { this.movieId = movieId; }
        public void setRating(int rating) { this.rating = rating; }
        public void setDate(String date) { this.date = date; }
    }
}
