package com.example.suitup.splash.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.suitup.auth.data.model.UserCredentials
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SplashRepository {
    fun getUser(): Flow<UserCredentials>
}

class SplashRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>): SplashRepository{

    companion object {
        val USERNAME = stringPreferencesKey("username")
        val PASSWORD = stringPreferencesKey("password")
    }

    override fun getUser(): Flow<UserCredentials> = dataStore.data.map { user ->
        UserCredentials(
            username = user[USERNAME]?: "",
            password = user[PASSWORD]?: ""
        )
    }

}