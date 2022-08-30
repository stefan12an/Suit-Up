package com.example.foodiezapp.auth.di

import com.example.foodiezapp.auth.data.repository.LoginRepository
import com.example.foodiezapp.auth.data.repository.LoginRepositoryImpl
import com.example.foodiezapp.auth.data.repository.RegisterRepository
import com.example.foodiezapp.auth.data.repository.RegisterRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    abstract fun provideLoginRepository(impl: LoginRepositoryImpl): LoginRepository

    @Binds
    abstract fun provideRegisterRepository(impl: RegisterRepositoryImpl): RegisterRepository

}