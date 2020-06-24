package com.example.prm_project2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prm_project2.db.VisitedLocation
import kotlinx.android.synthetic.main.activity_add_location.*
import kotlinx.android.synthetic.main.item_location.view.*
import java.io.File


interface OnItemListener{
    fun onItemClick(position: Int)
    fun onItemLongClick(position: Int)
}


class MyViewHolder(view: View, val onItemListener: OnItemListener): RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener{
    init {
        //attaching onClickListener to MyViewHolder
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    fun refreshData(location: VisitedLocation){
        if(location.photoUri != null){
            val uri = Uri.parse(location.photoUri)
            val photo: File = File(uri.path)
            photo.inputStream().use {
                val inputBitmap = BitmapFactory.decodeStream(it)
                if(inputBitmap != null){
                    val matrix = Matrix().apply { setRotate(90f) }
                    val rotatedBitmap = Bitmap.createBitmap(inputBitmap, 0, 0, inputBitmap.width, inputBitmap.height,  matrix, true)
                    Log.d("Items", "${location.photoUri} bitmap width: ${inputBitmap.width} height: ${inputBitmap.height}")
                    itemView.locationImage.setImageBitmap(rotatedBitmap)
                }
            }
            itemView.locationName.text = location.name
            itemView.locationDiameter.text = location.diameter.toString()+" [km]"
        } else {
            itemView.locationName.text = location.name
            itemView.locationDiameter.text = location.diameter.toString()+" [km]"
        }
    }



    override fun onClick(v: View?) {
        onItemListener.onItemClick(adapterPosition)
    }

    override fun onLongClick(v: View?): Boolean {
        onItemListener.onItemLongClick(adapterPosition)
        return true
    }



}

class MyAdapter(val allLocations: List<VisitedLocation>, private val onItemListener: OnItemListener,val selectedLocations: MutableList<VisitedLocation>) : RecyclerView.Adapter<MyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false), onItemListener
        )
    }

    override fun getItemCount(): Int = allLocations.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.refreshData(allLocations[position])
        val currentItem = allLocations[position]
        holder.itemView.checkBox2.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == true){
                selectedLocations.add(currentItem)
            } else {
                selectedLocations.remove(currentItem)
            }
        }
    }





}