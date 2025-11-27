package com.ibrahimekinci.barcrowd.domain.model;

import androidx.annotation.NonNull;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Venue implements Serializable {

    private String venueId;

    @NonNull
    private String name = "";

    @NonNull
    private String type = "";

    @NonNull
    private String logoUrl = "";

    @NonNull
    private String address = "";

    private double latitude;
    private double longitude;

    @NonNull
    private Map<String, String> openingHours = new HashMap<>();

    private String lastLiveUpdateCrowdLevel = "Medium";
    private String lastLiveUpdateWaitTime = "15–30";
    private String lastLiveUpdateAgeRange = "25–30";
    private Timestamp lastLiveUpdateCreatedAt = null;
    private boolean showOnHomePage = true;

    @ServerTimestamp
    private Timestamp updatedAt;

    @ServerTimestamp
    private Timestamp createdAt;

    public Venue() {
    }

    public String getVenueId() { return venueId; }
    public void setVenueId(String venueId) { this.venueId = venueId; }

    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    @NonNull
    public String getType() { return type; }
    public void setType(@NonNull String type) { this.type = type; }

    @NonNull
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(@NonNull String logoUrl) { this.logoUrl = logoUrl; }

    @NonNull
    public String getAddress() { return address; }
    public void setAddress(@NonNull String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    @NonNull
    public Map<String, String> getOpeningHours() { return openingHours; }
    public void setOpeningHours(@NonNull Map<String, String> openingHours) { this.openingHours = openingHours; }

    public String getLastLiveUpdateCrowdLevel() { return lastLiveUpdateCrowdLevel; }
    public void setLastLiveUpdateCrowdLevel(String lastLiveUpdateCrowdLevel) { this.lastLiveUpdateCrowdLevel = lastLiveUpdateCrowdLevel; }

    public String getLastLiveUpdateWaitTime() { return lastLiveUpdateWaitTime; }
    public void setLastLiveUpdateWaitTime(String lastLiveUpdateWaitTime) { this.lastLiveUpdateWaitTime = lastLiveUpdateWaitTime; }

    public String getLastLiveUpdateAgeRange() { return lastLiveUpdateAgeRange; }
    public void setLastLiveUpdateAgeRange(String lastLiveUpdateAgeRange) { this.lastLiveUpdateAgeRange = lastLiveUpdateAgeRange; }

    public Timestamp getLastLiveUpdateCreatedAt() { return lastLiveUpdateCreatedAt; }
    public void setLastLiveUpdateCreatedAt(Timestamp lastLiveUpdateCreatedAt) { this.lastLiveUpdateCreatedAt = lastLiveUpdateCreatedAt; }

    public boolean isShowOnHomePage() { return showOnHomePage; }
    public void setShowOnHomePage(boolean showOnHomePage) { this.showOnHomePage = showOnHomePage; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getImageUrl() { return logoUrl; }
    public void setImageUrl(String imageUrl) { this.logoUrl = imageUrl; }
}