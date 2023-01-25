package com.example.suitup.main.data.repository

import com.example.suitup.common.Constants
import com.example.suitup.common.Resource
import com.example.suitup.common.Status
import com.example.suitup.main.data.model.CurrentLocation
import com.example.suitup.main.data.model.Store
import com.example.suitup.main.data.model.yelp.YelpReviewResult
import com.example.suitup.main.data.model.yelp.YelpSearchResult
import com.example.suitup.main.data.network.YelpApiInterface
import javax.inject.Inject

interface YelpApiRepository {
    suspend fun getStoreData(storeId: String?): Resource<Store>

    suspend fun getStoreReviews(storeId: String?): Resource<YelpReviewResult>

    suspend fun getStoreSearch(
        currentLocation: CurrentLocation,
        searchInput: CharSequence
    ): Resource<YelpSearchResult>

    suspend fun getStoresNearBy(
        currentLocation: CurrentLocation,
        limit: Int
    ): Resource<YelpSearchResult>

    suspend fun getStoresHotNew(
        currentLocation: CurrentLocation,
        limit: Int
    ): Resource<YelpSearchResult>

    suspend fun getStoresDeals(
        currentLocation: CurrentLocation,
        limit: Int
    ): Resource<YelpSearchResult>

    suspend fun getAllFilteredStores(
        currentLocation: CurrentLocation,
        searchTerm: String? = null,
        attribute: String? = null,
        orderFilter: String? = null,
        priceFilter: String? = null,
    ): Resource<YelpSearchResult>
}

class YelpApiRepositoryImpl @Inject constructor(private val yelpApiInterface: YelpApiInterface) :
    YelpApiRepository {

    override suspend fun getStoreData(storeId: String?): Resource<Store> {
        val response = yelpApiInterface.getStore(
            "Bearer ${Constants.YELP_API_KEY}",
            storeId
        )
        return if (response.isSuccessful) {
            Resource(Status.SUCCESS, response.body())
        } else {
            Resource(Status.ERROR)
        }
    }

    override suspend fun getStoreReviews(storeId: String?): Resource<YelpReviewResult> {
        val response = yelpApiInterface.getStoreReviews(
            "Bearer ${Constants.YELP_API_KEY}",
            storeId
        )
        return if (response.isSuccessful) {
            Resource(Status.SUCCESS, response.body())
        } else {
            Resource(Status.ERROR)
        }
    }

    override suspend fun getStoreSearch(
        currentLocation: CurrentLocation,
        searchInput: CharSequence
    ): Resource<YelpSearchResult> {
        val response = yelpApiInterface.getStores(
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

    override suspend fun getStoresNearBy(
        currentLocation: CurrentLocation,
        limit: Int
    ): Resource<YelpSearchResult> {
        val response = yelpApiInterface.getStores(
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

    override suspend fun getStoresHotNew(
        currentLocation: CurrentLocation,
        limit: Int
    ): Resource<YelpSearchResult> {
        val response = yelpApiInterface.getStores(
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

    override suspend fun getStoresDeals(
        currentLocation: CurrentLocation,
        limit: Int
    ): Resource<YelpSearchResult> {
        val response = yelpApiInterface.getStores(
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

    override suspend fun getAllFilteredStores(
        currentLocation: CurrentLocation,
        searchTerm: String?,
        attribute: String?,
        orderFilter: String?,
        priceFilter: String?
    ): Resource<YelpSearchResult> {
        val response = yelpApiInterface.getStores(
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