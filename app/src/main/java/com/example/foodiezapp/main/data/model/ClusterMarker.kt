package com.example.foodiezapp.main.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class ClusterMarker(
    val restaurant: Restaurant
) : ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(restaurant.coordinates.latitude, restaurant.coordinates.longitude)
    }

    override fun getTitle(): String {
        return restaurant.name
    }

    override fun getSnippet(): String {
        return restaurant.categories.toString()
    }
}