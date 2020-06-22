package com.example.prm_project2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prm_project2.db.User
import com.example.prm_project2.db.VisitedLocation
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_locations.*

class LocationsActivity : AppCompatActivity(), OnItemListener {
    private lateinit var db: FirebaseFirestore
    private lateinit var myAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)
        db = Firebase.firestore
        val user = Firebase.auth.currentUser

        if(user != null) {
            val locationsDocRef = db.collection("users").document(user.uid).collection("locations")
            locationsDocRef.get()
                .addOnSuccessListener {
                    //Log.d("FirebaseInfo", "locations size: ${it.size()}")
                    if (it.size() > 0) {
                        val listLocations = it.toObjects<VisitedLocation>()
                        recyclerView.apply {
                            layoutManager = LinearLayoutManager(this@LocationsActivity)
                            myAdapter = MyAdapter(listLocations, this@LocationsActivity)
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
        TODO("Not yet implemented")
    }

    override fun onItemLongClick(position: Int) {
        TODO("Not yet implemented")
    }

    fun addLocation(view: View) {
        startActivity(Intent(this, AddLocationActivity::class.java))
    }
}
