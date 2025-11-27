package com.ibrahimekinci.barcrowd.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Database(entities = {VenueEntity.class, LiveUpdateEntity.class}, version = 2, exportSchema = false)
@TypeConverters({AppDatabase.Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract VenueDao venueDao();

    public abstract LiveUpdateDao liveUpdateDao();

    /**
     * Internal TypeConverters.
     */
    public static class Converters {

        @TypeConverter
        public static Date fromTimestamp(Long value) {
            return value == null ? null : new Date(value);
        }

        @TypeConverter
        public static Long dateToTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }

        @TypeConverter
        public static String fromStringMap(Map<String, String> map) {
            if (map == null) {
                return null;
            }
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            return gson.toJson(map, type);
        }

        @TypeConverter
        public static Map<String, String> toStringMap(String value) {
            if (value == null) {
                return new HashMap<>();
            }
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            return gson.fromJson(value, type);
        }
    }
}