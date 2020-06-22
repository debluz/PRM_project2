package com.example.prm_project2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prm_project2.db.VisitedLocation
import kotlinx.android.synthetic.main.item_location.view.*


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
        itemView.locationName.text = location.name
        itemView.locationDiameter.text = location.diameter.toString()
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    override fun onLongClick(v: View?): Boolean {
        TODO("Not yet implemented")
    }

}

class MyAdapter(val allLocations: List<VisitedLocation>, private val onItemListener: OnItemListener) : RecyclerView.Adapter<MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false), onItemListener
        )
    }

    override fun getItemCount(): Int = allLocations.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.refreshData(allLocations[position])
    }

}