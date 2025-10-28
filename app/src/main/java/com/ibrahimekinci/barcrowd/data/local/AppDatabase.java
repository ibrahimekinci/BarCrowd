package com.ibrahimekinci.barcrowd.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {UserEntity.class, VenueEntity.class, LiveUpdateEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract VenueDao venueDao();
    public abstract LiveUpdateDao liveUpdateDao();
}