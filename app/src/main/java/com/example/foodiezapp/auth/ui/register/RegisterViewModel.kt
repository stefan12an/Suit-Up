package com.example.foodiezapp.auth.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodiezapp.auth.data.model.UserCredentials
import com.example.foodiezapp.auth.data.repository.RegisterRepository
import com.example.foodiezapp.common.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val registerRepository: RegisterRepository) :
    ViewModel() {

    private val _sideEffect = MutableLiveData<Event<RegisterSideEffects>>()
    val sideEffect: LiveData<Event<RegisterSideEffects>> = _sideEffect
    private val _uiState = MutableLiveData(RegisterState())
    val uiState: LiveData<RegisterState> = _uiState

    fun action(userIntent: RegisterUserIntent) {
        when (userIntent) {
            is RegisterUserIntent.Register -> register(userIntent.credentials)
            is RegisterUserIntent.Login -> pushSideEffect(RegisterSideEffects.NavigateToLogin)
        }
    }

    private fun register(credentials: UserCredentials) {
        _uiState.value = uiState.value!!.copy(loading = true)
        viewModelScope.launch {
            val response = registerRepository.registerUser(credentials)
            if (response.status == Status.SUCCESS) {
                pushSideEffect(RegisterSideEffects.NavigateToLogin)
                _uiState.postValue(uiState.value?.copy(loading = false))
            } else if (response.status == Status.ERROR) {
                if (response.errorCode == 1) {
                    pushSideEffect(RegisterSideEffects.Feedback("The email format is not correct please enter a valid one."))
                    _uiState.postValue(uiState.value?.copy(loading = false))
                } else if (response.errorCode == 2) {
                    pushSideEffect(RegisterSideEffects.Feedback("The password you've entered is not long enough (minimum 5 characters)."))
                    _uiState.postValue(uiState.value?.copy(loading = false))
                }
            }
        }
    }

    private fun pushSideEffect(navigateTo: RegisterSideEffects) {
        _sideEffect.value = Event(navigateTo)
    }
}

data class RegisterState(
    val loading: Boolean = false
) : UiState

sealed class RegisterUserIntent : UserIntent {
    class Register(val credentials: UserCredentials) : RegisterUserIntent()
    object Login : RegisterUserIntent()
}

sealed class RegisterSideEffects : SideEffect {
    class Feedback(val msg: String) : RegisterSideEffects()
    object NavigateToLogin : RegisterSideEffects()
    object NavigateToRegister : RegisterSideEffects()
}