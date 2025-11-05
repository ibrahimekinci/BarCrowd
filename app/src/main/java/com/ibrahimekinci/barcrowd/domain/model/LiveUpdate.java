package com.ibrahimekinci.barcrowd.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

/**
 * Entity (POJO) for the /LiveUpdates collection.
 * Includes denormalized data (venueName, venueType, venueLogoUrl)
 * for efficient read queries.
 */
public class LiveUpdate implements Parcelable {

    @DocumentId
    private String updateId;

    @NonNull
    private String venueId;

    @NonNull
    private String userId;

    @NonNull
    private String crowdLevel;

    @NonNull
    private String waitTime;

    @NonNull
    private String ageRange;

    @NonNull
    private String mediaUrl;

    // --- Denormalized Venue Data ---
    private String venueName;    // NEW
    private String venueType;    // NEW
    private String venueLogoUrl; // NEW
    // --------------------------------

    private String description = "";
    private String thumbnailUrl;
    private boolean isDeleted = false;
    private Timestamp deletedAt = null;

    @ServerTimestamp
    private Timestamp updatedAt;

    @ServerTimestamp
    private Timestamp createdAt;

    // Public, no-argument constructor for Firestore
    public LiveUpdate() {
    }

    // --- Parcelable Implementation (Updated) ---

    protected LiveUpdate(Parcel in) {
        updateId = in.readString();
        venueId = in.readString();
        userId = in.readString();
        crowdLevel = in.readString();
        waitTime = in.readString();
        ageRange = in.readString();
        mediaUrl = in.readString();

        // Read new fields
        venueName = in.readString();
        venueType = in.readString();
        venueLogoUrl = in.readString();

        description = in.readString();
        thumbnailUrl = in.readString();
        isDeleted = in.readByte() != 0;
        deletedAt = in.readParcelable(Timestamp.class.getClassLoader());
        updatedAt = in.readParcelable(Timestamp.class.getClassLoader());
        createdAt = in.readParcelable(Timestamp.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(updateId);
        dest.writeString(venueId);
        dest.writeString(userId);
        dest.writeString(crowdLevel);
        dest.writeString(waitTime);
        dest.writeString(ageRange);
        dest.writeString(mediaUrl);

        // Write new fields
        dest.writeString(venueName);
        dest.writeString(venueType);
        dest.writeString(venueLogoUrl);

        dest.writeString(description);
        dest.writeString(thumbnailUrl);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
        dest.writeParcelable(deletedAt, flags);
        dest.writeParcelable(updatedAt, flags);
        dest.writeParcelable(createdAt, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LiveUpdate> CREATOR = new Creator<LiveUpdate>() {
        @Override
        public LiveUpdate createFromParcel(Parcel in) {
            return new LiveUpdate(in);
        }

        @Override
        public LiveUpdate[] newArray(int size) {
            return new LiveUpdate[size];
        }
    };

    // --- Getters ---

    public String getUpdateId() {
        return updateId;
    }

    @NonNull
    public String getVenueId() {
        return venueId;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    @NonNull
    public String getCrowdLevel() {
        return crowdLevel;
    }

    @NonNull
    public String getWaitTime() {
        return waitTime;
    }

    @NonNull
    public String getAgeRange() {
        return ageRange;
    }

    @NonNull
    public String getMediaUrl() {
        return mediaUrl;
    }

    // Getters for new fields
    public String getVenueName() {
        return venueName;
    }

    public String getVenueType() {
        return venueType;
    }

    public String getVenueLogoUrl() {
        return venueLogoUrl;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // --- Setters ---

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }

    public void setVenueId(@NonNull String venueId) {
        this.venueId = venueId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public void setCrowdLevel(@NonNull String crowdLevel) {
        this.crowdLevel = crowdLevel;
    }

    public void setWaitTime(@NonNull String waitTime) {
        this.waitTime = waitTime;
    }

    public void setAgeRange(@NonNull String ageRange) {
        this.ageRange = ageRange;
    }

    public void setMediaUrl(@NonNull String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    // Setters for new fields
    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public void setVenueLogoUrl(String venueLogoUrl) {
        this.venueLogoUrl = venueLogoUrl;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}