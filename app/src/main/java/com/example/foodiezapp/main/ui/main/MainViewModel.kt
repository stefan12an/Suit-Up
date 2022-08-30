package com.example.foodiezapp.main.ui.main

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodiezapp.common.Event
import com.example.foodiezapp.common.SideEffect
import com.example.foodiezapp.common.Status
import com.example.foodiezapp.common.UserIntent
import com.example.foodiezapp.main.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val locationRepository: LocationRepository) :
    ViewModel() {
    private val _sideEffect = MutableLiveData<Event<PermissionSideEffects>>()
    val sideEffect: LiveData<Event<PermissionSideEffects>> = _sideEffect
    fun action(permissionIntent: PermissionIntent) {
        when (permissionIntent) {
            is PermissionIntent.Check -> check()
            is PermissionIntent.Request -> pushSideEffect(PermissionSideEffects.ActivateRequest)
        }
    }

    private fun check() {
        val permission = locationRepository.checkForPermission()
        if (permission.status == Status.GRANTED) {
            pushSideEffect(PermissionSideEffects.NavigateToHome)
        } else if (permission.status == Status.DENIED) {
            pushSideEffect(PermissionSideEffects.NavigateToRequest)
        }
    }


    private fun pushSideEffect(navigateTo: PermissionSideEffects) {
        _sideEffect.value = Event(navigateTo)
    }
}

sealed class PermissionIntent : UserIntent {
    object Request : PermissionIntent()
    object Check : PermissionIntent()
}

sealed class PermissionSideEffects : SideEffect {
    class Feedback(val msg: String) : PermissionSideEffects()
    object NavigateToHome : PermissionSideEffects()
    object NavigateToRequest : PermissionSideEffects()
    object ActivateRequest : PermissionSideEffects()
}