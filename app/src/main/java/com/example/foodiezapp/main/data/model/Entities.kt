package com.example.foodiezapp.main.data.model

import androidx.room.*
import com.example.foodiezapp.main.data.model.yelp.YelpCategory
import com.example.foodiezapp.main.data.model.yelp.YelpCoordinates
import com.example.foodiezapp.main.data.model.yelp.YelpLocation


@Entity(tableName = "Restaurants")
data class RestaurantEntity(
    @PrimaryKey(autoGenerate = false) var id: String,
    var name: String?,
    var rating: Float?,
    var price: String?,
    var phone: String?,
    var review_count: Int?,
    var is_closed: Boolean?,
    var image_url: String?,
    var categories: String?,
    @Embedded var coordinates: YelpCoordinates?,
    @Embedded var location: YelpLocation?
) {
      fun mapToModel(): Restaurant = Restaurant(
        id, name!!, rating!!, price, phone, review_count, is_closed, image_url,
        listOf(YelpCategory(categories!!)), coordinates!!, location, isFavorite = true
    )
}