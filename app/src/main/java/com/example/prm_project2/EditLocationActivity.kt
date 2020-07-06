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
import com.example.prm_project2.db.VisitedLocation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_location.*
import kotlinx.android.synthetic.main.activity_edit_location.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditLocationActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var photo: File
    private lateinit var myLocation: VisitedLocation

    companion object{
        const val REQUEST_IMAGE_CAPTURE = 1111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_location)
        db = Firebase.firestore
        if(intent.hasExtra("selected_location")){
            myLocation = intent.getParcelableExtra("selected_location")
            edit_locationNameText.setText(myLocation.name)
            edit_locationDiameterText.setText(myLocation.diameter.toString())
            edit_latitudeText.setText(myLocation.latitude.toString())
            edit_longitudeText.setText(myLocation.longitude.toString())
            edit_descriptionText.setText(myLocation.description)
            if(myLocation.photoUri != null){
                val uri = Uri.parse(myLocation.photoUri)
                val photo: File = File(uri.path)
                photo.inputStream().use {
                    val inputBitmap = BitmapFactory.decodeStream(it)
                    if(inputBitmap != null){
                        val matrix = Matrix().apply { setRotate(90f) }
                        val rotatedBitmap = Bitmap.createBitmap(inputBitmap, 0, 0, inputBitmap.width, inputBitmap.height,  matrix, true)
                        Log.d("Items", "${myLocation.photoUri} bitmap width: ${inputBitmap.width} height: ${inputBitmap.height}")
                        edit_imageView.setImageBitmap(rotatedBitmap)
                    }
                }
            }
        }

    }

    fun uploadPhoto(view: View) {
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
        } else {
            runCamera()
        }
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

    private fun runCamera() {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        photo = filesDir.resolve("photo $timeStamp.jpg").apply { createNewFile() }
        val uri = FileProvider.getUriForFile(this, "com.example.prm_project2.fileprovider", photo)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).let {
            it.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
        startActivityForResult(intent, AddLocationActivity.REQUEST_IMAGE_CAPTURE)
    }

    fun setLocation(view: View) {
        startActivityForResult(Intent(this, LocationSelectActivity::class.java), 10)
    }

    fun declineEdit(view: View) {
        BackDialog().show(supportFragmentManager, "BackDialog")
    }

    fun confirmEdit(view: View) {
            lateinit var newLocation: VisitedLocation
            var inputBitmap: Bitmap? = null
            if(this::photo.isInitialized) {
                photo.inputStream().use {
                    inputBitmap = BitmapFactory.decodeStream(it)
                }
                Log.d("UploadPhoto0", inputBitmap.toString())
                if(inputBitmap != null){
                    Log.d("UploadPhoto1", "photoUri (local): ${Uri.fromFile(photo)}")
                    val photoUri = Uri.fromFile(photo)
                    newLocation = VisitedLocation(
                        myLocation.locationId,
                        edit_latitudeText.text.toString().toDouble(),
                        edit_longitudeText.text.toString().toDouble(),
                        edit_locationNameText.text.toString(),
                        edit_locationDiameterText.text.toString().toDouble(),
                        photoUri.toString(),
                        edit_descriptionText.text.toString()
                    )
                } else {
                    newLocation = VisitedLocation(
                        myLocation.locationId,
                        edit_latitudeText.text.toString().toDouble(),
                        edit_longitudeText.text.toString().toDouble(),
                        edit_locationNameText.text.toString(),
                        edit_locationDiameterText.text.toString().toDouble(),
                        myLocation.photoUri,
                        edit_descriptionText.text.toString()
                    )
                }
            } else {
                newLocation = VisitedLocation(
                    myLocation.locationId,
                    edit_latitudeText.text.toString().toDouble(),
                    edit_longitudeText.text.toString().toDouble(),
                    edit_locationNameText.text.toString(),
                    edit_locationDiameterText.text.toString().toDouble(),
                    myLocation.photoUri,
                    edit_descriptionText.text.toString()
                )
            }

            var user = Firebase.auth.currentUser
            if(user != null){
                db.collection("users").document(user.uid).collection("locations").document(myLocation.locationId.toString()).set(newLocation, SetOptions.merge())
            }
            finish()
            startActivity(Intent(this@EditLocationActivity, LocationsActivity::class.java))


    }

    fun checkIfEmpty(): Boolean{
        return locationNameText.text.isNullOrEmpty() ||
                locationDiameterText.text.isNullOrEmpty() ||
                latitudeText.text.isNullOrEmpty() ||
                longitudeText.text.isNullOrEmpty()
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 10 && resultCode == 10){
            if(data != null){
                val lat = data.getDoubleExtra("Lat", 0.0)
                val long = data.getDoubleExtra("Long", 0.0)
                Log.d("ActivityResult", "lat: $lat long: $long")
                edit_latitudeText.setText(lat.toString())
                edit_longitudeText.setText(long.toString())
            }
        } else if ( requestCode == AddLocationActivity.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            Log.d("PhotoUpload", "Result ok")
            Log.d("PhotoUpload", data.toString())
            Log.d("PhotoUpload", photo.name)

            photo.inputStream().use {
                val inputBitmap = BitmapFactory.decodeStream(it)
                val matrix = Matrix().apply { setRotate(90f) }
                val rotatedBitmap = Bitmap.createBitmap(inputBitmap, 0, 0, inputBitmap.width, inputBitmap.height,  matrix, true)
                edit_imageView.setImageBitmap(rotatedBitmap)
            }
        } else if( requestCode == AddLocationActivity.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_CANCELED){
            Log.d("PhotoUpload", "Result canceled")
            Log.d("PhotoUpload", photo.name)
            photo.delete()
            Log.d("DeleteUpload", "photo ${photo.name}")
            if(myLocation.photoUri != null){
                val uri = Uri.parse(myLocation.photoUri)
                val photo: File = File(uri.path)
                photo.inputStream().use {
                    val inputBitmap = BitmapFactory.decodeStream(it)
                    if(inputBitmap != null){
                        val matrix = Matrix().apply { setRotate(90f) }
                        val rotatedBitmap = Bitmap.createBitmap(inputBitmap, 0, 0, inputBitmap.width, inputBitmap.height,  matrix, true)
                        Log.d("Items", "${myLocation.photoUri} bitmap width: ${inputBitmap.width} height: ${inputBitmap.height}")
                        edit_imageView.setImageBitmap(rotatedBitmap)
                    }
                }
            }
        }
    }
}