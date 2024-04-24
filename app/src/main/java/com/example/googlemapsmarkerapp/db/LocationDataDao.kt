package com.example.googlemapsmarkerapp.db

import androidx.room.*

@Dao
interface LocationDataDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(locationData: LocationData)

    @Query("SELECT * FROM Locations")
    suspend fun getAllSavedLocation(): List<LocationData>

    @Delete
    suspend fun delete(locationData: LocationData)
}