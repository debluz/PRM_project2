package com.example.prm_project2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.prm_project2.db.VisitedLocation
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_location.*
import kotlinx.android.synthetic.main.item_location.*

class AddLocationActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)
        db = Firebase.firestore
        user = Firebase.auth.currentUser
    }

    fun confirmNew(view: View) {
        val newLocation = VisitedLocation(
            latitudeText.text.toString().toDouble(),
            longitudeText.text.toString().toDouble(),
            locationNameText.text.toString(),
            locationDiameter.text.toString().toDouble(),
            null,
            descriptionText.text.toString()
        )
    }

    fun declineNew(view: View) {
        finish()
    }

    fun uploadPhoto(view: View) {

    }

    fun setLocation(view: View) {
        startActivity(Intent(this, LocationSelectActivity::class.java))
        //startActivityForResult(Intent(this, LocationSelectActivity::class.java), 10)
    }
}