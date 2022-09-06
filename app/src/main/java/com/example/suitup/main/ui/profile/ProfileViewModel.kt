package com.example.suitup.main.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suitup.common.Event
import com.example.suitup.common.SideEffect
import com.example.suitup.common.UiState
import com.example.suitup.common.UserIntent
import com.example.suitup.main.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private val _sideEffect = MutableLiveData<Event<ProfileSideEffects>>()
    val sideEffect: LiveData<Event<ProfileSideEffects>> = _sideEffect

    private val _profileUiState = MutableLiveData(ProfileUiState())
    val profileUiState: LiveData<ProfileUiState> = _profileUiState

    fun action(profileIntent: ProfileIntent) {
        when (profileIntent) {
            is ProfileIntent.GetOrigin -> getOrigin()
            is ProfileIntent.LogOut -> pushSideEffect(ProfileSideEffects.NavigateToLogin)
        }
    }

    private fun getOrigin() {
        _profileUiState.value = profileUiState.value?.copy(loading = true)
        viewModelScope.launch {
            val origin = userRepository.getOrigin().first()
            if (origin != null) {
                _profileUiState.value = profileUiState.value?.copy(origin = origin, loading = false)
            }
        }
    }

    private fun pushSideEffect(navigateTo: ProfileSideEffects) {
        _sideEffect.value = Event(navigateTo)
    }
}

data class ProfileUiState(
    val loading: Boolean = false,
    val origin: String = ""
) : UiState

sealed class ProfileIntent : UserIntent {
    object LogOut : ProfileIntent()
    object GetOrigin : ProfileIntent()
}

sealed class ProfileSideEffects : SideEffect {
    class Feedback(val msg: String) : ProfileSideEffects()
    object NavigateToLogin : ProfileSideEffects()
}