package com.example.suitup.common

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.example.suitup.main.data.model.ClusterMarker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.squareup.picasso.Picasso
import suitup.R

class ClusterManagerRenderer(
    val context: Context?, map: GoogleMap?,
    clusterManager: ClusterManager<ClusterMarker>?
) : DefaultClusterRenderer<ClusterMarker>(context, map, clusterManager) {
    private var iconGenerator: IconGenerator
    private var markerImageView: ImageView

    init {
        val singleItem: View =
            LayoutInflater.from(context).inflate(R.layout.map_marker, null, false)
        iconGenerator = IconGenerator(context)
        iconGenerator.setContentView(singleItem)
        markerImageView = singleItem.findViewById(R.id.image)
    }



    override fun onClusterItemRendered(clusterItem: ClusterMarker, marker: Marker) {
        Picasso.get().load(clusterItem.restaurant.image_url.toString())
            .into(object : com.squareup.picasso.Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    markerImageView.setImageBitmap(bitmap)
                    val icon = iconGenerator.makeIcon()
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon))
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    markerImageView.setImageDrawable(placeHolderDrawable)
                    val icon = iconGenerator.makeIcon()
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon))
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    Log.e(TAG, "onBitmapFailed: Something Went Wrong")
                }
            })
    }

    override fun onBeforeClusterRendered(
        cluster: Cluster<ClusterMarker>,
        markerOptions: MarkerOptions
    ) {
        super.onBeforeClusterRendered(cluster, markerOptions)
    }

    override fun onClusterRendered(cluster: Cluster<ClusterMarker>, marker: Marker) {
        super.onClusterRendered(cluster, marker)
    }


    override fun shouldRenderAsCluster(cluster: Cluster<ClusterMarker>): Boolean = cluster.size >= 3
}