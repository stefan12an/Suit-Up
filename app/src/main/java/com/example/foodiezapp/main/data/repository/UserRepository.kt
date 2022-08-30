package com.example.foodiezapp.main.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.foodiezapp.auth.data.model.UserCredentials
import com.example.foodiezapp.auth.data.repository.LoginRepositoryImpl
import com.example.foodiezapp.common.Resource
import com.example.foodiezapp.common.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
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