package com.groupec.salesb.core.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.groupec.salesb.core.database.model.User
import com.groupec.salesb.core.database.room.dao.UserDao

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class SalesBDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}