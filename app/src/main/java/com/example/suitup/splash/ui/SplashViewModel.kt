package com.example.suitup.splash.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.suitup.common.Event
import com.example.suitup.common.SideEffect
import com.example.suitup.splash.data.SplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val splashRepository: SplashRepository
) : ViewModel() {
    private val _sideEffect = MutableLiveData<Event<SplashSideEffects>>()
    val sideEffect: LiveData<Event<SplashSideEffects>> = _sideEffect


    fun logInCheck(isLoggedIn: Boolean) {
        if(isLoggedIn) {
            pushSideEffect(SplashSideEffects.NavigateToMain)
        }else{
            pushSideEffect(SplashSideEffects.NavigateToIntro)
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