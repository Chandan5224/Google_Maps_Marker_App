package com.example.googlemapsmarkerapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.googlemapsmarkerapp.R
import com.example.googlemapsmarkerapp.adapter.CustomInfoWindowAdapter
import com.example.googlemapsmarkerapp.adapter.CustomMarkerRenderer
import com.example.googlemapsmarkerapp.databinding.ActivityMapsBinding
import com.example.googlemapsmarkerapp.model.LocationData
import com.example.googlemapsmarkerapp.model.LocationItem
import com.example.googlemapsmarkerapp.utils.*
import com.example.googlemapsmarkerapp.utils.Constants.DEFAULT_LOCATION
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    LocationSettingsCallback, EasyPermissions.PermissionCallbacks {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mViewModel: MainViewModel
    private var mClusterManager: ClusterManager<LocationItem>? = null

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapFragment: SupportMapFragment
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.onCreate(savedInstanceState)

        mapFragment.getMapAsync(this)

        observeGetLocationData()
        requestPermissions()
    }

    override fun onResume() {
        super.onResume()
        mapFragment.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapFragment.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapFragment.onDestroy()
    }

    private fun observeGetLocationData() {
        mViewModel.locations.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { locList ->
                        val locItems: MutableList<LocationItem> = mutableListOf()
                        locList.forEach { loc ->
                            val item = LocationItem(
                                loc,
                                loc.latLng,
                                "",
                                "",
                                1f,
                                loc.type
                            )
                            locItems.add(item)
                        }
                        mClusterManager?.clearItems()
                        mClusterManager?.addItems(locItems)
                        mClusterManager?.cluster()
                    }
                }
                is Resource.Error -> {

                }
                is Resource.Loading -> {

                }
            }
        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        // Add a marker in Sydney and move the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(4.3f))

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = false

        // my location button bottom end setup
        val locationButtonView =
            mapFragment.view?.findViewById<View>(Integer.parseInt("1"))?.parent as? View
        val locationButton = locationButtonView?.findViewById<View>(Integer.parseInt("2"))

        if (locationButton?.layoutParams != null) {
            val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            rlp.setMargins(0, 0, 0, 30)
        }

        // Set click listeners
        mMap.setOnMapClickListener(this)
        mMap.setOnMyLocationButtonClickListener {
            LocationUtils.checkLocationSettings(this, this)
            false
        }
        setupCluster()
    }

    // cluster setup
    private fun setupCluster() {
        // Initialize the ClusterManager with the context and the map.
        mClusterManager = ClusterManager<LocationItem>(this, mMap)
        val renderer = CustomMarkerRenderer(this, mMap, mClusterManager!!)
        mClusterManager?.renderer = renderer
        // Create an instance of the custom info window adapter
        val customInfoWindowAdapter = CustomInfoWindowAdapter(this)
        // Set the custom info window adapter to the ClusterManager
        mClusterManager?.markerCollection?.setInfoWindowAdapter(customInfoWindowAdapter)
        mMap.setOnCameraIdleListener(mClusterManager)
        mClusterManager?.setOnClusterItemClickListener(ClusterManager.OnClusterItemClickListener { item ->
            customInfoWindowAdapter.currentItem = item // Set the current item for the InfoWindow
            Log.d("TAG", item.toString())
            val dialog = Dialog(this)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_custom)
            dialog.setCancelable(false)
            dialog.show()
            val latlngText = "${item.position.latitude}, ${item.position.longitude}"
            val name = dialog.findViewById<TextInputEditText>(R.id.etName)
            val age = dialog.findViewById<TextInputEditText>(R.id.etAge)
            val relation = dialog.findViewById<TextInputEditText>(R.id.etRelation)
            dialog.findViewById<TextInputEditText>(R.id.etLatlng).apply {
                setText(latlngText)
                isEnabled = false
            }
            val address = dialog.findViewById<TextInputEditText>(R.id.etAddress)
            val btnSave = dialog.findViewById<MaterialButton>(R.id.btnSave)
            val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
            val title = dialog.findViewById<TextView>(R.id.tvDialogTitle)

            when (item.locationData?.type) {
                "saved" -> {
                    btnSave.text = "Delete"
                    title.text = "Personal Details"
                    item.locationData.let {
                        name.setText(item.locationData.name)
                        age.setText(item.locationData.age.toString())
                        relation.setText(item.locationData.relation)
                        address.setText(item.locationData.address)
                    }
                    name.isEnabled = false
                    age.isEnabled = false
                    relation.isEnabled = false
                    address.isEnabled = false
                    btnSave.setOnClickListener {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Deleting Marker")
                        //set message for alert dialog
                        builder.setMessage("are you sure?")
                        builder.setIcon(android.R.drawable.ic_dialog_alert)

                        //performing positive action
                        builder.setPositiveButton("Yes") { dialogInterface, which ->
                            mViewModel.deleteLocation(item.locationData)
                            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        //performing cancel action
                        builder.setNeutralButton("No") { dialogInterface, which ->
                        }
                        // Create the AlertDialog
                        val alertDialog: AlertDialog = builder.create()
                        // Set other dialog properties
                        alertDialog.setCancelable(false)
                        alertDialog.show()
                    }
                }
                else -> {
                    btnSave.text = "Save"
                    title.text = "Enter Personal Details"
                    btnSave.setOnClickListener {
                        saveData(item, name, age, relation, address, dialog)
                    }
                }
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            false
        })

        mViewModel.getAllSavedLocation()

    }

    private fun saveData(
        item: LocationItem,
        name: TextInputEditText,
        age: TextInputEditText,
        relation: TextInputEditText,
        address: TextInputEditText,
        dialog: Dialog
    ) {
        if (name.text.toString().isBlank() || age.text.toString()
                .isBlank() || relation.text.toString()
                .isBlank() || address.text.toString().isBlank()
        ) {
            Toast.makeText(this, "Please fill the details.", Toast.LENGTH_SHORT).show()
            return
        }
        val locationData =
            LocationData(
                0,
                name.text.toString(),
                age.text.toString().toInt(),
                relation.text.toString(),
                item.position,
                address.text.toString(),
                "saved"
            )
        mClusterManager?.removeItem(item)
        mViewModel.insertLocation(locationData)
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }

    override fun onMapClick(latlng: LatLng) {
        // Add a marker at the clicked location with some custom settings

        val locationItem = LocationItem(
            null,
            latlng,
            "",
            "",
            1f,
            "not_saved"
        )
        mClusterManager?.addItem(locationItem)
        mClusterManager?.cluster()
    }

    @SuppressLint("MissingPermission")
    override fun onLocationSettingsSuccess() {
        mFusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let { curLoc ->

            }
        }
    }

    override fun onLocationSettingsFailure(exception: Exception) {

    }

    /// request permission
    private fun requestPermissions() {
        if (TrackingUtility.hasLocationPermissions(this)) {
            return
        }
        EasyPermissions.requestPermissions(
            this,
            "You need to accept permissions to use this app.",
            Constants.REQUEST_CODE_LOCATION_PERMISSION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}