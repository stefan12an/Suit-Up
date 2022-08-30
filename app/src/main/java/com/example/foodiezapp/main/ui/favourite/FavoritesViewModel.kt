package com.example.foodiezapp.main.ui.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodiezapp.common.Event
import com.example.foodiezapp.common.SideEffect
import com.example.foodiezapp.common.UiState
import com.example.foodiezapp.common.UserIntent
import com.example.foodiezapp.main.data.model.Restaurant
import com.example.foodiezapp.main.data.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val roomRepository: RoomRepository) :
    ViewModel() {

    private val _sideEffect = MutableLiveData<Event<FavoritesSideEffects>>()
    val sideEffect: LiveData<Event<FavoritesSideEffects>> = _sideEffect

    private val _favoritesUiState = MutableLiveData(FavoritesUiState())
    val favoritesUiState: LiveData<FavoritesUiState> = _favoritesUiState

    fun action(favoritesIntent: FavoritesIntent) {
        when (favoritesIntent) {
            is FavoritesIntent.GetData -> getDbRestaurantData()
            is FavoritesIntent.AddToFavorites -> handleFavoritesRequest(favoritesIntent.restaurant)
            is FavoritesIntent.OpenDetails -> {
                pushSideEffect(FavoritesSideEffects.NavigateToDetails(favoritesIntent.restaurantId))
            }
        }
    }

    private fun getDbRestaurantData() {
        _favoritesUiState.value = favoritesUiState.value?.copy(loading = true)
        viewModelScope.launch {
            val result = roomRepository.loadRestaurantsFromDb()
            _favoritesUiState.value = favoritesUiState.value?.copy(
                restaurantsAll = result.map { it.mapToModel() },
                loading = false
            )
        }
    }

    private fun handleFavoritesRequest(restaurant: Restaurant) {
        _favoritesUiState.value = favoritesUiState.value?.copy(loading = true)
        viewModelScope.launch {
            if (restaurant.isFavorite != true) {
                roomRepository.addRestaurantToDb(restaurant.mapToEntity())
                _favoritesUiState.value?.restaurantsAll?.find { it.id == restaurant.id }?.isFavorite =
                    true
            } else {
                roomRepository.deleteRestaurantFromDb(restaurant.id)
                val tempList = _favoritesUiState.value?.restaurantsAll?.toMutableList()
                tempList?.remove(restaurant)
                _favoritesUiState.value =
                    favoritesUiState.value?.copy(restaurantsAll = tempList?.toList())
            }
            _favoritesUiState.value = favoritesUiState.value?.copy(loading = false)
        }
    }

    private fun pushSideEffect(navigateTo: FavoritesSideEffects) {
        _sideEffect.value = Event(navigateTo)
    }
}

data class FavoritesUiState(
    val loading: Boolean = false,
    val restaurantsAll: List<Restaurant>? = null
) : UiState

sealed class FavoritesIntent : UserIntent {
    object GetData : FavoritesIntent()
    class OpenDetails(val restaurantId: String) : FavoritesIntent()
    class AddToFavorites(val restaurant: Restaurant) : FavoritesIntent()
}

sealed class FavoritesSideEffects : SideEffect {
    class Feedback(val msg: String) : FavoritesSideEffects()
    class NavigateToDetails(val restaurantId: String) : FavoritesSideEffects()
}