package com.ibrahimekinci.barcrowd.di;

import android.content.Context;

import androidx.room.Room;

import com.ibrahimekinci.barcrowd.data.local.AppDatabase;

/**
 * Interface to abstract the static Room.databaseBuilder() call.
 * This makes the DependencyInjector class testable by allowing us to mock
 * the database building process.
 */
public interface RoomDatabaseProvider {
    AppDatabase getDatabase(Context context);

    /**
     * Production implementation that uses the real Room build chain.
     */
    class ProductionProvider implements RoomDatabaseProvider {
        @Override
        public AppDatabase getDatabase(Context context) {
            return Room.databaseBuilder(context, AppDatabase.class, "barcrowd-db").build();
        }
    }
}