package com.example.foodiezapp.main.di

import com.example.foodiezapp.main.data.repository.LocationRepository
import com.example.foodiezapp.main.data.repository.LocationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationAccessModule {

    @Binds
    abstract fun provideLocationRepository(impl: LocationRepositoryImpl): LocationRepository

}