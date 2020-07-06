package com.example.prm_project2

import android.app.Notification
import android.app.NotificationManager
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if(geofencingEvent.hasError()){
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e("GeofenceBroad", errorMessage)
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            val geofenceTransitionDetails = getGeofenceTransitionDetails(this, geofenceTransition, triggeringGeofences)


        } else {
            Log.e(TAG, "geofence_transition_invalid_type $geofenceTransition")
        }
    }
}

fun getGeofenceTransitionDetails(broadcastReceiver: BroadcastReceiver, geofenceTransition: Int, trigerringGeofences: List<Geofence>): String{
    return "${broadcastReceiver.toString()} transition type: $geofenceTransition triggered geofences: ${trigerringGeofences.forEach { it.toString()+"\n"}}"
}

fun makeNotificationIntent(geofenceService: Context): Intent{
    return Intent(geofenceService, LocationsActivity::class.java)
}

private const val TAG = "GeofenceReceiver"
