package com.ibrahimekinci.barcrowd.domain.model;

import java.io.Serializable;

public class Venue implements Serializable {
    private String id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String description;

    public Venue() {}

    public Venue(String id, String name, String address, double latitude, double longitude, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Business method example
    public boolean hasLocation() {
        return latitude != 0.0 || longitude != 0.0;
    }
}