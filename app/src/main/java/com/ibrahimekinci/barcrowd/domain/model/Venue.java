package com.ibrahimekinci.barcrowd.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.HashMap;
import java.util.Map;

/**
 * Entity (POJO) for the /Venues collection.
 * Implements Parcelable to be passed between Android components.
 */
public class Venue implements Parcelable {

    @DocumentId
    private String venueId;

    @NonNull
    private String name;

    @NonNull
    private String type;

    @NonNull
    private String logoUrl;

    @NonNull
    private String address;

    private double latitude;
    private double longitude;

    @NonNull
    private Map<String, String> openingHours = new HashMap<>();

    // Cached/Live Stats with Defaults
    private String averageCrowdLevel = "Medium";
    private String lastCrowdLevel = "Medium";
    private String averageWaitTime = "15–30";
    private String lastWaitTime = "15–30";
    private Timestamp crowdLevelUpdatedAt = null;
    private String mostPopulousAge = "25–30";
    private boolean showOnHomePage = true;

    @ServerTimestamp
    private Timestamp updatedAt;

    @ServerTimestamp
    private Timestamp createdAt;

    // Public, no-argument constructor for Firestore
    public Venue() {
    }

    // --- Parcelable Implementation ---

    protected Venue(Parcel in) {
        venueId = in.readString();
        name = in.readString();
        type = in.readString();
        logoUrl = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();

        // Read Map for openingHours
        openingHours = new HashMap<>();
        in.readMap(openingHours, Map.class.getClassLoader());

        averageCrowdLevel = in.readString();
        lastCrowdLevel = in.readString();
        averageWaitTime = in.readString();
        lastWaitTime = in.readString();
        crowdLevelUpdatedAt = in.readParcelable(Timestamp.class.getClassLoader());
        mostPopulousAge = in.readString();
        showOnHomePage = in.readByte() != 0;
        updatedAt = in.readParcelable(Timestamp.class.getClassLoader());
        createdAt = in.readParcelable(Timestamp.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(venueId);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(logoUrl);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);

        // Write Map for openingHours
        dest.writeMap(openingHours);

        dest.writeString(averageCrowdLevel);
        dest.writeString(lastCrowdLevel);
        dest.writeString(averageWaitTime);
        dest.writeString(lastWaitTime);
        dest.writeParcelable(crowdLevelUpdatedAt, flags);
        dest.writeString(mostPopulousAge);
        dest.writeByte((byte) (showOnHomePage ? 1 : 0));
        dest.writeParcelable(updatedAt, flags);
        dest.writeParcelable(createdAt, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Venue> CREATOR = new Creator<Venue>() {
        @Override
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        @Override
        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };

    // --- Getters ---

    public String getVenueId() {
        return venueId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getType() {
        return type;
    }

    @NonNull
    public String getLogoUrl() {
        return logoUrl;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @NonNull
    public Map<String, String> getOpeningHours() {
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

    public Timestamp getCrowdLevelUpdatedAt() {
        return crowdLevelUpdatedAt;
    }

    public String getMostPopulousAge() {
        return mostPopulousAge;
    }

    public boolean isShowOnHomePage() {
        return showOnHomePage;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // --- Setters ---

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public void setLogoUrl(@NonNull String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setOpeningHours(@NonNull Map<String, String> openingHours) {
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

    public void setCrowdLevelUpdatedAt(Timestamp crowdLevelUpdatedAt) {
        this.crowdLevelUpdatedAt = crowdLevelUpdatedAt;
    }

    public void setMostPopulousAge(String mostPopulousAge) {
        this.mostPopulousAge = mostPopulousAge;
    }

    public void setShowOnHomePage(boolean showOnHomePage) {
        this.showOnHomePage = showOnHomePage;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}