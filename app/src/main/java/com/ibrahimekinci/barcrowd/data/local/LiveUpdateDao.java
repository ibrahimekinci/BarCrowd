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
    void insert(LiveUpdateEntity liveUpdate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLiveUpdates(List<LiveUpdateEntity> updates);

    // DÜZELTME: Sadece silinmemiş olanları getir (isDeleted = 0 -> false)
    @Query("SELECT * FROM live_updates WHERE isDeleted = 0 ORDER BY createdAt DESC")
    LiveData<List<LiveUpdateEntity>> getAllLiveUpdates();

    // DÜZELTME: Sadece silinmemiş olanları getir
    @Query("SELECT * FROM live_updates WHERE isDeleted = 0 ORDER BY createdAt DESC")
    List<LiveUpdateEntity> getAllLiveUpdatesSync();

    @Query("SELECT * FROM live_updates WHERE updateId = :id")
    LiveUpdateEntity getLiveUpdateById(String id);

    @Query("SELECT * FROM live_updates WHERE syncStatus = 0")
    List<LiveUpdateEntity> getPendingUpdates();

    @Query("UPDATE live_updates SET syncStatus = 1 WHERE updateId = :id")
    void markAsSynced(String id);
}