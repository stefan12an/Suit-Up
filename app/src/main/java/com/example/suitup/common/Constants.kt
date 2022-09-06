package com.example.suitup.common

import suitup.BuildConfig

object Constants {
    //Room
    const val DB_NAME = "suitup"

    //Retrofit
    const val BASE_URL = "https://api.yelp.com/v3/"
    const val YELP_API_KEY = BuildConfig.YELP_API_KEY
    const val NEARBY_LIMIT = 4
    const val HOT_NEW_LIMIT = 5
    const val DEALS_LIMIT = 5
    const val defaultCategory = "fashion"

    //DataStore
    const val DATASTORE_NAME = "settings"

    //Adapter Data
    val ATTRIBUTES =
        mapOf(
            "" to "",
            "Hot and New" to "hot_and_new",
            "Best Deals" to "deals"
        )
    val INTERVAL_RED = 0f..3.99f
    val INTERVAL_YELLOW = 4f..4.5f
    val INTERVAL_GREEN = 4.51f..5f
}