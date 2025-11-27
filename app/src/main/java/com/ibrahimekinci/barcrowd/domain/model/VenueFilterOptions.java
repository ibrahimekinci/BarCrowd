package com.ibrahimekinci.barcrowd.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VenueFilterOptions implements Parcelable {

    private String venueNameQuery; // Corrected spelling from vanue
    private String venueType;
    private String lastLiveUpdateCrowdLevel;
    private String lastLiveUpdateWaitTime;
    private String lastLiveUpdateAgeRange;

    public VenueFilterOptions() {}

    public VenueFilterOptions(String venueNameQuery, String venueType,
                              String lastLiveUpdateCrowdLevel, String lastLiveUpdateWaitTime,
                              String lastLiveUpdateAgeRange) {
        this.venueNameQuery = venueNameQuery;
        this.venueType = venueType;
        this.lastLiveUpdateCrowdLevel = lastLiveUpdateCrowdLevel;
        this.lastLiveUpdateWaitTime = lastLiveUpdateWaitTime;
        this.lastLiveUpdateAgeRange = lastLiveUpdateAgeRange;
    }

    protected VenueFilterOptions(Parcel in) {
        venueNameQuery = in.readString();
        venueType = in.readString();
        lastLiveUpdateCrowdLevel = in.readString();
        lastLiveUpdateWaitTime = in.readString();
        lastLiveUpdateAgeRange = in.readString();
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(venueNameQuery);
        dest.writeString(venueType);
        dest.writeString(lastLiveUpdateCrowdLevel);
        dest.writeString(lastLiveUpdateWaitTime);
        dest.writeString(lastLiveUpdateAgeRange);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getVenueNameQuery() { return venueNameQuery; }
    public void setVenueNameQuery(String venueNameQuery) { this.venueNameQuery = venueNameQuery; }

    public String getVenueType() { return venueType; }
    public void setVenueType(String venueType) { this.venueType = venueType; }

    public String getLastLiveUpdateCrowdLevel() { return lastLiveUpdateCrowdLevel; }
    public void setLastLiveUpdateCrowdLevel(String lastLiveUpdateCrowdLevel) { this.lastLiveUpdateCrowdLevel = lastLiveUpdateCrowdLevel; }

    public String getLastLiveUpdateWaitTime() { return lastLiveUpdateWaitTime; }
    public void setLastLiveUpdateWaitTime(String lastLiveUpdateWaitTime) { this.lastLiveUpdateWaitTime = lastLiveUpdateWaitTime; }

    public String getLastLiveUpdateAgeRange() { return lastLiveUpdateAgeRange; }
    public void setLastLiveUpdateAgeRange(String lastLiveUpdateAgeRange) { this.lastLiveUpdateAgeRange = lastLiveUpdateAgeRange; }
}