package com.example.googlemapsmarkerapp.utils

interface LocationSettingsCallback {
    fun onLocationSettingsSuccess()
    fun onLocationSettingsFailure(exception: Exception)
}
