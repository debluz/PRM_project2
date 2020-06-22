package com.example.prm_project2.db

import android.net.Uri
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class VisitedLocation(
    var latitude: Double? = null,
    var longitude: Double? = null,
    var name: String? = null,
    var diameter: Double? = null,
    var photoUrl: String? = null,
    var description: String? = null
)
