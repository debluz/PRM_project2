package com.example.prm_project2

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.text.Layout
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prm_project2.LocationsActivity.Companion.REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
import com.example.prm_project2.db.User
import com.example.prm_project2.db.VisitedLocation
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.internal.Constants
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_locations.*

class LocationsActivity : AppCompatActivity(), OnItemListener{
    private lateinit var db: FirebaseFirestore
    private lateinit var myAdapter: MyAdapter
    private lateinit var listLocations: List<VisitedLocation>
    private var selectedLocations = mutableListOf<VisitedLocation>()
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    private lateinit var geofencingClient: GeofencingClient
    private var counter = 0
    val geofenceList = mutableListOf<Geofence>()
    companion object{
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 222
        const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 444
        const val REQUEST_TURN_DEVICE_LOCATION_ON = 555
    }
    //variable to handle geofence transitions
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)


        db = Firebase.firestore
        val user = Firebase.auth.currentUser
        var listTemp = mutableListOf<VisitedLocation>()
        if(user != null) {
            val locationsDocRef = db.collection("users").document(user.uid).collection("locations")
            locationsDocRef.get()
                .addOnSuccessListener {
                    //Log.d("FirebaseInfo", "locations size: ${it.size()}")
                    if (it.size() > 0) {
                        val documents = it.documents
                        documents.forEach {
                            val location = it.toObject(VisitedLocation::class.java)
                            if(location != null){
                                location.locationId = it.id
                                listTemp.add(location)
                            }
                        }
                        listLocations = listTemp
                        recyclerView.apply {
                            layoutManager = LinearLayoutManager(this@LocationsActivity)
                            myAdapter = MyAdapter(listLocations, this@LocationsActivity, selectedLocations)
                            adapter = myAdapter
                        }
                    } else {
                        noLocationsInfo.text = "Nie masz dodanych żadnych odwiedzonych miejsc"
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("FirebaseInfo", "Error reading document", e)
                }
        }

        /*geofencingClient = LocationServices.getGeofencingClient(this)

        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent)?.run {
            addOnSuccessListener {
                Log.d("GeofenceAdd", "geofence added")
            }
            addOnFailureListener {
                Log.d("GeofenceAdd", "geofence failed to add")
            }
        }*/

    }


    fun signOut(view: View) {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                Toast.makeText(this, "Signing out was succesfull", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, EditLocationActivity::class.java).apply {
            if(!listLocations.isNullOrEmpty()){
                putExtra("selected_location", listLocations[position])
            } else {
                Log.d("errors", "List of locations is empty or null")
            }
        }
        startActivity(intent)
        myAdapter.notifyDataSetChanged()
    }

    override fun onItemLongClick(position: Int) {
        DeleteDialog(listLocations[position]).show(supportFragmentManager, "DeleteDialog")
        myAdapter.notifyDataSetChanged()
    }



    fun addLocation(view: View) {
        startActivity(Intent(this, AddLocationActivity::class.java))
    }

    fun showSelected(view: View) {
        if(this::myAdapter.isInitialized && myAdapter.selectedLocations.size > 0){
            /*myAdapter.selectedLocations.forEach {
                Log.d("SelectedLocations", "Location: ${it.toString()}")
            }*/
            val intent = Intent(this, MapsActivity::class.java).apply {
                putParcelableArrayListExtra("selected_locations", ArrayList(myAdapter.selectedLocations))
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "No locations added/selected", Toast.LENGTH_SHORT).show()
        }
    }

    fun showAll(view: View) {
        if(listLocations.size > 0){
            /*myAdapter.selectedLocations.forEach {
                Log.d("SelectedLocations", "Location: ${it.toString()}")
            }*/
            val intent = Intent(this, MapsActivity::class.java).apply {
                putParcelableArrayListExtra("all_locations", ArrayList(listLocations))
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "No locations added", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        AlertDialog.Builder(this@LocationsActivity).apply {
            setTitle("Alert")
            setMessage("Are you sure want to sign out?")
            setPositiveButton("Yes") { _, _ ->
                AuthUI.getInstance()
                    .signOut(this@LocationsActivity)
                    .addOnCompleteListener {
                        Toast.makeText(this@LocationsActivity, "Signing out was succesfull", Toast.LENGTH_SHORT).show()
                        finish()
                        startActivity(Intent(this@LocationsActivity, MainActivity::class.java))
                    }
            }
            setNegativeButton("Cancel", {_,_->})
        }.create().show()
    }

    /*@TargetApi(29)
    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
        //check ACCESS_FINE_LOCATION permission
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION))
        val backgroundPermissionApproved =
            //If the device is running Android Q (API 29) or higher, check the ACCESS_BACKGROUND_LOCATION
            if (runningQOrLater) {
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    @TargetApi(29 )
    private fun requestForegroundAndBackgroundLocationPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved())
        //If the permissions have already been granted
            return
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCode = when {
            runningQOrLater -> {
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }
        Log.d("GeofencePermission", "Request foreground only location permission")
        ActivityCompat.requestPermissions(
            this,
            permissionsArray,
            resultCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d("GeofencePermission", "onRequestPermissionResult")

        if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[1] == PackageManager.PERMISSION_DENIED)){
        } else {
            checkDeviceLocationSettingsAndStartGeofence()
        }
    }

    private fun checkDeviceLocationSettingsAndStartGeofence(resolve:Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 6000
            fastestInterval = 3000
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        //check location settings
        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponse = settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponse
            //if settings are not satisfied
            .addOnFailureListener {
                Log.w("GeoLocationSettings", it.toString())
                if(it is ResolvableApiException && resolve){
                    try {
                        it.startResolutionForResult(this,
                            REQUEST_TURN_DEVICE_LOCATION_ON
                        )
                    } catch (sendEx: IntentSender.SendIntentException){
                        Log.d("GeoLocationSettings", "Error getting location settings resolution: ${sendEx.message}")
                    }
                } else {
                    Toast.makeText(this, "Location needs to be enabled to use geofence", Toast.LENGTH_SHORT).show()
                }
            }
            //if locationSettingsResposne is completed
            .addOnCompleteListener {
                //check if succesful
                if(it.isSuccessful){
                    addGeofenceForClue()
                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_TURN_DEVICE_LOCATION_ON){
            //if the user has chosen not to accept the permissions, ask again.
            checkDeviceLocationSettingsAndStartGeofence(false)
        }
    }

    private fun addGeofenceForClue() {
        listLocations.forEach {
            geofenceList.add(Geofence.Builder()
                .setRequestId("Geofence:${it.locationId}")
                .setCircularRegion(it.latitude!!.toDouble(), it.longitude!!.toDouble(), (it.diameter!!.toFloat()))
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build())
        }
    }

    private fun getGeofencingRequest(): GeofencingRequest{
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }*/



}
