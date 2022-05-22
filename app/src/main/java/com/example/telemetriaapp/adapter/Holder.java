package com.example.telemetriaapp.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Holder extends RecyclerView.ViewHolder {

    public View view;

    public Holder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
    }
}