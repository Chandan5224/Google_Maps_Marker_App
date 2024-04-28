package com.example.googlemapsmarkerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.googlemapsmarkerapp.R
import com.example.googlemapsmarkerapp.model.LocationData
import com.example.googlemapsmarkerapp.model.LocationItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    private val windowView: View =
        LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)
    var currentItem: LocationItem? = null

    private fun renderWindowText(marker: Marker, view: View) {
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val address = view.findViewById<TextView>(R.id.tvAddress)
        val lat = view.findViewById<TextView>(R.id.tvLat)
        val lng = view.findViewById<TextView>(R.id.tvLng)
        currentItem?.locationData?.let {
            lat.visibility = View.GONE
            lng.visibility = View.GONE
            address.visibility = View.VISIBLE
            title.visibility = View.VISIBLE
            title.text = "Name : ${it.name}"
            address.text = "Address : ${it.address}"
        } ?: run {
            title.visibility = View.GONE
            address.visibility = View.GONE
            lat.visibility = View.VISIBLE
            lng.visibility = View.VISIBLE
            val latitude = String.format("%.3f", currentItem?.position?.latitude)
            val longitude = String.format("%.3f", currentItem?.position?.longitude)
            lat.text =
                "Lat : $latitude"
            lng.text =
                "Lng : $longitude"
        }
    }

    override fun getInfoWindow(marker: Marker): View {
        renderWindowText(marker, windowView)
        return windowView
    }

    override fun getInfoContents(marker: Marker): View? = null
}