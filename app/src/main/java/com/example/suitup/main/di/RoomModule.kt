package com.example.suitup.main.di

import com.example.suitup.main.data.repository.RoomRepository
import com.example.suitup.main.data.repository.RoomRepositoryImpl
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