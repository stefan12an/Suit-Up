package com.example.foodiezapp.main.data.model.yelp
import com.example.foodiezapp.main.data.model.Restaurant
import com.google.gson.annotations.SerializedName

data class YelpSearchResult (
    @SerializedName("total") val total: Int,
    @SerializedName("businesses") val restaurants: List<Restaurant>
        )