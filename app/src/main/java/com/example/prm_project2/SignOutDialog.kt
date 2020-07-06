package com.example.prm_project2

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignOutDialog: DialogFragment() {
    lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        db = Firebase.firestore
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setTitle("Alert")
            setMessage("Do you want to sign out?")
            setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                AuthUI.getInstance()
                    .signOut(requireActivity())
                    .addOnCompleteListener {
                        requireActivity().finish()
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                    }
            })
            setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->  })
        }.create()

    }
}