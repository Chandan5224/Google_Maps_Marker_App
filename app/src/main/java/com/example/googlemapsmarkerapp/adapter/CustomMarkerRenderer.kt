package com.example.googlemapsmarkerapp.adapter

import android.content.Context
import com.example.googlemapsmarkerapp.model.LocationItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class CustomMarkerRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<LocationItem>
) : DefaultClusterRenderer<LocationItem>(context, map, clusterManager) {

    private val MIN_CLUSTER_SIZE = 2 // Set your minimum cluster size here
    override fun shouldRenderAsCluster(cluster: Cluster<LocationItem>): Boolean {
        // Check if cluster items count is above or equal to the minimum cluster size
        return cluster.size >= MIN_CLUSTER_SIZE
    }

    override fun onBeforeClusterItemRendered(item: LocationItem, markerOptions: MarkerOptions) {
        val markerColor = when (item.type) {
            "saved" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            "not_saved" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE) // Default color
        }
        markerOptions.icon(markerColor)

        super.onBeforeClusterItemRendered(item, markerOptions)
    }


}