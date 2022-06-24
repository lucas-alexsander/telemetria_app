package br.edu.uniritter.gps.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleService;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.tasks.Task;

import br.edu.uniritter.gps.repositorios.PosicaoRepository;

public class GPSService extends LifecycleService {

    public static final String TAG = "GPService";
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastLoc = null;
    private NotificationChannel canalNotificacao;

    private int intervaloLocation;
    //0 == a pé | 1 == veículo
    private int modoDeslocamento;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ponto 1");

        carregaPreferencias();

        // checagem de permissões para localização
        if (ActivityCompat.checkSelfPermission(this.getBaseContext(),
                                               Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                                &&
                                               ActivityCompat.checkSelfPermission(this.getBaseContext(),
                                                Manifest.permission.ACCESS_COARSE_LOCATION)
                                                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }

        Log.d(TAG, "onCreate: ponto 2");

        /*
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
                .addOnSuccessListener( new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d(TAG, "onSuccess current location: "+location);
                    }
                });
        */

        //Criar canal de notificação caso não exista ainda
        canalNotificacao = new NotificationChannel(
                "UniR",
                "Channel UniRitter",
                NotificationManager.IMPORTANCE_LOW
        );
        canalNotificacao.setDescription("Este é o canal de notificação do app modelo de aula");

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(canalNotificacao);

        //Cria uma notificação para rodar o serviço em foreground sem a aplicação aberta
        Notification notification =
                new Notification.Builder(this, "UniR")
                        .setContentTitle("Titulo")
                        .setContentText("Texto")
                        .build();

        // Notification ID cannot be 0.
        startForeground(1234, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //Toast.makeText(GPSService.this, "onStartCommand", Toast.LENGTH_LONG).show();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest currentLocationRequest = LocationRequest.create();

        // aqui os valores de interval devem ser configurados de acordo com os parâmentros definidos
        currentLocationRequest.setInterval(intervaloLocation*1000)
                .setMaxWaitTime(18000)
                .setSmallestDisplacement(0)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return Service.START_NOT_STICKY;
        }

        Task<Void> voidTask = fusedLocationClient.requestLocationUpdates(currentLocationRequest,
                new LocationCallback() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        float distancia = 0;
                        Location loc = locationResult.getLastLocation();

                        if (lastLoc != null) {
                            distancia = loc.distanceTo(lastLoc);
                        } else {
                            lastLoc = loc;
                        }
                        //envia localização para o repository
                        PosicaoRepository.getInstance(getApplicationContext()).incluir(loc, distancia);
                        if (distancia > loc.getAccuracy()) {
                            lastLoc = loc;
                        }
                    }
                }, null);
        Log.w(TAG, "onStartCommand ");
        if (intent == null) {
            return START_NOT_STICKY;
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        super.onBind(intent);
        return null;
    }

    private void carregaPreferencias() {
        //Carrega-los em variaveis/constantes
        //que serão usadas pelo app
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        if(preferences.contains("intervalo_location")){
            intervaloLocation = preferences.getInt("intervalo_location",5);
        }

        if(preferences.contains("modo_deslocamento")){
            modoDeslocamento = preferences.getInt("modo_deslocamento",0);
        }
    }



}
