package com.example.suitup.main.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suitup.common.*
import com.example.suitup.main.data.model.CurrentLocation
import com.example.suitup.main.data.model.Store
import com.example.suitup.main.data.repository.LocationRepository
import com.example.suitup.main.data.repository.YelpApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val yelpApiRepository: YelpApiRepository
) :
    ViewModel() {
    private val _sideEffect = MutableLiveData<Event<MapsSideEffects>>()
    val sideEffect: LiveData<Event<MapsSideEffects>> = _sideEffect

    private val _mapsUiState = MutableLiveData(MapsUiState())
    val mapsUiState: LiveData<MapsUiState> = _mapsUiState

    fun action(mapsIntent: MapsIntent) {
        when (mapsIntent) {
            is MapsIntent.GetData -> loadAllNearbyStores()
            is MapsIntent.OpenDetails -> pushSideEffect(MapsSideEffects.NavigateToDetails(mapsIntent.storeId))
        }
    }

    private fun loadAllNearbyStores() {
        _mapsUiState.value = mapsUiState.value?.copy(loading = true)
        viewModelScope.launch {
            val currentLocation = getLocation()
            if (currentLocation == null) {
                pushSideEffect(MapsSideEffects.NavigateToRequest)
                return@launch
            } else {
                val searchResult = yelpApiRepository.getAllFilteredStores(currentLocation)
                _mapsUiState.value = mapsUiState.value?.copy(
                    storesAll = searchResult.data?.stores,
                    location = currentLocation,
                    loading = false
                )
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

    private fun pushSideEffect(navigateTo: MapsSideEffects) {
        _sideEffect.value = Event(navigateTo)
    }
}

data class MapsUiState(
    val loading: Boolean = false,
    val location: CurrentLocation? = null,
    val storesAll: List<Store>? = null
) : UiState

sealed class MapsIntent : UserIntent {
    object GetData : MapsIntent()
    class OpenDetails(val storeId: String) : MapsIntent()
}

sealed class MapsSideEffects : SideEffect {
    class Feedback(val msg: String) : MapsSideEffects()
    object NavigateToRequest : MapsSideEffects()
    class NavigateToDetails(val storeId: String) : MapsSideEffects()
}