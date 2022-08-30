package com.example.foodiezapp.auth.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.foodiezapp.auth.data.model.UserCredentials
import com.example.foodiezapp.common.Resource
import com.example.foodiezapp.common.Status
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface LoginRepository {
    fun getUser(): Flow<UserCredentials>
    suspend fun loginUser(credentials: UserCredentials): Status
    fun logout(): Resource<Unit>
}

class LoginRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : LoginRepository {

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

    override suspend fun loginUser(credentials: UserCredentials): Status {
        val user = getUser().first()
        return if (user == credentials) {
            Status.SUCCESS
        } else {
            Status.ERROR
        }
    }

    override fun logout(): Resource<Unit> {
        TODO("Not yet implemented")
    }
}
