package com.example.suitup.main.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.suitup.main.data.model.RestaurantEntity

@Database(
    entities = [RestaurantEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
}