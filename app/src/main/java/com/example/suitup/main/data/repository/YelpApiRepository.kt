package com.example.suitup.main.data.repository

import com.example.suitup.common.Constants
import com.example.suitup.common.Resource
import com.example.suitup.common.Status
import com.example.suitup.main.data.model.CurrentLocation
import com.example.suitup.main.data.model.Restaurant
import com.example.suitup.main.data.model.yelp.YelpReviewResult
import com.example.suitup.main.data.model.yelp.YelpSearchResult
import com.example.suitup.main.data.network.YelpApiInterface
import javax.inject.Inject

interface YelpApiRepository {
    suspend fun getRestaurantData(restaurantId: String?): Resource<Restaurant>

    suspend fun getRestaurantReviews(restaurantId: String?): Resource<YelpReviewResult>

    suspend fun getRestaurantsSearch(
        currentLocation: CurrentLocation,
        searchInput: CharSequence
    ): Resource<YelpSearchResult>

    suspend fun getRestaurantsNearBy(
        currentLocation: CurrentLocation,
        limit: Int
    ): Resource<YelpSearchResult>

    suspend fun getRestaurantsHotNew(
        currentLocation: CurrentLocation,
        limit: Int
    ): Resource<YelpSearchResult>

    suspend fun getRestaurantsDeals(
        currentLocation: CurrentLocation,
        limit: Int
    ): Resource<YelpSearchResult>

    suspend fun getAllFilteredRestaurants(
        currentLocation: CurrentLocation,
        searchTerm: String? = null,
        attribute: String? = null,
        orderFilter: String? = null,
        priceFilter: String? = null,
    ): Resource<YelpSearchResult>
}

class YelpApiRepositoryImpl @Inject constructor(private val yelpApiInterface: YelpApiInterface) :
    YelpApiRepository {

    override suspend fun getRestaurantData(restaurantId: String?): Resource<Restaurant> {
        val response = yelpApiInterface.getRestaurant(
            "Bearer ${Constants.YELP_API_KEY}",
            restaurantId
        )
        return if (response.isSuccessful) {
            Resource(Status.SUCCESS, response.body())
        } else {
            Resource(Status.ERROR)
        }
    }

    override suspend fun getRestaurantReviews(restaurantId: String?): Resource<YelpReviewResult> {
        val response = yelpApiInterface.getRestaurantReviews(
            "Bearer ${Constants.YELP_API_KEY}",
            restaurantId
        )
        return if (response.isSuccessful) {
            Resource(Status.SUCCESS, response.body())
        } else {
            Resource(Status.ERROR)
        }
    }

    override suspend fun getRestaurantsSearch(
        currentLocation: CurrentLocation,
        searchInput: CharSequence
    ): Resource<YelpSearchResult> {
        val response = yelpApiInterface.getRestaurants(
            "Bearer ${Constants.YELP_API_KEY}",
            currentLocation.latitude,
            currentLocation.longitude,
            searchTerm = searchInput.toString()
        )
        return if (response.isSuccessful) {
            Resource(Status.SUCCESS, response.body())
        } else {
            Resource(Status.ERROR)
        }
    }

    override suspend fun getRestaurantsNearBy(
        currentLocation: CurrentLocation,
        limit: Int
    ): Resource<YelpSearchResult> {
        val response = yelpApiInterface.getRestaurants(
            "Bearer ${Constants.YELP_API_KEY}",
            currentLocation.latitude,
            currentLocation.longitude,
            limit = limit
        )
        return if (response.isSuccessful) {
            Resource(Status.SUCCESS, response.body())
        } else {
            Resource(Status.ERROR, message = "Couldn't load data for the Near By section")
        }
    }

    override suspend fun getRestaurantsHotNew(
        currentLocation: CurrentLocation,
        limit: Int
    ): Resource<YelpSearchResult> {
        val response = yelpApiInterface.getRestaurants(
            "Bearer ${Constants.YELP_API_KEY}",
            currentLocation.latitude,
            currentLocation.longitude,
            limit = limit,
            attributes = "hot_and_new"
        )
        return if (response.isSuccessful) {
            Resource(Status.SUCCESS, response.body())
        } else {
            Resource(Status.ERROR, message = "Couldn't load data for the Hot and New section")
        }
    }

    override suspend fun getRestaurantsDeals(
        currentLocation: CurrentLocation,
        limit: Int
    ): Resource<YelpSearchResult> {
        val response = yelpApiInterface.getRestaurants(
            "Bearer ${Constants.YELP_API_KEY}",
            currentLocation.latitude,
            currentLocation.longitude,
            limit = limit,
            attributes = "deals"
        )
        return if (response.isSuccessful) {
            Resource(Status.SUCCESS, response.body())
        } else {
            Resource(Status.ERROR, message = "Couldn't load data for the Deals section")
        }
    }

    override suspend fun getAllFilteredRestaurants(
        currentLocation: CurrentLocation,
        searchTerm: String?,
        attribute: String?,
        orderFilter: String?,
        priceFilter: String?
    ): Resource<YelpSearchResult> {
        val response = yelpApiInterface.getRestaurants(
            "Bearer ${Constants.YELP_API_KEY}",
            currentLocation.latitude,
            currentLocation.longitude,
            price = priceFilter,
            attributes = attribute,
            searchTerm = searchTerm
        )
        return if (response.isSuccessful) {
            Resource(Status.SUCCESS, response.body())
        } else {
            Resource(Status.ERROR, message = "Couldn't load data for the specified filters")
        }
    }
}