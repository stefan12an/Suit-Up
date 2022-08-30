package com.example.foodiezapp.main.di

import com.example.foodiezapp.main.data.repository.UserRepository
import com.example.foodiezapp.main.data.repository.UserRepositoryImpl
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