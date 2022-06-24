package br.edu.uniritter.gps.servicosApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.uniritter.gps.sqlite.DBHelper;

public class PosicaoDBServiceSQLite implements PosicaoDBServices{
    DBHelper dbHelper;

    public PosicaoDBServiceSQLite(Context context) {
        dbHelper = new DBHelper(context);
    }
    @Override
    public void salvar(Location loc) {
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        //id integer primary key, lat real, lng real, time integer, enviado integer);";
        values.put(DBHelper.LATITUDE_COL, loc.getLatitude());
        values.put(DBHelper.LONGITUDE_COL, loc.getLongitude());
        Date date = new Date();
        Instant instant = Instant.now();
        values.put(DBHelper.TIME_COL,instant.getEpochSecond());
        values.put(DBHelper.ENVIADO_COL, 0);

        long newRowId = writableDatabase.insert(DBHelper.TABLE_NAME,null,values);

    }

    @Override
    public List<Localizacao> getAllLocalizacao() {

        //https://developer.android.com/training/data-storage/sqlite?hl=pt-br

        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorLocalizacoes = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME, null);

        // on below line we are creating a new array list.
        ArrayList<Localizacao> localizacaoModalArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorLocalizacoes.moveToFirst()) {
            do {
                //Instant instant = Instant.ofEpochSecond(cursorLocalizacoes.getInt(2));
                //Date dt = Date.from(instant);
                // on below line we are adding the data from cursor to our array list.
                localizacaoModalArrayList.add(new Localizacao(cursorLocalizacoes.getFloat(1),
                        cursorLocalizacoes.getFloat(2),
                        cursorLocalizacoes.getInt(3),
                        cursorLocalizacoes.getInt(4)==1));
            } while (cursorLocalizacoes.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorLocalizacoes.close();
        return localizacaoModalArrayList;
    }

    @Override
    public List<Localizacao> getAllLocalizacaoData(long inicio, long fim) {
        return null;
    }

    @Override
    public List<Localizacao> getAllLocalizacaoNaoEnviadas() {

        //https://developer.android.com/training/data-storage/sqlite?hl=pt-br

        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorLocalizacoes = db.rawQuery(
                "SELECT * FROM " + DBHelper.TABLE_NAME+
                    " WHERE " + DBHelper.ENVIADO_COL +" =0"
                    , null);

        // on below line we are creating a new array list.
        ArrayList<Localizacao> localizacaoModalArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorLocalizacoes.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                localizacaoModalArrayList.add(new Localizacao(cursorLocalizacoes.getFloat(0),
                        cursorLocalizacoes.getFloat(1),
                        cursorLocalizacoes.getInt(2),
                        cursorLocalizacoes.getInt(3)==1));
            } while (cursorLocalizacoes.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorLocalizacoes.close();
        return localizacaoModalArrayList;

    }
}
