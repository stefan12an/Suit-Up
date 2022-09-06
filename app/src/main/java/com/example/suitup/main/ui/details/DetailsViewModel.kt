package com.example.suitup.main.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suitup.common.Event
import com.example.suitup.common.SideEffect
import com.example.suitup.common.UiState
import com.example.suitup.common.UserIntent
import com.example.suitup.main.data.model.Restaurant
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
            is DetailsIntent.GetData -> loadRestaurantData(detailsIntent.restaurantId)
            is DetailsIntent.OpenPhoto -> pushSideEffect(
                DetailsSideEffects.NavigateToPhoto(
                    detailsIntent.photo
                )
            )
            is DetailsIntent.AddToFavorites -> handleFavoritesRequest()
        }
    }

    private fun handleFavoritesRequest() {
        _detailsUiState.value = detailsUiState.value?.copy(loading = true)
        val restaurant = detailsUiState.value?.restaurant
        viewModelScope.launch {
            if (restaurant?.isFavorite != true) {
                restaurant?.mapToEntity()?.let { roomRepository.addRestaurantToDb(it) }
                restaurant?.isFavorite = true
            } else {
                roomRepository.deleteRestaurantFromDb(restaurant.id)
                restaurant.isFavorite = false
            }
            _detailsUiState.value =
                detailsUiState.value?.copy(restaurant = restaurant, loading = false)
        }
    }

    private fun loadRestaurantData(restaurantId: String?) {
        _detailsUiState.value = detailsUiState.value?.copy(loading = true)
        viewModelScope.launch {
            val restaurantsDb = roomRepository.loadRestaurantIdsFromDb()
            val searchResult = yelpApiRepository.getRestaurantData(restaurantId)
            val reviewResult = yelpApiRepository.getRestaurantReviews(restaurantId)
            if (restaurantsDb.contains(searchResult.data?.id)) {
                searchResult.data?.isFavorite = true
            }
            _detailsUiState.value =
                detailsUiState.value?.copy(
                    restaurant = searchResult.data,
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
    val restaurant: Restaurant? = null,
    val reviews: List<YelpReview>? = null
) : UiState

sealed class DetailsIntent : UserIntent {
    class GetData(val restaurantId: String?) : DetailsIntent()
    class OpenPhoto(val photo: String?) : DetailsIntent()
    object AddToFavorites : DetailsIntent()
}

sealed class DetailsSideEffects : SideEffect {
    class Feedback(val msg: String) : DetailsSideEffects()
    class NavigateToPhoto(val photo: String?) : DetailsSideEffects()
}