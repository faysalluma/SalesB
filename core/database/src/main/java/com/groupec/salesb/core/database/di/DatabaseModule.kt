package com.groupec.salesb.core.database.di

import android.content.Context
import androidx.room.Room
import com.groupec.salesb.core.database.room.SalesBDatabase
import com.groupec.salesb.core.database.room.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): SalesBDatabase {
        return Room.databaseBuilder(
            context,
            SalesBDatabase::class.java,
            name = "SalesBRoomDatabase"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: SalesBDatabase): UserDao {
        return database.userDao()
    }
}