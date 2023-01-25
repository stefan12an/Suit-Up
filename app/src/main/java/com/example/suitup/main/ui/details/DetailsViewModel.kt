package com.example.suitup.main.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suitup.common.Event
import com.example.suitup.common.SideEffect
import com.example.suitup.common.UiState
import com.example.suitup.common.UserIntent
import com.example.suitup.main.data.model.Store
import com.example.suitup.main.data.model.yelp.YelpReview
import com.example.suitup.main.data.repository.RoomRepository
import com.example.suitup.main.data.repository.YelpApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val yelpApiRepository: YelpApiRepository,
    private val roomRepository: RoomRepository
) :
    ViewModel() {
    private val _sideEffect = MutableLiveData<Event<DetailsSideEffects>>()
    val sideEffect: LiveData<Event<DetailsSideEffects>> = _sideEffect

    private val _detailsUiState = MutableLiveData(DetailsUiState())
    val detailsUiState: LiveData<DetailsUiState> = _detailsUiState

    fun action(detailsIntent: DetailsIntent) {
        when (detailsIntent) {
            is DetailsIntent.GetData -> loadStoreData(detailsIntent.storeId)
            is DetailsIntent.OpenPhoto -> pushSideEffect(
                DetailsSideEffects.NavigateToPhoto(
                    detailsIntent.photo
                )
            )
            is DetailsIntent.AddToFavorites -> handleFavoritesRequest()
            is DetailsIntent.GoToPhotoSearch -> pushSideEffect(DetailsSideEffects.GoToPhotoSearch)
        }
    }

    private fun handleFavoritesRequest() {
        _detailsUiState.value = detailsUiState.value?.copy(loading = true)
        val store = detailsUiState.value?.store
        viewModelScope.launch {
            if (store?.isFavorite != true) {
                store?.mapToEntity()?.let { roomRepository.addStoreToDb(it) }
                store?.isFavorite = true
            } else {
                roomRepository.deleteStoreFromDb(store.id)
                store.isFavorite = false
            }
            _detailsUiState.value =
                detailsUiState.value?.copy(store = store, loading = false)
        }
    }

    private fun loadStoreData(storeId: String?) {
        _detailsUiState.value = detailsUiState.value?.copy(loading = true)
        viewModelScope.launch {
            val storesDb = roomRepository.loadStoreIdsFromDb()
            val searchResult = yelpApiRepository.getStoreData(storeId)
            val reviewResult = yelpApiRepository.getStoreReviews(storeId)
            if (storesDb.contains(searchResult.data?.id)) {
                searchResult.data?.isFavorite = true
            }
            _detailsUiState.value =
                detailsUiState.value?.copy(
                    store = searchResult.data,
                    reviews = reviewResult.data?.reviews,
                    loading = false
                )
        }
    }

    private fun pushSideEffect(navigateTo: DetailsSideEffects) {
        _sideEffect.value = Event(navigateTo)
    }
}

data class DetailsUiState(
    val loading: Boolean = false,
    val store: Store? = null,
    val reviews: List<YelpReview>? = null
) : UiState

sealed class DetailsIntent : UserIntent {
    class GetData(val storeId: String?) : DetailsIntent()
    class OpenPhoto(val photo: String?) : DetailsIntent()
    object AddToFavorites : DetailsIntent()
    object GoToPhotoSearch : DetailsIntent()
}

sealed class DetailsSideEffects : SideEffect {
    class Feedback(val msg: String) : DetailsSideEffects()
    class NavigateToPhoto(val photo: String?) : DetailsSideEffects()
    object GoToPhotoSearch : DetailsSideEffects()
}