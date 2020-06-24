package com.example.prm_project2

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.prm_project2.db.VisitedLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.concurrent.thread

class DeleteDialog(val location: VisitedLocation) : DialogFragment() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setTitle("Alert")
            setMessage("Do you want to delete this location?")
            setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                val user = FirebaseAuth.getInstance().currentUser
                if(user != null){
                    db.collection("users").document(user.uid).collection("locations").document(location.locationId.toString()).delete()
                    startActivity(Intent(requireContext(), LocationsActivity::class.java))
                    requireActivity().finish()
                }

            })
            setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ -> })
        }.create()
    }
}