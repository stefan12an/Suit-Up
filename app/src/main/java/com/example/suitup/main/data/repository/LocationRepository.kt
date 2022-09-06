package com.example.suitup.main.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.example.suitup.common.Resource
import com.example.suitup.common.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


interface LocationRepository {
    val fusedLocationClient: FusedLocationProviderClient
    suspend fun checkForPermission(): Resource<Status>
    suspend fun getCurrentLocation(): Location
}

class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) :
    LocationRepository {
    override val fusedLocationClient: FusedLocationProviderClient
        get() = LocationServices.getFusedLocationProviderClient(context)


    override suspend fun checkForPermission(): Resource<Status> {
        return withContext(ioDispatcher) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return@withContext Resource(Status.GRANTED)
            } else {
                return@withContext Resource(Status.DENIED)
            }
        }
    }

    override suspend fun getCurrentLocation(): Location =
        withContext(ioDispatcher) { fusedLocationClient.lastLocation.await() }
}
