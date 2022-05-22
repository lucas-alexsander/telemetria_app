package com.example.telemetriaapp.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.telemetriaapp.R;
import com.example.telemetriaapp.adapter.LocationAdapter;
import com.example.telemetriaapp.model.LocationModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        List<LocationModel> locations = new ArrayList<>();

        RecyclerView rvLocations = (RecyclerView) findViewById(R.id.rvLocations);

        RecyclerView.Adapter adapter = new LocationAdapter(locations);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        rvLocations.setAdapter(adapter);
        rvLocations.setLayoutManager(layoutManager);


        LocationModel newLocation1 = new LocationModel(1, 10,20, "Luk", new Date());
        locations.add(newLocation1);
        adapter.notifyDataSetChanged();
        // é necessário solicitar permissão de acesso e
        // informar no manifest o uso das permissões

        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                        .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        // Localização precisa autorizada
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 3, new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull Location location) {
                                Log.d("TESTE:  ", "onLocationChanged: " + location);
                                LocationModel newLocation = new LocationModel(1, location.getLongitude(),
                                        location.getLatitude(), "Luk", new Date());
                                newLocation.setLocation(location);
                                location.getLatitude();
                                if(locations.size() == 0) {

                                }
                                else {
                                    Float distance = location.distanceTo(locations.get(locations.size()-1).getLocation());
                                    newLocation.setLatitude(distance);
                                }

                                location.getLongitude();
                                locations.add(newLocation);
                                adapter.notifyDataSetChanged();
                            }
                        });
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
// Somente localização aproximada autorizada
                            } else {
// Nenhuma localização autorizada
                            }
                        }
                );
// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }


    public void localizar(){


    }


}
