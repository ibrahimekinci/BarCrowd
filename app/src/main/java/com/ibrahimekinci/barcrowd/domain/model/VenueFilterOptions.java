package com.ibrahimekinci.barcrowd.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VenueFilterOptions implements Parcelable {
    private String nameQuery;
    private String type;       // "Bar", "Club", "Pub"
    private String crowdLevel; // "Low", "Medium", "High"
    private String waitTime;   // "0-5", "5-15", etc.
    private String ageRange;   // "18-21", "21-24", etc.

    // --- Constructors ---
    public VenueFilterOptions() {
    }

    public VenueFilterOptions(String nameQuery, String type, String crowdLevel, String waitTime, String ageRange) {
        this.nameQuery = nameQuery;
        this.type = type;
        this.crowdLevel = crowdLevel;
        this.waitTime = waitTime;
        this.ageRange = ageRange;
    }

    // --- Getters and Setters ---
    public String getNameQuery() {
        return nameQuery;
    }

    public void setNameQuery(String nameQuery) {
        this.nameQuery = nameQuery;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCrowdLevel() {
        return crowdLevel;
    }

    public void setCrowdLevel(String crowdLevel) {
        this.crowdLevel = crowdLevel;
    }

    public String getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) {
        this.waitTime = waitTime;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    // --- Parcelable Implementation ---
    protected VenueFilterOptions(Parcel in) {
        nameQuery = in.readString();
        type = in.readString();
        crowdLevel = in.readString();
        waitTime = in.readString();
        ageRange = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameQuery);
        dest.writeString(type);
        dest.writeString(crowdLevel);
        dest.writeString(waitTime);
        dest.writeString(ageRange);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VenueFilterOptions> CREATOR = new Creator<VenueFilterOptions>() {
        @Override
        public VenueFilterOptions createFromParcel(Parcel in) {
            return new VenueFilterOptions(in);
        }

        @Override
        public VenueFilterOptions[] newArray(int size) {
            return new VenueFilterOptions[size];
        }
    };

    @Override
    public String toString() {
        return "VenueFilterOptions{" +
                "nameQuery='" + nameQuery + '\'' +
                ", type='" + type + '\'' +
                ", crowdLevel='" + crowdLevel + '\'' +
                ", waitTime='" + waitTime + '\'' +
                ", ageRange='" + ageRange + '\'' +
                '}';
    }
}
