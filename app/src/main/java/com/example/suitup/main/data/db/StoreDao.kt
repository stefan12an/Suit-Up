package com.example.suitup.main.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.suitup.main.data.model.StoreEntity

@Dao
interface StoreDao {
    @Transaction
    @Query(
        "SELECT * FROM Stores"
    )
    suspend fun getStores(): List<StoreEntity>

    @Transaction
    @Query(
        "SELECT id FROM Stores"
    )
    suspend fun getStoreIds(): List<String>

    @Query("DELETE FROM Stores WHERE id = :storeId")
    suspend fun deleteStore(storeId: String)

    @Insert
    suspend fun addStore(storeEntity: StoreEntity)
}