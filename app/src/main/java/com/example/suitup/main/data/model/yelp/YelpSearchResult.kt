package com.example.suitup.main.data.model.yelp
import com.example.suitup.main.data.model.Store
import com.google.gson.annotations.SerializedName

data class YelpSearchResult (
    @SerializedName("total") val total: Int,
    @SerializedName("businesses") val stores: List<Store>
        )