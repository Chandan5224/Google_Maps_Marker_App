package com.example.googlemapsmarkerapp.ui

import com.example.googlemapsmarkerapp.db.AppDatabase
import com.example.googlemapsmarkerapp.db.LocationData

class MainRepository(private val appDatabase: AppDatabase) {

    suspend fun insertLocation(locationData: LocationData) =
        appDatabase.locationDataDao().insert(locationData)

    suspend fun deleteLocation(locationData: LocationData) =
        appDatabase.locationDataDao().delete(locationData)

    suspend fun getAllSavedLocation() =
        appDatabase.locationDataDao().getAllSavedLocation()
}
