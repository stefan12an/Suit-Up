package com.example.foodiezapp.auth.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.foodiezapp.auth.data.model.UserCredentials
import com.example.foodiezapp.common.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface LoginRepository {
    suspend fun saveOrigin(origin: String)
}

class LoginRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : LoginRepository {

    companion object {
        val LOGIN_ORIGIN = stringPreferencesKey("origin")
    }

    override suspend fun saveOrigin(origin: String) {
        dataStore.edit { preferences ->
            preferences[LOGIN_ORIGIN] = origin
        }
    }
}
