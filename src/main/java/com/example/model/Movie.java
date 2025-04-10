package com.example.model;

import java.util.List;

public class Movie {
    private List<Integer> watchedBy;
    private String title;
    private String movieId;
    private int yearOfRelease;

    // Getters and Setters
    public List<Integer> getWatchedBy() { return watchedBy; }
    public void setWatchedBy(List<Integer> watchedBy) { this.watchedBy = watchedBy; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }
    public int getYearOfRelease() { return yearOfRelease; }
    public void setYearOfRelease(int yearOfRelease) { this.yearOfRelease = yearOfRelease; }
}

