package br.edu.uniritter.gps.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "BancoDeDados";
    private static final Integer DB_VERSION = 2;


    public static final String ID_COL = "id";
    public static final String LATITUDE_COL = "lat";
    public static final String LONGITUDE_COL = "lng";
    public static final String TIME_COL = "time";
    public static final String ENVIADO_COL = "enviado";
    public static final String TABLE_NAME = "localizacoes";
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //enviado nao = 0 / sim = 1
        String stm = "create table localizacoes (id integer primary key, lat real, lng real, time integer, enviado integer);";


        sqLiteDatabase.execSQL(stm);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int currentVersion) {
        String stm = "create table localizacoes (id integer primary key, lat real, lng real, time integer, enviado integer);";
        if (oldVersion == 0) {
            //sqLiteDatabase.execSQL(stm);
        }
        if (oldVersion == 2) {
            sqLiteDatabase.execSQL(stm);
        }
    }
}
