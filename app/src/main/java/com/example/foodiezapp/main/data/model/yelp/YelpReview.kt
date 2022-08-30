package com.example.foodiezapp.main.data.model.yelp

data class YelpReview(
    val id: String,
    val rating: Float,
    val user: YelpUser,
    val text: String,
    val time_created: String
)