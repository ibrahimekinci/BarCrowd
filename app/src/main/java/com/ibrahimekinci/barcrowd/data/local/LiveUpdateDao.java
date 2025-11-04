package com.ibrahimekinci.barcrowd.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface LiveUpdateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LiveUpdateEntity update);

    @Query("SELECT * FROM live_updates WHERE venueId = :venueId ORDER BY createdAt DESC")
    LiveData<List<LiveUpdateEntity>> getUpdatesForVenue(String venueId);

    @Query("SELECT * FROM live_updates WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<LiveUpdateEntity>> getUserContributions(String userId);

    @Query("SELECT * FROM live_updates WHERE syncStatus = 0 ORDER BY createdAt") // Changed from syncStatus = 0
    List<LiveUpdateEntity> getPendingUpdates();

    @Query("UPDATE live_updates SET syncStatus = 1 WHERE updateId = :id") // Changed from id
    void markAsSynced(String id);

    /**
     * Gets the 5 most recent, non-deleted updates for the Home Page.
     */
    @Query("SELECT * FROM live_updates WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT 5")
    LiveData<List<LiveUpdateEntity>> getRecentLiveUpdates();

    /**
     * Gets all (up to 100) non-deleted updates for the "All Updates" feed.
     */
    @Query("SELECT * FROM live_updates WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT 100")
    LiveData<List<LiveUpdateEntity>> getAllLiveUpdates();
}