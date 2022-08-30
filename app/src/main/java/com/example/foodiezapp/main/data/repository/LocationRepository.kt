package com.example.foodiezapp.main.data.repository

import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.foodiezapp.common.Resource
import com.example.foodiezapp.common.Status
import com.example.foodiezapp.main.data.model.CurrentLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.log

interface LocationRepository {
    fun checkForPermission(): Resource<Status>
    fun getCurrentLocation(): Task<Location>
}

class LocationRepositoryImpl @Inject constructor(@ApplicationContext val context: Context) :
    LocationRepository {


    override fun checkForPermission(): Resource<Status> {
        return if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Resource(Status.GRANTED)
        } else {
            Resource(Status.DENIED, errorCode = 1)
        }
    }



    override fun getCurrentLocation(): Task<Location> {
        Log.e(TAG, "getCurrentLocation: CEVA")
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return fusedLocationClient.lastLocation
    }
}
