package com.example.prm_project2.db

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class User (
    var name: String? = null,
    var email: String? = null
) {
    override fun toString(): String = "User $name email: $email"
}