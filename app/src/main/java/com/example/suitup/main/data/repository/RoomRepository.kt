package com.example.suitup.main.data.repository

import com.example.suitup.main.data.db.AppDatabase
import com.example.suitup.main.data.model.StoreEntity
import javax.inject.Inject

interface RoomRepository {
    suspend fun loadStoresFromDb(): List<StoreEntity>
    suspend fun loadStoreIdsFromDb(): List<String>
    suspend fun addStoreToDb(storeEntity: StoreEntity)
    suspend fun deleteStoreFromDb(storeId: String)
}

class RoomRepositoryImpl @Inject constructor(db: AppDatabase) : RoomRepository {
    private val storeDao = db.storeDao()

    override suspend fun loadStoresFromDb(): List<StoreEntity> {
        return storeDao.getStores()
    }

    override suspend fun loadStoreIdsFromDb(): List<String> {
        return storeDao.getStoreIds()
    }

    override suspend fun addStoreToDb(storeEntity: StoreEntity) {
        storeDao.addStore(storeEntity)
    }

    override suspend fun deleteStoreFromDb(storeId: String) {
        storeDao.deleteStore(storeId)
    }
}