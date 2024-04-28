package com.example.googlemapsmarkerapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.googlemapsmarkerapp.model.LocationData


@Database(
    entities = [LocationData::class], version = 1
)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDataDao(): LocationDataDao

}
