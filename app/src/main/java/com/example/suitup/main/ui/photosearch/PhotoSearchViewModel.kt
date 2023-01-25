package com.example.suitup.main.ui.photosearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.example.suitup.common.*
import com.example.suitup.main.data.model.CurrentLocation
import com.example.suitup.main.data.model.Store
import com.example.suitup.main.data.repository.LocationRepository
import com.example.suitup.main.data.repository.YelpApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoSearchViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val yelpApiRepository: YelpApiRepository,
) : ViewModel() {

    private val _sideEffect = MutableLiveData<Event<PhotoSearchSideEffects>>()
    val sideEffect: LiveData<Event<PhotoSearchSideEffects>> = _sideEffect

    private val _photoSearchUiState = MutableLiveData(PhotoSearchUiState())
    val photoSearchUiState: LiveData<PhotoSearchUiState> = _photoSearchUiState

    fun action(photoSearchIntent: PhotoSearchIntent) {
        when (photoSearchIntent) {
            is PhotoSearchIntent.SeeAll -> pushSideEffect(
                PhotoSearchSideEffects.NavigateToSeeAll(
                    photoSearchIntent.directions
                )
            )
            is PhotoSearchIntent.GetSearchResult -> getYelpApiSearchResponse(photoSearchIntent.label)
        }
    }

    private fun getYelpApiSearchResponse(label: CharSequence) {
        _photoSearchUiState.value = photoSearchUiState.value?.copy(loading = true)
        viewModelScope.launch {
            val currentLocation = getLocation()
            if (currentLocation == null) {
                pushSideEffect(PhotoSearchSideEffects.NavigateToRequest)
                return@launch
            } else {
                val searchResult = yelpApiRepository.getStoreSearch(currentLocation, label)
                _photoSearchUiState.value = photoSearchUiState.value?.copy(
                    storesSearches = searchResult.data?.stores,
                    loading = false
                )
                if (searchResult.data != null) {
                    pushSideEffect(
                        PhotoSearchSideEffects.NavigateToSeeAll(
                            PhotoSearchFragmentDirections.actionPhotoSearchFragmentToSeeAllFragment(
                                label.toString()
                            )
                        )
                    )
                } else {
                    pushSideEffect(
                        PhotoSearchSideEffects.Feedback(
                            searchResult.message ?: "Didn't get a messageB"
                        )
                    )
                }
            }
        }
    }

    private suspend fun getLocation(): CurrentLocation? {
        val permission = locationRepository.checkForPermission()
        return if (permission.status == Status.GRANTED) {
            val result = locationRepository.getCurrentLocation()
            CurrentLocation(result.latitude, result.longitude)
        } else {
            null
        }
    }

    private fun pushSideEffect(navigateTo: PhotoSearchSideEffects) {
        _sideEffect.value = Event(navigateTo)
    }
}

data class PhotoSearchUiState(
    val loading: Boolean = false,
    val storesSearches: List<Store>? = null
) : UiState

sealed class PhotoSearchIntent : UserIntent {
    class SeeAll(val directions: NavDirections) : PhotoSearchIntent()
    class GetSearchResult(val label: CharSequence) : PhotoSearchIntent()
}

sealed class PhotoSearchSideEffects : SideEffect {
    class Feedback(val msg: String) : PhotoSearchSideEffects()
    class NavigateToSeeAll(val directions: NavDirections) : PhotoSearchSideEffects()
    object NavigateToRequest : PhotoSearchSideEffects()
}