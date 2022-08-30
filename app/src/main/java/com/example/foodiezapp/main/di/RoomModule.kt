package com.example.foodiezapp.main.di

import com.example.foodiezapp.main.data.repository.RoomRepository
import com.example.foodiezapp.main.data.repository.RoomRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomModule {

    @Binds
    abstract fun provideRoomRepository(impl: RoomRepositoryImpl): RoomRepository

}