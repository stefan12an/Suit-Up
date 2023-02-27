package com.example.suitup.auth.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suitup.auth.data.repository.LoginRepository
import com.example.suitup.common.Event
import com.example.suitup.common.SideEffect
import com.example.suitup.common.UiState
import com.example.suitup.common.UserIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {
    private val _sideEffect = MutableLiveData<Event<SignupSideEffects>>()
    val sideEffect: LiveData<Event<SignupSideEffects>> = _sideEffect
    private val _uiState = MutableLiveData(SignupState())
    val uiState: LiveData<SignupState> = _uiState

    fun action(userIntent: SignupUserIntent) {
        when (userIntent) {
            is SignupUserIntent.Signup -> saveOrigin(userIntent.origin)
            is SignupUserIntent.GoToLogin -> pushSideEffect(SignupSideEffects.NavigateToLogin)
        }
    }


    private fun saveOrigin(origin: String) {
        _uiState.value = uiState.value!!.copy(loading = true)
        viewModelScope.launch {
            val response = loginRepository.saveOrigin(origin)
            pushSideEffect(SignupSideEffects.NavigateToMain)
            _uiState.postValue(uiState.value?.copy(loading = false))
        }
    }

    private fun pushSideEffect(navigateTo: SignupSideEffects) {
        _sideEffect.value = Event(navigateTo)
    }
}

data class SignupState(
    val loading: Boolean = false
) : UiState

sealed class SignupUserIntent : UserIntent {
    class Signup(val origin: String) : SignupUserIntent()
    object GoToLogin : SignupUserIntent()
}

sealed class SignupSideEffects : SideEffect {
    class Feedback(val msg: String) : SignupSideEffects()
    object NavigateToMain : SignupSideEffects()
    object NavigateToLogin : SignupSideEffects()
}