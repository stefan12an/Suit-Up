package com.example.foodiezapp.auth.di

import com.example.foodiezapp.auth.data.repository.LoginRepository
import com.example.foodiezapp.auth.data.repository.LoginRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    abstract fun provideLoginRepository(impl: LoginRepositoryImpl): LoginRepository

}