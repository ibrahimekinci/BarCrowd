package com.ibrahimekinci.barcrowd.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface VenueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVenue(VenueEntity venue);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVenues(List<VenueEntity> venues);

    @Query("SELECT * FROM venues ORDER BY name ASC")
    LiveData<List<VenueEntity>> getAllVenues();

    @Query("SELECT * FROM venues WHERE venueId = :venueId")
    VenueEntity getVenueById(String venueId);

    // Updated for new field names
    @Query("SELECT * FROM venues WHERE showOnHomePage = 1 ORDER BY lastLiveUpdateCreatedAt DESC")
    LiveData<List<VenueEntity>> getHomePageVenues();

    // Search logic
    @Query("SELECT * FROM venues WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    LiveData<List<VenueEntity>> searchVenues(String searchQuery);

    // Update Venue Stats directly
    @Query("UPDATE venues SET lastLiveUpdateCrowdLevel = :crowd, lastLiveUpdateWaitTime = :wait, lastLiveUpdateAgeRange = :age, lastLiveUpdateCreatedAt = :time WHERE venueId = :venueId")
    void updateVenueStats(String venueId, String crowd, String wait, String age, java.util.Date time);

    @Query("DELETE FROM venues")
    void deleteAllVenues();
}