package com.ibrahimekinci.barcrowd.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;

@Dao
public interface VenueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<VenueEntity> venues);

    @Query("SELECT * FROM venues ORDER BY showOnHomePage DESC, createdAt DESC, name ASC LIMIT 5")
    LiveData<List<VenueEntity>> getHomePageVenues();

    @Query("SELECT * FROM venues WHERE venueId = :venueId")
    VenueEntity getVenueById(String venueId);
    @RawQuery(observedEntities = VenueEntity.class)
    LiveData<List<VenueEntity>> searchVenuesWithFilters(SimpleSQLiteQuery query);
    /**
     * Gets all venues from the local cache, sorted by name.
     * Used for spinners and "All Venues" lists.
     */
    @Query("SELECT * FROM venues ORDER BY name ASC")
    LiveData<List<VenueEntity>> getAllVenuesSortedByName();
}