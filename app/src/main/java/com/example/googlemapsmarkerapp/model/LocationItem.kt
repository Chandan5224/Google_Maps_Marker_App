package com.example.googlemapsmarkerapp.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class LocationItem(
    val locationData: LocationData? = null,
    private val position: LatLng,
    private val title: String,
    private val snippet: String,
    private val zIndex: Float,
    val type: String
) : ClusterItem {
    override fun getPosition(): LatLng = position
    override fun getTitle(): String = title
    override fun getSnippet(): String = snippet
    override fun getZIndex(): Float = zIndex
}