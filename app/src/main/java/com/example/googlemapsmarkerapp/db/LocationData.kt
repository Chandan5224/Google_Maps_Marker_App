package com.example.googlemapsmarkerapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Locations")
data class LocationData(
    @PrimaryKey(autoGenerate = true) var id: Int,
    val name: String,
    val age: Int,
    val relation: String,
    val latLng: String,
    val address: String
)
