package com.example.suitup.main.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.example.suitup.common.*
import com.example.suitup.common.Constants.DEALS_LIMIT
import com.example.suitup.common.Constants.HOT_NEW_LIMIT
import com.example.suitup.common.Constants.NEARBY_LIMIT
import com.example.suitup.main.data.model.CurrentLocation
import com.example.suitup.main.data.model.Store
import com.example.suitup.main.data.repository.LocationRepository
import com.example.suitup.main.data.repository.YelpApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val yelpApiRepository: YelpApiRepository,
) :
    ViewModel() {
    private val _sideEffect = MutableLiveData<Event<HomeSideEffects>>()
    val sideEffect: LiveData<Event<HomeSideEffects>> = _sideEffect

    private val _homeUiState = MutableLiveData(HomeUiState())
    val homeUiState: LiveData<HomeUiState> = _homeUiState

    fun action(homeIntent: HomeIntent) {
        when (homeIntent) {
            is HomeIntent.SeeAll -> pushSideEffect(HomeSideEffects.NavigateToSeeAll(homeIntent.directions))
            is HomeIntent.GetData -> getHomeNecessaryData()
            is HomeIntent.OpenDetails -> pushSideEffect(HomeSideEffects.NavigateToDetails(homeIntent.storeId))
            is HomeIntent.GetSearchResult -> getYelpApiSearchResponse(homeIntent.searchInput)
            is HomeIntent.GoToPhotoSearch -> pushSideEffect(HomeSideEffects.GoToPhotoSearch)
        }
    }

    private fun getYelpApiSearchResponse(search: CharSequence) {
        _homeUiState.value = homeUiState.value?.copy(loading = true)
        viewModelScope.launch {
            val currentLocation = getLocation()
            if (currentLocation == null) {
                pushSideEffect(HomeSideEffects.NavigateToRequest)
                return@launch
            } else {
                val searchResult = yelpApiRepository.getStoreSearch(currentLocation, search)
                _homeUiState.value = homeUiState.value?.copy(
                    storesSearches = searchResult.data?.stores,
                    loading = false
                )
                if(searchResult.data != null) {
                    pushSideEffect(
                        HomeSideEffects.NavigateToSeeAll(
                            HomeFragmentDirections.actionHomeFragmentToSeeAllFragment(
                                search.toString()
                            )
                        )
                    )
                }else{
                    pushSideEffect(HomeSideEffects.Feedback(searchResult.message ?: "Didn't get a messageB"))
                }
            }
        }
    }

    private fun getHomeNecessaryData() {
        _homeUiState.value = homeUiState.value?.copy(loading = true)
        viewModelScope.launch {
            val location = getLocation()
            if (location == null) {
                return@launch
            } else {
                val nearByResult = yelpApiRepository.getStoresNearBy(location, NEARBY_LIMIT)
                val hotNewResult = yelpApiRepository.getStoresHotNew(location, HOT_NEW_LIMIT)
                val dealsResult = yelpApiRepository.getStoresDeals(location, DEALS_LIMIT)
                for (response in listOf(nearByResult, hotNewResult, dealsResult)) {
                    if (response.status == Status.ERROR) {
                        pushSideEffect(
                            HomeSideEffects.Feedback(
                                response.message ?: "Didn't get a message"
                            )
                        )
                    }
                }
                _homeUiState.value = homeUiState.value?.copy(
                    storesNearBy = nearByResult.data?.stores,
                    storesHotNew = hotNewResult.data?.stores,
                    storesDeals = dealsResult.data?.stores,
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

    private fun pushSideEffect(navigateTo: HomeSideEffects) {
        _sideEffect.value = Event(navigateTo)
    }
}

data class HomeUiState(
    val loading: Boolean = false,
    val storesSearches: List<Store>? = null,
    val storesNearBy: List<Store>? = null,
    val storesHotNew: List<Store>? = null,
    val storesDeals: List<Store>? = null
) : UiState

sealed class HomeIntent : UserIntent {
    class SeeAll(val directions: NavDirections) : HomeIntent()
    object GetData : HomeIntent()
    class GetSearchResult(val searchInput: CharSequence) : HomeIntent()
    class OpenDetails(val storeId: String) : HomeIntent()
    object GoToPhotoSearch : HomeIntent()
}

sealed class HomeSideEffects : SideEffect {
    class Feedback(val msg: String) : HomeSideEffects()
    class NavigateToSeeAll(val directions: NavDirections) : HomeSideEffects()
    class NavigateToDetails(val storeId: String) : HomeSideEffects()
    object GoToPhotoSearch : HomeSideEffects()
    object NavigateToRequest : HomeSideEffects()
}