package com.example.suitup.main.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.suitup.main.data.model.StoreEntity

@Database(
    entities = [StoreEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
}