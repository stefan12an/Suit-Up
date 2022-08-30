package com.example.suitup.auth.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suitup.auth.data.model.UserCredentials
import com.example.suitup.auth.data.repository.LoginRepository
import com.example.suitup.common.*
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
            is LoginUserIntent.Login -> login(userIntent.credentials)
            is LoginUserIntent.Forgot -> pushSideEffect(LoginSideEffects.NavigateToResetPass)
            is LoginUserIntent.Register -> pushSideEffect(LoginSideEffects.NavigateToRegister)
        }
    }


    private fun login(userCredentials: UserCredentials) {
        _uiState.value = uiState.value!!.copy(loading = true)
        viewModelScope.launch {
            val response = loginRepository.loginUser(userCredentials)
            if (response == Status.SUCCESS) {
                pushSideEffect(LoginSideEffects.NavigateToMain)
                _uiState.postValue(uiState.value?.copy(loading = false))
            } else if (response == Status.ERROR) {
                pushSideEffect(LoginSideEffects.Feedback("Something went wrong! Please try again."))
                _uiState.postValue(uiState.value?.copy(loading = false))
            }
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
    class Login(val credentials: UserCredentials) : LoginUserIntent()
    object Forgot : LoginUserIntent()
    object Register : LoginUserIntent()
}

sealed class LoginSideEffects : SideEffect {
    class Feedback(val msg: String) : LoginSideEffects()
    object NavigateToMain : LoginSideEffects()
    object NavigateToRegister : LoginSideEffects()
    object NavigateToResetPass : LoginSideEffects()
}

