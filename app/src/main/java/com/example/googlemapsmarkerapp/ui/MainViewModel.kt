package com.example.googlemapsmarkerapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googlemapsmarkerapp.db.LocationData
import com.example.googlemapsmarkerapp.utils.Resource
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository) : ViewModel() {

    var locations: MutableLiveData<Resource<List<LocationData>>> = MutableLiveData()

    fun insertLocation(locationData: LocationData) {
        viewModelScope.launch {
            locations.postValue(Resource.Loading())
            try {
                repository.insertLocation(locationData)
                val updatedLocations = repository.getAllSavedLocation()  // Refresh list from database
                locations.postValue(Resource.Success(updatedLocations))
            } catch (e: Exception) {
                locations.postValue(Resource.Error("Failed to insert location: ${e.localizedMessage}"))
            }
        }
    }

    fun deleteLocation(locationData: LocationData) {
        viewModelScope.launch {
            locations.postValue(Resource.Loading())
            try {
                repository.deleteLocation(locationData)
                val updatedLocations = repository.getAllSavedLocation()  // Refresh list from database
                locations.postValue(Resource.Success(updatedLocations))
            } catch (e: Exception) {
                locations.postValue(Resource.Error("Failed to delete location: ${e.localizedMessage}"))
            }
        }
    }

    fun getAllSavedLocation() {
        viewModelScope.launch {
            locations.postValue(Resource.Loading())
            try {
                val allLocations = repository.getAllSavedLocation()
                locations.postValue(Resource.Success(allLocations))
            } catch (e: Exception) {
                locations.postValue(Resource.Error("Failed to fetch locations: ${e.localizedMessage}"))
            }
        }
    }
}
