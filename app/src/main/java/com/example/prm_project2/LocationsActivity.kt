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

class LocationsActivity : AppCompatActivity(), OnItemListener{
    private lateinit var db: FirebaseFirestore
    private lateinit var myAdapter: MyAdapter
    private lateinit var listLocations: List<VisitedLocation>
    private var selectedLocations = mutableListOf<VisitedLocation>()

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


}
