package com.ibrahimekinci.barcrowd.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;
import java.util.Map;

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

    // TypeConverters required in AppDatabase for Map
    private Map<String, String> openingHours;

    // Cached Stats - New Fields
    private String lastLiveUpdateCrowdLevel;
    private String lastLiveUpdateWaitTime;
    private String lastLiveUpdateAgeRange;
    private Date lastLiveUpdateCreatedAt;
    private boolean showOnHomePage;

    private Date updatedAt;
    private Date createdAt;

    public VenueEntity() {
    }

    @NonNull
    public String getVenueId() { return venueId; }
    public void setVenueId(@NonNull String venueId) { this.venueId = venueId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Map<String, String> getOpeningHours() { return openingHours; }
    public void setOpeningHours(Map<String, String> openingHours) { this.openingHours = openingHours; }

    public String getLastLiveUpdateCrowdLevel() { return lastLiveUpdateCrowdLevel; }
    public void setLastLiveUpdateCrowdLevel(String lastLiveUpdateCrowdLevel) { this.lastLiveUpdateCrowdLevel = lastLiveUpdateCrowdLevel; }

    public String getLastLiveUpdateWaitTime() { return lastLiveUpdateWaitTime; }
    public void setLastLiveUpdateWaitTime(String lastLiveUpdateWaitTime) { this.lastLiveUpdateWaitTime = lastLiveUpdateWaitTime; }

    public String getLastLiveUpdateAgeRange() { return lastLiveUpdateAgeRange; }
    public void setLastLiveUpdateAgeRange(String lastLiveUpdateAgeRange) { this.lastLiveUpdateAgeRange = lastLiveUpdateAgeRange; }

    public Date getLastLiveUpdateCreatedAt() { return lastLiveUpdateCreatedAt; }
    public void setLastLiveUpdateCreatedAt(Date lastLiveUpdateCreatedAt) { this.lastLiveUpdateCreatedAt = lastLiveUpdateCreatedAt; }

    public boolean isShowOnHomePage() { return showOnHomePage; }
    public void setShowOnHomePage(boolean showOnHomePage) { this.showOnHomePage = showOnHomePage; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}