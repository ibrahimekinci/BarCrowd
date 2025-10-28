package com.ibrahimekinci.barcrowd.data.local;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Room entity for LiveUpdate model, including sync status for offline handling.
 */
@Entity(tableName = "live_updates", indices = {@Index(value = {"venueId"})})
public class LiveUpdateEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String venueId;
    private String userId;
    private long timestamp;
    private String crowdLevel; // e.g., "low", "medium", "high"
    private int waitTime; // minutes
    private String ageRange; // e.g., "20-30"
    private String photoUrl;
    private String videoUrl;
    private double latitude;
    private double longitude;
    private boolean syncStatus; // false if pending sync

    public LiveUpdateEntity() {}
    // Parameterized constructor (mark as @Ignore)
    @Ignore
    public LiveUpdateEntity(@NonNull String id, String venueId, String userId, long timestamp, String crowdLevel, int waitTime, String ageRange, String photoUrl, String videoUrl, double latitude, double longitude, boolean syncStatus) {
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
        this.syncStatus = syncStatus;
    }

    // Getters and setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
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
    public boolean isSyncStatus() { return syncStatus; }
    public void setSyncStatus(boolean syncStatus) { this.syncStatus = syncStatus; }
}