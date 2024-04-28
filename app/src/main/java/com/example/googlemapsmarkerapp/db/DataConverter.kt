package com.example.googlemapsmarkerapp.db

import androidx.room.TypeConverter
import com.example.googlemapsmarkerapp.model.LocationData
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromLatLng(latLng: LatLng): String {
        return gson.toJson(latLng)
    }

    @TypeConverter
    fun toLatLng(data: String): LatLng {
        val type = object : TypeToken<LatLng>() {}.type
        return gson.fromJson(data, type)
    }

}
