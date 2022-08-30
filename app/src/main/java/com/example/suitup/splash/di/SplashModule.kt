package com.example.suitup.splash.di

import com.example.suitup.splash.data.SplashRepository
import com.example.suitup.splash.data.SplashRepositoryImpl
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