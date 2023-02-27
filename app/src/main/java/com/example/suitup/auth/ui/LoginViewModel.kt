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
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {
    private val _sideEffect = MutableLiveData<Event<LoginSideEffects>>()
    val sideEffect: LiveData<Event<LoginSideEffects>> = _sideEffect
    private val _uiState = MutableLiveData(LoginState())
    val uiState: LiveData<LoginState> = _uiState

    fun action(userIntent: LoginUserIntent) {
        when (userIntent) {
            is LoginUserIntent.Login -> saveOrigin(userIntent.origin)
            is LoginUserIntent.GoToSignup -> pushSideEffect(LoginSideEffects.NavigateToSignUp)
        }
    }


    private fun saveOrigin(origin: String) {
        _uiState.value = uiState.value!!.copy(loading = true)
        viewModelScope.launch {
            val response = loginRepository.saveOrigin(origin)
            pushSideEffect(LoginSideEffects.NavigateToMain)
            _uiState.postValue(uiState.value?.copy(loading = false))
        }
    }

    private fun pushSideEffect(navigateTo: LoginSideEffects) {
        _sideEffect.value = Event(navigateTo)
    }
}

data class LoginState(
    val loading: Boolean = false
) : UiState

sealed class LoginUserIntent : UserIntent {
    class Login(val origin: String) : LoginUserIntent()
    object GoToSignup : LoginUserIntent()
}

sealed class LoginSideEffects : SideEffect {
    class Feedback(val msg: String) : LoginSideEffects()
    object NavigateToMain : LoginSideEffects()
    object NavigateToSignUp : LoginSideEffects()
}

