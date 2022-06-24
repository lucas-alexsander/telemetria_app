package br.edu.uniritter.gps.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


import br.edu.uniritter.atsd.gps.R;
import br.edu.uniritter.gps.adapter.PosicaoAdapter;
import br.edu.uniritter.gps.viewmodel.PosicaoViewModel;
import br.edu.uniritter.gps.viewmodel.SensorsViewModel;
import br.edu.uniritter.gps.broadcastreceiver.GPSBroadcastReceiver;
import br.edu.uniritter.gps.repositorios.PosicaoRepository;
import br.edu.uniritter.gps.sqlite.DBHelper;

public class MainActivity extends AppCompatActivity {

    private SensorsViewModel viewmodel;
    int valor = 0;
    BroadcastReceiver br;

    private Switch swModoDeslocamento;
    private EditText etIntervaloLocation;
    private SeekBar sbIntervaloDeslocamento;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registrarIntentFilters();
        mAuth = FirebaseAuth.getInstance();

        swModoDeslocamento = (Switch) findViewById(R.id.swVeiculo);
        etIntervaloLocation = (EditText) findViewById(R.id.etIntervaloLocation);
        sbIntervaloDeslocamento = (SeekBar) findViewById(R.id.sbTempoDeslocamento);
        // é necessário solicitar permissão de acesso e
        // informar no manifest o uso das permissões
        verificaPermissoes();

        //Buscar as configurações no SharedPeferences
        carregaPreferencias();

        // aqui podesse usar um viewmodel para as ações desta activity
        //viewmodel = new ViewModelProvider(this).get(SensorsViewModel.class);
        //viewmodel.setContexto(this);

        //disparo para enviar intent para iniciar o serviço de GPS
        findViewById(R.id.btStartService).setOnClickListener(view -> {

            salvaPreferencias();

            Intent intent = new Intent();
            intent.setAction("br.edu.uniritter.GPS_START");
            getApplicationContext().sendBroadcast(intent);
        });

        setarRecyclerViewGPS();

       // DBHelper dbHelper = new DBHelper(this);
        //SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();


        findViewById(R.id.btLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), LoginActivity.class);
                MainActivity.this.startActivity(intent);
                finish();
            }
        });

        SeekBar sbTempoDeslocamento = (SeekBar) findViewById(R.id.sbTempoDeslocamento);
        sbTempoDeslocamento.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                etIntervaloLocation.setText("" + progress);
                //textView.setY(100); just added a value set this properly using screen with height aspect ratio , if you do not set it by default it will be there below seek bar

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void setarRecyclerViewGPS() {
        RecyclerView recyclerView =  findViewById(R.id.rvPosicao);
        PosicaoAdapter adapter =  new PosicaoAdapter();
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

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

    private void salvaPreferencias(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int intervaloLocation = Integer.valueOf(etIntervaloLocation.getText().toString());
        int modoDeslocamento = swModoDeslocamento.isChecked()?1:0;
        editor.putInt("intervalo_location", intervaloLocation);
        editor.putInt("modo_deslocamento", modoDeslocamento);
        editor.commit();
    }

    private void carregaPreferencias() {
        //tem que criar os valores nas preferências e carrega-los em variaveis/constantes
        //que serão usadas pelo app
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int intervaloLocation = 5000;
        //0 == a pé | 1 == veículo
        int modoDeslocamento = 0;
        int qtd = 0;

        if (preferences.contains("qtd")) {
            qtd = preferences.getInt("qtd",0)+1;
        }
        editor.putInt("qtd", qtd);

        if(preferences.contains("intervalo_location")){
            intervaloLocation = preferences.getInt("intervalo_location",5000);
        }
        etIntervaloLocation.setText(String.valueOf(intervaloLocation));
        sbIntervaloDeslocamento.setProgress(intervaloLocation);
        editor.putInt("intervalo_location", intervaloLocation);

        if(preferences.contains("modo_deslocamento")){
            modoDeslocamento = preferences.getInt("modo_deslocamento",0);
        }

        swModoDeslocamento.setChecked(modoDeslocamento==1);
        editor.putInt("modo_deslocamento", modoDeslocamento);
        
        editor.commit();
    }

    // é necessário solicitar permissão de acesso e
    // informar no manifest o uso das permissões
    private void verificaPermissoes() {
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
                                Toast.makeText(this, "Localização autorizado", Toast.LENGTH_SHORT).show();


                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                Toast.makeText(this, "Somente localização aproximada", Toast.LENGTH_SHORT).show();
                                // Somente localização aproximada autorizada
                            } else {
                                // Nenhuma localização autorizada
                                Toast.makeText(this, "Localização não autorizada", Toast.LENGTH_SHORT).show();
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

    // registra os intents filters para o disparo do serviço de GPS da aplicação
    // e o para iniciar a app quando o dispositivo for ligado
    // Mais informações sobre onde encontrar as actions possível você encontra no
    // SDK do Android do diretório platform
    private void registrarIntentFilters() {
        br = new GPSBroadcastReceiver();
        IntentFilter intf = new IntentFilter("br.edu.uniritter.GPS_START");
        IntentFilter intf1 = new IntentFilter("android.intent.action.BOOT_COMPETED");

        registerReceiver(br, intf);
        registerReceiver(br, intf1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // uma boa prática é desregistrar o brodcastreceiver no final da aplicação
        //unregisterReceiver(br);
    }
}