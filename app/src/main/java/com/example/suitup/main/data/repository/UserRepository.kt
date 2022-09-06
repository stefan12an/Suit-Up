package com.example.suitup.main.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface UserRepository {
    fun getOrigin(): Flow<String?>
}

class UserRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>) :
    UserRepository {

    companion object {
        val LOGIN_ORIGIN = stringPreferencesKey("origin")
    }

    override fun getOrigin(): Flow<String?> = dataStore.data.map { it[LOGIN_ORIGIN] }

}