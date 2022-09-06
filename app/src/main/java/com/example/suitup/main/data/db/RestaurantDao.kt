package com.example.suitup.main.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.suitup.main.data.model.RestaurantEntity

@Dao
interface RestaurantDao {
    @Transaction
    @Query(
        "SELECT * FROM Restaurants"
    )
    suspend fun getRestaurants(): List<RestaurantEntity>

    @Transaction
    @Query(
        "SELECT id FROM Restaurants"
    )
    suspend fun getRestaurantIds(): List<String>

    @Query("DELETE FROM Restaurants WHERE id = :restaurantId")
    suspend fun deleteRestaurant(restaurantId: String)

    @Insert
    suspend fun addRestaurant(restaurantEntity: RestaurantEntity)
}