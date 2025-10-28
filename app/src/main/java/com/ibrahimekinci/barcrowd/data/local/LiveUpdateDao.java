package com.ibrahimekinci.barcrowd.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

/**
 * DAO for LiveUpdateEntity, with sync-specific methods for offline queuing.
 */
@Dao
public interface LiveUpdateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LiveUpdateEntity update);

    @Query("SELECT * FROM live_updates WHERE venueId = :venueId ORDER BY timestamp DESC")
    LiveData<List<LiveUpdateEntity>> getUpdatesForVenue(String venueId);

    @Query("SELECT * FROM live_updates WHERE userId = :userId")
    LiveData<List<LiveUpdateEntity>> getUserContributions(String userId);

    @Query("SELECT * FROM live_updates WHERE syncStatus = 0")
    List<LiveUpdateEntity> getPendingUpdates(); // For sync queue, synchronous

    @Query("UPDATE live_updates SET syncStatus = 1 WHERE id = :id")
    void markAsSynced(String id); // After successful upload
}