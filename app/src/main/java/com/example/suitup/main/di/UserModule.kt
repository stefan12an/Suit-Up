package com.example.suitup.main.di

import com.example.suitup.main.data.repository.UserRepository
import com.example.suitup.main.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    @Binds
    abstract fun provideUserRepository(impl: UserRepositoryImpl): UserRepository

}