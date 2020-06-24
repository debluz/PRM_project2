package com.example.prm_project2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prm_project2.db.User
import com.example.prm_project2.db.VisitedLocation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_add_location.*
import kotlinx.android.synthetic.main.activity_locations.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddLocationActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var photo: File
    lateinit var newLocation: VisitedLocation
    val storage = Firebase.storage

    companion object{
        const val REQUEST_IMAGE_CAPTURE = 1111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)
        db = Firebase.firestore
    }

    fun confirmNew(view: View) {
        /*var storageRef = storage.reference
        val imageRef = storageRef.child("images/${photo.name}")
        var uploadTask = imageRef.putFile(Uri.fromFile(photo)).apply {
            addOnSuccessListener {
                Log.d("UploadPhoto", "${photo.name} uploaded ${it.metadata}")
            }
            addOnFailureListener {
                Log.w("UploadPhoto2", it.toString())
            }
        }
        Log.d("UploadPhoto", "imageRef: ${imageRef.toString()}")*/

        if(this::photo.isInitialized){
            Log.d("UploadPhoto", "photoUri (local): ${Uri.fromFile(photo)}")
            val photoUri = Uri.fromFile(photo)
            newLocation = VisitedLocation(
                null,
                latitudeText.text.toString().toDouble(),
                longitudeText.text.toString().toDouble(),
                locationNameText.text.toString(),
                locationDiameterText.text.toString().toDouble(),
                photoUri.toString(),
                descriptionText.text.toString()
            )
        } else {
            newLocation = VisitedLocation(
                null,
                latitudeText.text.toString().toDouble(),
                longitudeText.text.toString().toDouble(),
                locationNameText.text.toString(),
                locationDiameterText.text.toString().toDouble(),
                null,
                descriptionText.text.toString()
            )
        }

        val user = Firebase.auth.currentUser
        if(user != null){
            db.collection("users").document(user.uid).collection("locations").add(newLocation)
            finish()
            startActivity(Intent(this@AddLocationActivity, LocationsActivity::class.java))
        }

    }




    fun declineNew(view: View) {
        finish()
    }

    fun uploadPhoto(view: View) {
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
        } else {
            runCamera()
        }
    }

    fun setLocation(view: View) {
        startActivityForResult(Intent(this, LocationSelectActivity::class.java), 10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 10 && resultCode == 10){
            if(data != null){
                val lat = data.getDoubleExtra("Lat", 0.0)
                val long = data.getDoubleExtra("Long", 0.0)
                Log.d("ActivityResult", "lat: $lat long: $long")
                latitudeText.setText(lat.toString())
                longitudeText.setText(long.toString())
            }
        } else if ( requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            Log.d("PhotoUpload", "Result ok")
            Log.d("PhotoUpload", data.toString())
            Log.d("PhotoUpload", photo.name)

            photo.inputStream().use {
                val inputBitmap = BitmapFactory.decodeStream(it)
                val matrix = Matrix().apply { setRotate(90f) }
                val rotatedBitmap = Bitmap.createBitmap(inputBitmap, 0, 0, inputBitmap.width, inputBitmap.height,  matrix, true)
                imageView.setImageBitmap(rotatedBitmap)
            }
        }
    }

    fun runCamera(){
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        photo = filesDir.resolve("photo $timeStamp.jpg").apply { createNewFile() }
        val uri = FileProvider.getUriForFile(this, "com.example.prm_project2.fileprovider", photo)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).let {
            it.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            runCamera()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


}