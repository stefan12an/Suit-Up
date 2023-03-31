package com.example.suitup.main.ui.photosearch

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.example.suitup.common.*
import com.example.suitup.main.data.model.CurrentLocation
import com.example.suitup.main.data.model.Store
import com.example.suitup.main.data.model.ml.Classifier
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
            is PhotoSearchIntent.Predict -> getImageCombinedPrediction(
                photoSearchIntent.bitmap,
                photoSearchIntent.classifier
            )
            is PhotoSearchIntent.GetSearchResult -> getYelpApiSearchResponse(photoSearchIntent.label)
        }
    }

    private fun getImageCombinedPrediction(bitmap: Bitmap, classifier: Classifier) {
        _photoSearchUiState.value = photoSearchUiState.value?.copy(predictionsLoading = true)
        viewModelScope.launch {
            val response = classifier.predictCombine(bitmap)
            val type = response.first as String
            val colors = response.second as List<*>
            val predictions = colors.map { "$it $type" }.toList()
            _photoSearchUiState.value = photoSearchUiState.value?.copy(
                predictions = predictions,
                predictionsLoading = false
            )
        }
    }

    private fun getYelpApiSearchResponse(label: CharSequence) {
        _photoSearchUiState.value =
            photoSearchUiState.value?.copy(loading = true, predictionsLoading = null)
        viewModelScope.launch {
            val currentLocation = getLocation()
            if (currentLocation == null) {
                pushSideEffect(PhotoSearchSideEffects.NavigateToRequest)
                return@launch
            } else {
                val searchResult = yelpApiRepository.getStoreSearch(currentLocation, label)
                _photoSearchUiState.value = photoSearchUiState.value?.copy(
                    storesSearches = searchResult.data?.stores,
                    loading = false,
                    predictionsLoading = null
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
                            searchResult.message ?: "Didn't get a message"
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
    val predictionsLoading: Boolean? = null,
    val storesSearches: List<Store>? = null,
    val predictions: List<String>? = null
) : UiState

sealed class PhotoSearchIntent : UserIntent {
    class SeeAll(val directions: NavDirections) : PhotoSearchIntent()
    class Predict(val bitmap: Bitmap, val classifier: Classifier) : PhotoSearchIntent()
    class GetSearchResult(val label: CharSequence) : PhotoSearchIntent()
}

sealed class PhotoSearchSideEffects : SideEffect {
    class Feedback(val msg: String) : PhotoSearchSideEffects()
    class NavigateToSeeAll(val directions: NavDirections) : PhotoSearchSideEffects()
    object NavigateToRequest : PhotoSearchSideEffects()
}