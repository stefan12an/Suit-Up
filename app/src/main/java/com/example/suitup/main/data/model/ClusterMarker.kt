package com.example.suitup.main.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class ClusterMarker(
    val store: Store
) : ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(store.coordinates.latitude, store.coordinates.longitude)
    }

    override fun getTitle(): String {
        return store.name
    }

    override fun getSnippet(): String {
        return store.categories.toString()
    }
}