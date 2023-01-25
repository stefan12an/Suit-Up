package com.example.suitup.main.ui.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suitup.common.Event
import com.example.suitup.common.SideEffect
import com.example.suitup.common.UiState
import com.example.suitup.common.UserIntent
import com.example.suitup.main.data.model.Store
import com.example.suitup.main.data.repository.RoomRepository
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
            is FavoritesIntent.GetData -> getDbStoreData()
            is FavoritesIntent.AddToFavorites -> handleFavoritesRequest(favoritesIntent.store)
            is FavoritesIntent.OpenDetails -> {
                pushSideEffect(FavoritesSideEffects.NavigateToDetails(favoritesIntent.storeId))
            }
        }
    }

    private fun getDbStoreData() {
        _favoritesUiState.value = favoritesUiState.value?.copy(loading = true)
        viewModelScope.launch {
            val result = roomRepository.loadStoresFromDb()
            _favoritesUiState.value = favoritesUiState.value?.copy(
                storesAll = result.map { it.mapToModel() },
                loading = false
            )
        }
    }

    private fun handleFavoritesRequest(store: Store) {
        _favoritesUiState.value = favoritesUiState.value?.copy(loading = true)
        viewModelScope.launch {
            if (store.isFavorite != true) {
                roomRepository.addStoreToDb(store.mapToEntity())
                _favoritesUiState.value?.storesAll?.find { it.id == store.id }?.isFavorite =
                    true
            } else {
                roomRepository.deleteStoreFromDb(store.id)
                val tempList = _favoritesUiState.value?.storesAll?.toMutableList()
                tempList?.remove(store)
                _favoritesUiState.value =
                    favoritesUiState.value?.copy(storesAll = tempList?.toList())
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
    val storesAll: List<Store>? = null
) : UiState

sealed class FavoritesIntent : UserIntent {
    object GetData : FavoritesIntent()
    class OpenDetails(val storeId: String) : FavoritesIntent()
    class AddToFavorites(val store: Store) : FavoritesIntent()
}

sealed class FavoritesSideEffects : SideEffect {
    class Feedback(val msg: String) : FavoritesSideEffects()
    class NavigateToDetails(val storeId: String) : FavoritesSideEffects()
}