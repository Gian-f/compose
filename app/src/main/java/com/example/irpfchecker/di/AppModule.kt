package com.example.irpfchecker.di

import android.app.Application
import android.content.Context
import com.example.irpfchecker.data.AppDatabase
import com.example.irpfchecker.data.repository.UserRepository
import com.example.irpfchecker.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideUserRepository(appDatabase: AppDatabase): UserRepository {
        return UserRepositoryImpl(appDatabase.userDao())
    }

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}