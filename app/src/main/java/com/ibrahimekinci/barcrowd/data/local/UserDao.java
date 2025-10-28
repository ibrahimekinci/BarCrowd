package com.ibrahimekinci.barcrowd.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * DAO for UserEntity, handling insertion and retrieval for session management.
 */
@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserEntity user);

    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<UserEntity> getUserById(String userId);

    @Query("SELECT * FROM users WHERE email = :email")
    UserEntity getUserByEmail(String email); // Synchronous for auth checks

    @Query("DELETE FROM users")
    void deleteAll(); // For logout or testing
}