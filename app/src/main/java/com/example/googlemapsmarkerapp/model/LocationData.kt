package com.example.googlemapsmarkerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng


@Entity(tableName = "locations")
data class LocationData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val age: Int,
    val relation: String,
    val latLng: LatLng,
    val address: String,
    val type: String
)
