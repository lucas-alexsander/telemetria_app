package br.edu.uniritter.gps.gps.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


import br.edu.uniritter.atsd.gps.R;
import br.edu.uniritter.gps.gps.viewmodel.SensorsViewModel;
import br.edu.uniritter.gps.receiver.GPSBroadcastReceiver;
import br.edu.uniritter.gps.sqlite.DBHelper;
import br.edu.uniritter.gps.views.GPSActivity;

public class MainActivity extends AppCompatActivity {

    //BROADCAST RECEIVER
    //LOCKED BOOT
    private FirebaseAuth mAuth;
    private SensorsViewModel viewmodel;
    int valor = 0;
    BroadcastReceiver br;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        br = new GPSBroadcastReceiver();
        IntentFilter intf = new IntentFilter("br.edu.uniritter.GPS_START");
        IntentFilter intf1 = new IntentFilter("android.intent.action.BOOT_COMPETED");

        registerReceiver(br, intf);

        // é necessário solicitar permissão de acesso e
        // informar no manifest o uso das permissões

        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                    Boolean backgroundLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted && backgroundLocationGranted) {
                                Log.d("MainActivity", "onCreate: autorizado GPS");

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
        //registerReceiver(br, intf1);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        int qtd = 0;
        if (preferences.contains("qtd")) {
            qtd = preferences.getInt("qtd",0)+1;
        }
        editor.putInt("qtd", qtd);
        editor.commit();
        ((TextView) findViewById(R.id.textViewNome)).setText(""+qtd);

        viewmodel = new ViewModelProvider(this).get(SensorsViewModel.class);
        viewmodel.setContexto(this);

        viewmodel.getNome().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                ((TextView) findViewById(R.id.textViewNome)).setText(s);
            }
        });
        viewmodel.getSensores().observe(this, new Observer<List<Sensor>>() {
            @Override
            public void onChanged(List<Sensor> sensors) {
                LinearLayout layout = findViewById(R.id.layoutSensores);
                layout.removeAllViews();
                for (Sensor sensor : sensors) {
                    TextView tv = new TextView(getBaseContext());
                    tv.setText(sensor.getName()+ " "+ sensor.getStringType());
                    layout.addView(tv);


                }

            }
        });
        findViewById(R.id.button2).setOnClickListener(view->{
            viewmodel.setNome("Jean Paul "+ valor++);
            Intent intent = new Intent();
            intent.setAction("br.edu.uniritter.GPS_START");
            getApplicationContext().sendBroadcast(intent);
        });
        findViewById(R.id.btNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), GPSActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        findViewById(R.id.btLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), LoginActivity.class);
                MainActivity.this.startActivity(intent);
                finish();
            }
        });



        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(br);
    }
}