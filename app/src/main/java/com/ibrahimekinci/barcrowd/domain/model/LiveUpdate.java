package com.ibrahimekinci.barcrowd.domain.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;

public class LiveUpdate implements Serializable {

    private String updateId;
    private String venueId;
    private String userId;

    // Denormalized User Info (Optional but good for performance)
    private String userName;
    private String userPhotoUrl;

    // Denormalized Venue Info (For Feed display)
    private String venueName;
    private String venueType;
    private String venueLogoUrl;

    // Stats
    private String crowdLevel;
    private String waitTime;
    private String ageRange;

    private String description;
    private String mediaUrl;       // Video URL
    private String thumbnailUrl;   // Thumbnail URL

    @PropertyName("isDeleted")
    private boolean isDeleted;     // Soft Delete flag

    @ServerTimestamp
    private Timestamp createdAt;

    private Timestamp deletedAt;
    private Timestamp updatedAt;

    public LiveUpdate() {
        // Firestore requires empty constructor
    }

    // --- Getters and Setters ---

    public String getUpdateId() { return updateId; }
    public void setUpdateId(String updateId) { this.updateId = updateId; }

    public String getVenueId() { return venueId; }
    public void setVenueId(String venueId) { this.venueId = venueId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserPhotoUrl() { return userPhotoUrl; }
    public void setUserPhotoUrl(String userPhotoUrl) { this.userPhotoUrl = userPhotoUrl; }

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }

    public String getVenueType() { return venueType; }
    public void setVenueType(String venueType) { this.venueType = venueType; }

    public String getVenueLogoUrl() { return venueLogoUrl; }
    public void setVenueLogoUrl(String venueLogoUrl) { this.venueLogoUrl = venueLogoUrl; }

    public String getCrowdLevel() { return crowdLevel; }
    public void setCrowdLevel(String crowdLevel) { this.crowdLevel = crowdLevel; }

    public String getWaitTime() { return waitTime; }
    public void setWaitTime(String waitTime) { this.waitTime = waitTime; }

    public String getAgeRange() { return ageRange; }
    public void setAgeRange(String ageRange) { this.ageRange = ageRange; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    @PropertyName("isDeleted")
    public boolean getIsDeleted() { return isDeleted; }

    @PropertyName("isDeleted")
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Timestamp deletedAt) { this.deletedAt = deletedAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}