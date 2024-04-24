package com.example.googlemapsmarkerapp.utils

import android.app.Activity
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient

object LocationUtils {
    fun checkLocationSettings(activity: Activity, callback: LocationSettingsCallback) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener(activity) { response ->
                callback.onLocationSettingsSuccess()
            }
            .addOnFailureListener(activity) { e ->
                if (e is ResolvableApiException) {
                    try {
                        e.startResolutionForResult(activity, Constants.REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Notify the callback about the failure.
                        callback.onLocationSettingsFailure(sendEx)
                    }
                } else {
                    // Notify the callback about the failure.
                    callback.onLocationSettingsFailure(e)
                }
            }
    }
}
