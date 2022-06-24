package br.edu.uniritter.gps.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import br.edu.uniritter.atsd.gps.R;
import br.edu.uniritter.gps.adapter.PosicaoAdapter;
import br.edu.uniritter.gps.repositorios.PosicaoRepository;
import br.edu.uniritter.gps.servicosApp.PosicaoDBServices;
import br.edu.uniritter.gps.servicosApp.PosicaoServices;
import br.edu.uniritter.gps.viewmodel.PosicaoViewModel;

public class GPSActivity extends AppCompatActivity {
    public static final String TAG = "GPSActivity";
    //private FusedLocationProviderClient fusedLocationClient;

    private int intervaloLocation;
    //0 == a pé | 1 == veículo
    private int modoDeslocamento;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_activity);
        carregaPreferencias();
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
                                localizar();

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
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        setarRecyclerViewGPS();
    }

    private void localizar() {

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, intervaloLocation*1000, 3, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d(TAG, "onLocationChanged: " + location);

            }
        });

 /*        FusedLocationProviderClient fusedLocationClient;
         fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
         fusedLocationClient.getLastLocation()
                 .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                     @Override
                     public void onSuccess(Location location) {
                         Log.d(TAG, "onSuccess: "+location);
                     }
                 });

         fusedLocationClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY,
                 new CancellationToken() {
                     @Override
                     public boolean isCancellationRequested() {
                         return false;
                     }

                     @NonNull
                     @Override
                     public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                         return null;
                     }
                 })
                 .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                     @Override
                     public void onSuccess(Location location) {
                         Log.d(TAG, "onSuccess current location: "+location);
                     }
                 });
                 */

     }

    private void carregaPreferencias() {
        //Carrega-los em variaveis/constantes
        //que serão usadas pelo app
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if(preferences.contains("intervalo_location")){
            intervaloLocation = preferences.getInt("intervalo_location",5);
        }

        if(preferences.contains("modo_deslocamento")){
            modoDeslocamento = preferences.getInt("modo_deslocamento",0);
        }

        editor.commit();
    }

    private void setarRecyclerViewGPS() {
        RecyclerView recyclerView =  findViewById(R.id.rvRegistros);
        PosicaoAdapter adapter =  new PosicaoAdapter();
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        List<PosicaoDBServices.Localizacao> localizacoes = PosicaoServices.getInstance(this.getApplicationContext()).getAllLocalizacao();

        PosicaoViewModel viewmodel = new ViewModelProvider(this).get(PosicaoViewModel.class);
        PosicaoRepository.getInstance(this.getApplicationContext()).getPosicoes().observe(this,
                new Observer<List<Location>>() {
                    @Override
                    public void onChanged(List<Location> locations) {
                        adapter.refresh();
                    }
                }
        );
    }

}