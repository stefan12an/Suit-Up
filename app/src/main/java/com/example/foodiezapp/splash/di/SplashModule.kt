package com.example.foodiezapp.splash.di

import com.example.foodiezapp.auth.data.repository.LoginRepository
import com.example.foodiezapp.auth.data.repository.LoginRepositoryImpl
import com.example.foodiezapp.splash.data.SplashRepository
import com.example.foodiezapp.splash.data.SplashRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SplashModule {

    @Binds
    abstract fun provideSplashRepository(impl: SplashRepositoryImpl): SplashRepository
}