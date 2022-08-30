package com.example.foodiezapp.main.data.repository

import com.example.foodiezapp.main.data.db.AppDatabase
import com.example.foodiezapp.main.data.model.*
import javax.inject.Inject

interface RoomRepository {
    suspend fun loadRestaurantsFromDb(): List<RestaurantEntity>
    suspend fun loadRestaurantIdsFromDb(): List<String>
    suspend fun addRestaurantToDb(restaurantEntity: RestaurantEntity)
    suspend fun deleteRestaurantFromDb(restaurantId: String)
}

class RoomRepositoryImpl @Inject constructor(db: AppDatabase) : RoomRepository {
    private val restaurantDao = db.restaurantDao()

    override suspend fun loadRestaurantsFromDb(): List<RestaurantEntity> {
        return restaurantDao.getRestaurants()
    }

    override suspend fun loadRestaurantIdsFromDb(): List<String> {
        return restaurantDao.getRestaurantIds()
    }

    override suspend fun addRestaurantToDb(restaurantEntity: RestaurantEntity) {
        restaurantDao.addRestaurant(restaurantEntity)
    }

    override suspend fun deleteRestaurantFromDb(restaurantId: String) {
        restaurantDao.deleteRestaurant(restaurantId)
    }
}