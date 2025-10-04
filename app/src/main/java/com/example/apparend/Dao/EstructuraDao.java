package com.example.apparend.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.apparend.db.DBHelper;
import com.example.apparend.models.Estructura;

import java.util.ArrayList;
import java.util.List;

public class EstructuraDao {
    private static final String TAG = "Estructura Dao Arenado";
    private final DBHelper dbHelper;

    public EstructuraDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long insertEstructura(int trabajoId, String descripcion, String imagen) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trabajo_id", trabajoId);
        values.put("descripcion", descripcion);
        values.put("imagen", imagen);

        long id = db.insert("Estructuras", null, values);
        Log.d(TAG, "insertEstructura -> id=" + id + " trabajoId=" + trabajoId);
        db.close();
        return id;
    }

    public List<String> getEstructurasByTrabajo(int trabajoId) {
        List<String> res = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id, descripcion, imagen FROM Estructuras WHERE trabajo_id=?",
                new String[]{String.valueOf(trabajoId)}
        );
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String desc = c.getString(1);
            String img = c.getString(2);
            res.add("ID:" + id + " | " + desc + " | img:" + img);
        }
        c.close();
        db.close();
        Log.d(TAG, "getEstructurasByTrabajo -> trabajoId=" + trabajoId + " count=" + res.size());
        return res;
    }

    public int deleteEstructura(int estructuraId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("Estructuras", "id=?", new String[]{String.valueOf(estructuraId)});
        Log.d(TAG, "deleteEstructura -> id=" + estructuraId + " rows=" + rows);
        db.close();
        return rows;
    }

    // En tu archivo: com.example.apparend.Dao.EstructuraDao.java

    public List<Estructura> getAllEstructuras() {
        List<Estructura> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, trabajo_id, descripcion, imagen FROM Estructuras",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int trabajoId = cursor.getInt(cursor.getColumnIndexOrThrow("trabajo_id"));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                String imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"));

                // 👀 Como la tabla no tiene totalM2, lo dejamos en 0 por defecto
                lista.add(new Estructura(id, trabajoId, descripcion, imagen, 0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }


}
