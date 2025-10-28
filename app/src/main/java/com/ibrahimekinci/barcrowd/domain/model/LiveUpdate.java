package com.ibrahimekinci.barcrowd.domain.model;

import java.io.Serializable;

public class LiveUpdate implements Serializable {
    private String id;
    private String venueId;
    private String userId;
    private long timestamp;
    private String crowdLevel;
    private int waitTime;
    private String ageRange;
    private String photoUrl;
    private String videoUrl;
    private double latitude;
    private double longitude;

    public LiveUpdate() {}

    public LiveUpdate(String id, String venueId, String userId, long timestamp, String crowdLevel, int waitTime, String ageRange, String photoUrl, String videoUrl, double latitude, double longitude) {
        this.id = id;
        this.venueId = venueId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.crowdLevel = crowdLevel;
        this.waitTime = waitTime;
        this.ageRange = ageRange;
        this.photoUrl = photoUrl;
        this.videoUrl = videoUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getVenueId() { return venueId; }
    public void setVenueId(String venueId) { this.venueId = venueId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getCrowdLevel() { return crowdLevel; }
    public void setCrowdLevel(String crowdLevel) { this.crowdLevel = crowdLevel; }
    public int getWaitTime() { return waitTime; }
    public void setWaitTime(int waitTime) { this.waitTime = waitTime; }
    public String getAgeRange() { return ageRange; }
    public void setAgeRange(String ageRange) { this.ageRange = ageRange; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    // Business method example
    public boolean isComplete() {
        return crowdLevel != null && !crowdLevel.isEmpty() && waitTime >= 0;
    }
}