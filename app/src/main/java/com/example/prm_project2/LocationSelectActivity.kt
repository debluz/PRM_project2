package com.example.prm_project2

import android.content.ContentProviderClient
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import java.util.jar.Manifest

class LocationSelectActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Task<Location>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_select)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        /*val returnIntent = Intent().apply {
            putExtra("latitude", -34.0)
            putExtra("longitude", 151.0)
        }
        setResult(10, returnIntent)
        finish()*/
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
        getCurrentLocation()
    }

    fun getCurrentLocation(){
        if(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0)
        } else {
            lastLocation = mFusedLocationProviderClient.lastLocation
            Log.d("LocationSelect", lastLocation.toString())
            lastLocation.apply {
                addOnCompleteListener {
                    val taskResult = it.result
                    if (taskResult != null){
                        mMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(
                                LatLng(taskResult.latitude, taskResult.longitude),
                                10.0F
                            )
                        )
                        mMap.addMarker(MarkerOptions().position(LatLng(taskResult.latitude, taskResult.longitude)).title("Current position").draggable(true))
                    } else {
                        Log.d("LocationSelect", "Current location is null. Using defaults.");
                        Log.e("LocationSelect", "Exception: %s", it.exception);
                    }
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getCurrentLocation()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            finish()
        }
    }
}