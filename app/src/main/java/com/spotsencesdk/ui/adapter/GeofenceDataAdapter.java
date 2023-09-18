package com.spotsensesdk.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spotsensesdk.R;
import com.spotsense.data.network.model.GeoFenceDatabaseModel;

import java.util.List;

public class GeofenceDataAdapter extends RecyclerView.Adapter<GeofenceDataAdapter.MyViewHolder> {

    private List<GeoFenceDatabaseModel> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.year);
        }
    }


    public GeofenceDataAdapter(List<GeoFenceDatabaseModel> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_geofence, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GeoFenceDatabaseModel geoFenceDatabaseModel = moviesList.get(position);
        holder.title.setText(geoFenceDatabaseModel.getName());
        holder.date.setText(geoFenceDatabaseModel.getGeofenceDate());
    }

    @Override
    public int getItemCount() {
        Log.e("funcalledss",""+moviesList.size());
        return moviesList.size();
    }
}