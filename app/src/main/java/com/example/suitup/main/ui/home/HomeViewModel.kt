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
import com.example.suitup.main.data.model.Restaurant
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
            is HomeIntent.OpenDetails -> pushSideEffect(HomeSideEffects.NavigateToDetails(homeIntent.restaurantId))
            is HomeIntent.GetSearchResult -> getYelpApiSearchResponse(homeIntent.searchInput)
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
                val searchResult = yelpApiRepository.getRestaurantsSearch(currentLocation, search)
                _homeUiState.value = homeUiState.value?.copy(
                    restaurantsSearch = searchResult.data?.restaurants,
                    loading = false
                )
                pushSideEffect(
                    HomeSideEffects.NavigateToSeeAll(
                        HomeFragmentDirections.actionHomeFragmentToSeeAllFragment(
                            search.toString()
                        )
                    )
                )
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
                val nearByResult = yelpApiRepository.getRestaurantsNearBy(location, NEARBY_LIMIT)
                val hotNewResult = yelpApiRepository.getRestaurantsHotNew(location, HOT_NEW_LIMIT)
                val dealsResult = yelpApiRepository.getRestaurantsDeals(location, DEALS_LIMIT)
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
                    restaurantsNearBy = nearByResult.data?.restaurants,
                    restaurantsHotNew = hotNewResult.data?.restaurants,
                    restaurantsDeals = dealsResult.data?.restaurants,
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
    val restaurantsSearch: List<Restaurant>? = null,
    val restaurantsNearBy: List<Restaurant>? = null,
    val restaurantsHotNew: List<Restaurant>? = null,
    val restaurantsDeals: List<Restaurant>? = null
) : UiState

sealed class HomeIntent : UserIntent {
    class SeeAll(val directions: NavDirections) : HomeIntent()
    object GetData : HomeIntent()
    class GetSearchResult(val searchInput: CharSequence) : HomeIntent()
    class OpenDetails(val restaurantId: String) : HomeIntent()

}

sealed class HomeSideEffects : SideEffect {
    class Feedback(val msg: String) : HomeSideEffects()
    class NavigateToSeeAll(val directions: NavDirections) : HomeSideEffects()
    class NavigateToDetails(val restaurantId: String) : HomeSideEffects()
    object NavigateToRequest : HomeSideEffects()
}