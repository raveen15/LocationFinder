package com.example.assignment2;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Location.class, version = 1, exportSchema = false)
public abstract class LocationDatabase extends RoomDatabase {
    private static LocationDatabase locationDatabase;

    public static synchronized LocationDatabase getLocationDatabase(Context context) {
        if (locationDatabase == null) {
            locationDatabase = Room.databaseBuilder(
                    context,
                    LocationDatabase.class,
                    "location_db"
            ).build();
        }
        return locationDatabase;
    }

    public abstract LocationDao locationDao();
}
