package com.app.spotsensesdk.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.spotsense.network.model.GeoFenceDatabaseModel
import com.app.spotsensesdk.R
import com.app.spotsensesdk.ui.adapter.GeofenceDataAdapter.MyViewHolder

class GeofenceDataAdapter(moviesList: List<GeoFenceDatabaseModel>) :
    RecyclerView.Adapter<MyViewHolder>() {
    private val moviesList: List<GeoFenceDatabaseModel>

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView
        var date: TextView

        init {
            title = view.findViewById<View>(R.id.title) as TextView
            date = view.findViewById<View>(R.id.year) as TextView
        }
    }

    init {
        this.moviesList = moviesList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.raw_geofence, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val geoFenceDatabaseModel: GeoFenceDatabaseModel = moviesList[position]
        holder.title.text = geoFenceDatabaseModel.name
        holder.date.text = geoFenceDatabaseModel.geofenceDate
    }

    override fun getItemCount(): Int {
        Log.e("funcalledss", "" + moviesList.size)
        return moviesList.size
    }
}