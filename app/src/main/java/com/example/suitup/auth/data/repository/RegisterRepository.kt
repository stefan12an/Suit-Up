package com.example.suitup.auth.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.suitup.auth.data.model.UserCredentials
import com.example.suitup.common.Resource
import com.example.suitup.common.Status
import javax.inject.Inject

interface RegisterRepository {
    suspend fun saveUser(credentials: UserCredentials): Preferences
    suspend fun registerUser(credentials: UserCredentials): Resource<Status>
}

class RegisterRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : RegisterRepository {

    companion object {
        val USERNAME = stringPreferencesKey("username")
        val PASSWORD = stringPreferencesKey("password")
    }

    override suspend fun saveUser(credentials: UserCredentials): Preferences = dataStore.edit { preferences ->
        preferences[USERNAME] = credentials.username
        preferences[PASSWORD] = credentials.password
    }


    override suspend fun registerUser(credentials: UserCredentials): Resource<Status> {
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(credentials.username).matches()){
            if (credentials.password.length > 5){
            saveUser(credentials)
        }else{
            return Resource(Status.ERROR, errorCode = 2)
        }
        }else{
            return Resource(Status.ERROR, errorCode = 1)
        }
        return Resource(Status.SUCCESS, errorCode = 0)
    }
}