package com.example.irpfchecker.di

import android.content.Context
import androidx.room.Room
import com.example.irpfchecker.data.AppDatabase
import com.example.irpfchecker.data.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }
}