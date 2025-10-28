package com.ibrahimekinci.barcrowd.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

/**
 * DAO for VenueEntity, supporting batch operations and location-based queries.
 */
@Dao
public interface VenueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<VenueEntity> venues);

    @Query("SELECT * FROM venues")
    LiveData<List<VenueEntity>> getAllVenues();

    @Query("SELECT * FROM venues WHERE id = :venueId")
    VenueEntity getVenueById(String venueId);

    @Query("SELECT * FROM venues WHERE name LIKE '%' || :search || '%'")
    LiveData<List<VenueEntity>> searchVenues(String search); // For filters
}