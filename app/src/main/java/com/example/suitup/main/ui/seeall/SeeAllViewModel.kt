package com.example.suitup.main.ui.seeall

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suitup.common.*
import com.example.suitup.main.data.model.CurrentLocation
import com.example.suitup.main.data.model.Store
import com.example.suitup.main.data.model.yelp.YelpSearchResult
import com.example.suitup.main.data.repository.LocationRepository
import com.example.suitup.main.data.repository.RoomRepository
import com.example.suitup.main.data.repository.YelpApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeeAllViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val yelpApiRepository: YelpApiRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {
    private val _sideEffect = MutableLiveData<Event<SeeAllSideEffects>>()
    val sideEffect: LiveData<Event<SeeAllSideEffects>> = _sideEffect

    private val _seeAllUiState = MutableLiveData(SeeAllUiState())
    val seeAllUiState: LiveData<SeeAllUiState> = _seeAllUiState

    fun action(seeAllIntent: SeeAllIntent) {
        when (seeAllIntent) {
            is SeeAllIntent.GetData -> loadAllFilteredStores(
                seeAllIntent.attribute,
                seeAllIntent.priceFilter,
                seeAllIntent.orderFilter
            )
            is SeeAllIntent.AddToFavorites -> handleFavoritesRequest(seeAllIntent.store)
            is SeeAllIntent.Filter -> pushSideEffect(SeeAllSideEffects.NavigateToFilter)
            is SeeAllIntent.OpenDetails -> pushSideEffect(
                SeeAllSideEffects.NavigateToDetails(
                    seeAllIntent.storeId
                )
            )
        }
    }

    private fun loadAllFilteredStores(
        attributes: String?,
        priceFilter: String? = null,
        orderFilter: String? = null
    ) {
        _seeAllUiState.value = seeAllUiState.value?.copy(loading = true)
        viewModelScope.launch {
            val storesDb = roomRepository.loadStoreIdsFromDb()
            Log.e(TAG, "loadAllFilteredStores: $storesDb")
            val currentLocation = getLocation()
            if (currentLocation == null) {
                pushSideEffect(SeeAllSideEffects.NavigateToRequest)
                return@launch
            } else {
                val searchResult: Resource<YelpSearchResult>
                if (Constants.ATTRIBUTES.containsKey(attributes)) {
                    searchResult =
                        yelpApiRepository.getAllFilteredStores(
                            currentLocation,
                            attribute = attributes,
                            orderFilter = orderFilter,
                            priceFilter = priceFilter
                        )
                } else {
                    searchResult = yelpApiRepository.getAllFilteredStores(
                        currentLocation,
                        searchTerm = attributes,
                        orderFilter = orderFilter,
                        priceFilter = priceFilter
                    )
                }
                searchResult.data?.stores?.filter {
                    storesDb.contains(it.id)
                }?.forEach { it.isFavorite = true }
                _seeAllUiState.value = seeAllUiState.value?.copy(
                    storesAll = (if (orderFilter != null) {
                        orderResults(orderFilter, searchResult.data)
                    } else {
                        searchResult.data?.stores
                    }),
                    loading = false
                )
            }
        }
    }

    private fun handleFavoritesRequest(store: Store) {
        _seeAllUiState.value = seeAllUiState.value?.copy(loading = true)
        viewModelScope.launch {
            if (store.isFavorite != true) {
                roomRepository.addStoreToDb(store.mapToEntity())
                _seeAllUiState.value?.storesAll?.find { it.id == store.id }?.isFavorite =
                    true
            } else {
                roomRepository.deleteStoreFromDb(store.id)
                _seeAllUiState.value?.storesAll?.find { it.id == store.id }?.isFavorite =
                    false
            }
            _seeAllUiState.value = seeAllUiState.value?.copy(loading = false)
        }
    }

    private fun orderResults(
        orderFilter: String,
        searchResult: YelpSearchResult?
    ): List<Store>? {
        return when (orderFilter) {
            "close" -> searchResult?.stores
            "asc" -> searchResult?.stores?.sortedBy { it.price }
            "desc" -> searchResult?.stores?.sortedByDescending { it.price }
            else -> {
                throw IllegalStateException()
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

    private fun pushSideEffect(navigateTo: SeeAllSideEffects) {
        _sideEffect.value = Event(navigateTo)
    }
}

data class SeeAllUiState(
    val loading: Boolean = false,
    val storesAll: List<Store>? = null,
    val priceFilter: String? = null,
    val orderFilter: String? = null
) : UiState

sealed class SeeAllIntent : UserIntent {
    class GetData(
        val attribute: String?,
        val priceFilter: String? = null,
        val orderFilter: String? = null
    ) : SeeAllIntent()

    class AddToFavorites(val store: Store) : SeeAllIntent()
    object Filter : SeeAllIntent()
    class OpenDetails(val storeId: String) : SeeAllIntent()
}

sealed class SeeAllSideEffects : SideEffect {
    class Feedback(val msg: String) : SeeAllSideEffects()
    class NavigateToDetails(val storeId: String) : SeeAllSideEffects()
    object NavigateToRequest : SeeAllSideEffects()
    object NavigateToFilter : SeeAllSideEffects()
}