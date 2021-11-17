package com.example.assignment2;
import androidx.room.*;

import java.util.List;

@Dao
public interface LocationDao {
    @Query("SELECT * FROM location")
    List<Location> getAll();

    @Query("SELECT * FROM location WHERE address LIKE :address")
    Location findByAddress(String address);

    @Insert
    void insertAll(Location... location);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Location location);

    @Delete
    Void delete(Location location);
}
