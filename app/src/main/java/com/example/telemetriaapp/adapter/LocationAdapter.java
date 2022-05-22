package com.example.telemetriaapp.adapter;

import static java.lang.Math.round;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telemetriaapp.R;
import com.example.telemetriaapp.model.LocationModel;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<Holder> {


    List<LocationModel> locations;

    public LocationAdapter(List<LocationModel> locations) {
        this.locations = locations;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_location, parent,false);
        Holder holder = new Holder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        TextView tvID = holder.view.findViewById(R.id.tvID);
        TextView tvUser = holder.view.findViewById(R.id.tvUser);
        TextView tvLat = holder.view.findViewById(R.id.tvLat);
        TextView tvLong = holder.view.findViewById(R.id.tvLong);
        TextView tvDate = holder.view.findViewById(R.id.tvDate);

        tvID.setText(String.valueOf(locations.get(position).getId()));
        tvUser.setText(locations.get(position).getUser());
        //String str = String.format("%1.2f", d);
        tvLat.setText(String.valueOf(String.valueOf(locations.get(position).getLatitude())));
        tvLong.setText(String.valueOf(String.valueOf(round(locations.get(position).getLongitude()))));
        tvDate.setText(String.valueOf("")); //locations.get(position).getData()));

    }

    @Override
    public int getItemCount() {
        return locations.size();
    }
}
