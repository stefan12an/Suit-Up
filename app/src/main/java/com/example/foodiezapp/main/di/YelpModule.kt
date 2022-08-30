package com.example.foodiezapp.main.di

import com.example.foodiezapp.main.data.repository.YelpApiRepository
import com.example.foodiezapp.main.data.repository.YelpApiRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class YelpModule {
    @Binds
    abstract fun provideYelpApiRepository(impl: YelpApiRepositoryImpl): YelpApiRepository

}