package com.example.prm_project2.db

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class User (
    var name: String?,
    var email: String?,
    var locations: List<VisitedLocations>?
)