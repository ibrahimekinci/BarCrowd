package com.ibrahimekinci.barcrowd.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import java.util.Date;

@Database(entities = {VenueEntity.class, LiveUpdateEntity.class}, version = 1, exportSchema = false)
@TypeConverters({AppDatabase.DateConverter.class}) // Register the converter
public abstract class AppDatabase extends RoomDatabase {

    public abstract VenueDao venueDao();
    public abstract LiveUpdateDao liveUpdateDao();

    /**
     * Internal TypeConverter for java.util.Date <-> Long.
     * Room will use this automatically for all Date fields.
     */
    public static class DateConverter {
        @TypeConverter
        public static Date fromTimestamp(Long value) {
            return value == null ? null : new Date(value);
        }

        @TypeConverter
        public static Long dateToTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }
    }
}