package com.example.suitup.splash.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suitup.common.Event
import com.example.suitup.common.SideEffect
import com.example.suitup.splash.data.SplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val splashRepository: SplashRepository
) : ViewModel() {
    private val _sideEffect = MutableLiveData<Event<SplashSideEffects>>()
    val sideEffect: LiveData<Event<SplashSideEffects>> = _sideEffect


    fun isLoggedIn() {
        viewModelScope.launch {
            val user = splashRepository.getUser().first()
            Log.e(TAG, "isLoggedIn: $user")
            if (user.username == "" && user.password == "") {
                pushSideEffect(SplashSideEffects.NavigateToIntro)
            } else {
                pushSideEffect(SplashSideEffects.NavigateToMain)
            }
        }
    }
    private fun pushSideEffect(navigateTo: SplashSideEffects) {
        _sideEffect.value = Event(navigateTo)
    }
}


sealed class SplashSideEffects : SideEffect {
    class Feedback(val msg: String) : SplashSideEffects()
    object NavigateToIntro : SplashSideEffects()
    object NavigateToMain: SplashSideEffects()
}