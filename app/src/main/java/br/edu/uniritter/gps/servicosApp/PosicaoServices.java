package br.edu.uniritter.gps.servicosApp;

import android.content.Context;
import android.location.Location;

import java.util.List;

public class PosicaoServices {

    private static PosicaoServices instance;
    private PosicaoDBServices dbLocal;
    private PosicaoDBServices dbRemoto;
    private Context context;

    private PosicaoServices(Context context) {
        dbRemoto = new PosicaoDBServiceFirebase();
        dbLocal = new PosicaoDBServiceSQLite(context);
        this.context = context;

    }
    public static PosicaoServices getInstance(Context context) {
        if (instance == null) {
            instance = new PosicaoServices(context);
        }
        return instance;
    }

    public void gravar(Location localizacao) {
        if (dbRemoto != null) {
            dbRemoto.salvar(localizacao);
        }
        if (dbLocal != null) {
            dbLocal.salvar(localizacao);
        }
        // poderia ter uma lista de PosicaoDBServices
        //for (PosicaoDBServices dbSer : listaDBServices) {
        //    dbSer.salvar(localizacao);
        //}
    }

    public List<PosicaoDBServices.Localizacao> getAllLocalizacao() {
        return dbLocal.getAllLocalizacao();
    }

}
