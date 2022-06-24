package br.edu.uniritter.gps.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.edu.uniritter.atsd.gps.R;
import br.edu.uniritter.gps.adapter.PosicaoAdapter;
import br.edu.uniritter.gps.repositorios.PosicaoRepository;
import br.edu.uniritter.gps.servicosApp.PosicaoDBServices;
import br.edu.uniritter.gps.servicosApp.PosicaoServices;
import br.edu.uniritter.gps.viewmodel.PosicaoViewModel;

public class MainActivityAdmin extends AppCompatActivity {


    private FirebaseAuth mAuth;

    private int diasColetados = 0;
    private int distanciaPercorrida = 0;
    private long tempoDeslocamento = 0;
    private int tempoImobilidade = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.buttonMap).setOnClickListener((v)->{
            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GPSActivity.class);
                MainActivityAdmin.this.startActivity(intent);
            }
        });

        findViewById(R.id.btLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivityAdmin.this.getApplicationContext(), LoginActivity.class);
                MainActivityAdmin.this.startActivity(intent);
                finish();
            }
        });

        List<PosicaoDBServices.Localizacao> localizacoes = PosicaoServices.getInstance(this.getApplicationContext()).getAllLocalizacao();

        Date dataInicial = null;
        Date dataFinal = null;
        PosicaoDBServices.Localizacao locAnterior = null;
        for(PosicaoDBServices.Localizacao loc: localizacoes){
            if(locAnterior != null){
                int dist = loc.getLocalizacao().compareTo(locAnterior.getLocalizacao());
                distanciaPercorrida += dist;
            }
            else{
                dataInicial = loc.getData();
            }
            locAnterior = loc;
        }
        dataFinal = locAnterior.getData();

        tempoDeslocamento = (dataFinal.getTime()- dataInicial.getTime())/1000;

        EditText etDistanciaPercorrida = (EditText) findViewById(R.id.tvDistanciaPercorrida);
        etDistanciaPercorrida.setText(String.valueOf(distanciaPercorrida));

        EditText etTempoDeslocamento = (EditText) findViewById(R.id.etTempoDeslocamento);
        etTempoDeslocamento.setText(String.valueOf(tempoDeslocamento));



    }


}