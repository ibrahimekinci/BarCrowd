package com.ibrahimekinci.barcrowd.data.local;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Room entity for Venue model, storing static location info.
 */
@Entity(tableName = "venues")
public class VenueEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String description;

    public VenueEntity() {}
    // Parameterized constructor (mark as @Ignore)
    @Ignore
    public VenueEntity(@NonNull String id, String name, String address, double latitude, double longitude, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    // Getters and setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
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
}