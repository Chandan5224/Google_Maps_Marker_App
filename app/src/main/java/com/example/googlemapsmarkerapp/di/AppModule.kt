package com.example.googlemapsmarkerapp.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.googlemapsmarkerapp.db.AppDatabase
import com.example.googlemapsmarkerapp.db.LocationDataDao
import com.example.googlemapsmarkerapp.ui.MainRepository
import com.example.googlemapsmarkerapp.ui.ViewModelProviderFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideLocationDao(database: AppDatabase): LocationDataDao {
        return database.locationDataDao()
    }

    @Provides
    fun provideViewModelProviderFactory(repository: MainRepository): ViewModelProvider.Factory {
        return ViewModelProviderFactory(repository)
    }
    @Singleton
    @Provides
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "google_map.db").build()
    }

    @Singleton
    @Provides
    fun provideYourRepository(db: AppDatabase): MainRepository {
        return MainRepository(db.locationDataDao())
    }
}