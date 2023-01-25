package com.example.suitup.main.data.network

import com.example.suitup.common.Constants.defaultCategory
import com.example.suitup.main.data.model.Store
import com.example.suitup.main.data.model.yelp.YelpReviewResult
import com.example.suitup.main.data.model.yelp.YelpSearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface YelpApiInterface {

    @GET("businesses/search")
    suspend fun getStores(
        @Header("Authorization") authHeader: String,
        @Query("latitude") searchLatitude: Double,
        @Query("longitude") searchLongitude: Double,
        @Query("term") searchTerm: String? = null,
        @Query("attributes") attributes: String? = null,
        @Query("categories") categories: String? = defaultCategory,
        @Query("price") price: String? = null,
        @Query("limit") limit: Int? = null
    ) : Response<YelpSearchResult>

    @GET("businesses/{id}")
    suspend fun getStore(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String?,
    ): Response<Store>

    @GET("businesses/{id}/reviews")
    suspend fun getStoreReviews(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String?,
    ): Response<YelpReviewResult>
}