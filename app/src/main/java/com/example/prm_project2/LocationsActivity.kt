package com.example.prm_project2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_locations.*

class LocationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)

        val user = Firebase.auth.currentUser

        if(user != null){
            userInfoText.text = "You are signed in as ${user.displayName}"
        }
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
}
