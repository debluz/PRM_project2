package com.example.prm_project2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.prm_project2.db.VisitedLocation

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var selectedLocations: List<VisitedLocation>
    private lateinit var allLocations: List<VisitedLocation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        if(intent.getParcelableArrayListExtra<VisitedLocation>("selected_locations") != null){
            selectedLocations = intent.getParcelableArrayListExtra("selected_locations")
        } else if (intent.getParcelableArrayListExtra<VisitedLocation>("all_locations") != null){
            allLocations = intent.getParcelableArrayListExtra("all_locations")
        }

        if(this::selectedLocations.isInitialized){
            Log.d("ShowMap", "Sent locations size: ${selectedLocations.size}")
        } else if (this::allLocations.isInitialized){
            Log.d("ShowMap", "Sent locations size: ${allLocations.size}")
        }

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
        lateinit var coordinates: LatLng
        if(this::selectedLocations.isInitialized){
            selectedLocations.forEach {
                coordinates = LatLng(it.latitude!!, it.longitude!!)
                mMap.addMarker(MarkerOptions()
                    .position(coordinates)
                    .title(it.name))
                mMap.addCircle(CircleOptions()
                    .center(coordinates)
                    .radius(it.diameter!!*1000))
            }
        } else if (this::allLocations.isInitialized){
            allLocations.forEach {
                coordinates = LatLng(it.latitude!!, it.longitude!!)
                mMap.addMarker(MarkerOptions()
                    .position(coordinates)
                    .title(it.name))
                mMap.addCircle(CircleOptions()
                    .center(coordinates)
                    .radius(it.diameter!!*1000))
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 12f))
        mMap.uiSettings.isZoomControlsEnabled = true
    }
}