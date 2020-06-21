package com.example.prm_project2.db

import android.net.Uri
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class VisitedLocations(
    var latitude: Double,
    var longitude: Double,
    var name: String?,
    var diameter: Double?,
    var photo: Uri?,
    var description: String?
)
