package com.ibrahimekinci.barcrowd.data.local;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Room entity for a Venue, acting as a local cache.
 * Uses @Embedded for opening hours and java.util.Date for timestamps.
 */
@Entity(tableName = "venues")
public class VenueEntity {

    @PrimaryKey
    @NonNull
    private String venueId;

    private String name;
    private String type;
    private String logoUrl;
    private String address;
    private double latitude;
    private double longitude;

    @Embedded
    private OpeningHoursEmbedded openingHours;

    // Cached Stats
    private String averageCrowdLevel;
    private String lastCrowdLevel;
    private String averageWaitTime;
    private String lastWaitTime;
    private Date crowdLevelUpdatedAt; // Use java.util.Date
    private String mostPopulousAge;
    private boolean showOnHomePage;

    // Timestamps
    private Date updatedAt; // Use java.util.Date
    private Date createdAt; // Use java.util.Date

    public VenueEntity() {
    }

    // --- Getters ---
    @NonNull
    public String getVenueId() {
        return venueId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public OpeningHoursEmbedded getOpeningHours() {
        return openingHours;
    }

    public String getAverageCrowdLevel() {
        return averageCrowdLevel;
    }

    public String getLastCrowdLevel() {
        return lastCrowdLevel;
    }

    public String getAverageWaitTime() {
        return averageWaitTime;
    }

    public String getLastWaitTime() {
        return lastWaitTime;
    }

    public Date getCrowdLevelUpdatedAt() {
        return crowdLevelUpdatedAt;
    }

    public String getMostPopulousAge() {
        return mostPopulousAge;
    }

    public boolean isShowOnHomePage() {
        return showOnHomePage;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    // --- Setters ---
    public void setVenueId(@NonNull String venueId) {
        this.venueId = venueId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setOpeningHours(OpeningHoursEmbedded openingHours) {
        this.openingHours = openingHours;
    }

    public void setAverageCrowdLevel(String averageCrowdLevel) {
        this.averageCrowdLevel = averageCrowdLevel;
    }

    public void setLastCrowdLevel(String lastCrowdLevel) {
        this.lastCrowdLevel = lastCrowdLevel;
    }

    public void setAverageWaitTime(String averageWaitTime) {
        this.averageWaitTime = averageWaitTime;
    }

    public void setLastWaitTime(String lastWaitTime) {
        this.lastWaitTime = lastWaitTime;
    }

    public void setCrowdLevelUpdatedAt(Date crowdLevelUpdatedAt) {
        this.crowdLevelUpdatedAt = crowdLevelUpdatedAt;
    }

    public void setMostPopulousAge(String mostPopulousAge) {
        this.mostPopulousAge = mostPopulousAge;
    }

    public void setShowOnHomePage(boolean showOnHomePage) {
        this.showOnHomePage = showOnHomePage;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}