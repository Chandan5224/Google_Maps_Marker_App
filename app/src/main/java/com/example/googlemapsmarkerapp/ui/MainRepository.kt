package com.example.googlemapsmarkerapp.ui

import com.example.googlemapsmarkerapp.model.LocationData
import com.example.googlemapsmarkerapp.db.LocationDataDao
import javax.inject.Inject

class MainRepository @Inject constructor(private val locationDataDao: LocationDataDao) {

    suspend fun insertLocation(locationData: LocationData) =
        locationDataDao.insert(locationData)

    suspend fun deleteLocation(locationData: LocationData) =
        locationDataDao.delete(locationData)

    suspend fun getAllSavedLocation() =
        locationDataDao.getAllSavedLocation()
}
