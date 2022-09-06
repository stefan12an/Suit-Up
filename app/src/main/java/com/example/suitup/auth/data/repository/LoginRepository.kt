package com.example.suitup.auth.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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
