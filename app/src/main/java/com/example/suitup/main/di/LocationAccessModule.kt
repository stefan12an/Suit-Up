package com.example.suitup.main.di

import com.example.suitup.main.data.repository.LocationRepository
import com.example.suitup.main.data.repository.LocationRepositoryImpl
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