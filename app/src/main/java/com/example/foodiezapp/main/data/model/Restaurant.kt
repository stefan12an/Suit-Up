package com.example.foodiezapp.main.data.model

import com.example.foodiezapp.main.data.model.yelp.YelpCategory
import com.example.foodiezapp.main.data.model.yelp.YelpCoordinates
import com.example.foodiezapp.main.data.model.yelp.YelpHours
import com.example.foodiezapp.main.data.model.yelp.YelpLocation

data class Restaurant(
    val id: String,
    val name: String,
    val rating: Float,
    val price: String?,
    val phone: String?,
    val review_count: Int?,
    val is_closed: Boolean?,
    val image_url: String?,
    val categories: List<YelpCategory>,
    val coordinates: YelpCoordinates,
    val location: YelpLocation?,
    var isFavorite: Boolean? = false,
    val photos: List<String>? = emptyList(),
    val hours: List<YelpHours>? = emptyList(),
    val url: String? = null
) {
    fun mapToEntity(): RestaurantEntity =
        RestaurantEntity(
            id,
            name,
            rating,
            price,
            phone,
            review_count,
            is_closed,
            image_url,
            categories.map { it.title }.toString(),
            coordinates,
            location
        )
}