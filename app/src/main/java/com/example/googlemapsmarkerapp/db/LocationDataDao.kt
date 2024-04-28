package com.example.googlemapsmarkerapp.db

import androidx.room.*
import com.example.googlemapsmarkerapp.model.LocationData

@Dao
interface LocationDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locationData: LocationData)

    @Query("SELECT * FROM locations")
    suspend fun getAllSavedLocation(): List<LocationData>

    @Delete
    suspend fun delete(locationData: LocationData)
}