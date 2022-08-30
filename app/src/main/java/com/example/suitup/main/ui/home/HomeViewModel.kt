package com.example.suitup.main.ui.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.suitup.common.Event
import com.example.suitup.common.SideEffect
import com.example.suitup.common.UserIntent
import com.example.suitup.main.data.repository.LocationRepository
import com.example.suitup.main.ui.main.PermissionSideEffects
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(private val locationRepository: LocationRepository): ViewModel() {
    private val _sideEffect = MutableLiveData<Event<PermissionSideEffects>>()
    val sideEffect: LiveData<Event<PermissionSideEffects>> = _sideEffect
    fun action(navigationIntent: NavigationIntent) {
        when (navigationIntent) {
            is NavigationIntent.Test -> test()
            else -> {}
        }
    }

    private fun test(){
        locationRepository.getCurrentLocation().addOnSuccessListener { location ->
            Log.e(TAG, "test: ${location.latitude} ${location.longitude}", )
        }
    }
    private fun pushSideEffect(navigateTo: PermissionSideEffects) {
        _sideEffect.value = Event(navigateTo)
    }

    override fun onCleared() {
        super.onCleared()
    }
}

sealed class NavigationIntent : UserIntent {
    object Test: NavigationIntent()
    object Check : NavigationIntent()
}

sealed class NavigationSideEffects : SideEffect {
    class Feedback(val msg: String) : NavigationSideEffects()
    object NavigateToHome : NavigationSideEffects()
    object NavigateToRequest : NavigationSideEffects()
    object ActivateRequest : NavigationSideEffects()
}