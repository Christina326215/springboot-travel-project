package com.sunnyhsu.springboottravelproject.model;

import java.time.LocalDate;

public class Destination {
    private String name;
    private String description;
    private String imageUrl;
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;

    // Constructors
    public Destination(String name, String description, String imageUrl, LocalDate createdDate, LocalDate lastModifiedDate) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
