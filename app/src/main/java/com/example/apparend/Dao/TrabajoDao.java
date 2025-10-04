//package com.example.apparend.Dao;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//
//import com.example.apparend.db.DBHelper;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TrabajoDao {
//    private static final String TAG = "Trabajo Dao Arenado";
//    private DBHelper dbHelper;
//
//    public TrabajoDao(Context context) {
//        dbHelper = new DBHelper(context);
//    }
//
//    // Insertar un trabajo nuevo
//    public long insertTrabajo(String cliente, String descripcion, String fechaHora) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("cliente", cliente);
//        values.put("descripcion", descripcion);
//        values.put("fecha", fechaHora.split("   ")[0]); // antes de los 3 espacios va la fecha
//        values.put("hora", fechaHora.split("   ")[1]);  // después de los 3 espacios va la hora
//
//        long id = db.insert("Trabajos", null, values);
//        Log.d(TAG, "Insertando trabajo , id generado: " + id);
//
//        db.close();
//        return id;
//    }
//
//    // Obtener todos los trabajos
//    public List<String> getAllTrabajos() {
//        List<String> trabajos = new ArrayList<>();
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        Cursor cursor = db.rawQuery(
//                "SELECT id, cliente, descripcion, fecha, hora FROM Trabajos",
//                null
//        );
//
//        Log.d(TAG, "Consultando todos los trabajos... registros encontrados: " + cursor.getCount());
//
//        if (cursor.moveToFirst()) {
//            do {
//                int id = cursor.getInt(0);
//                String cliente = cursor.getString(1);
//                String descripcion = cursor.getString(2);
//                String fecha = cursor.getString(3);
//                String hora = cursor.getString(4);
//
//                String registro = "ID: " + id + " | " + cliente + " | " + descripcion + " | " + fecha + " " + hora;
//                Log.d(TAG, "Trabajo leído -> " + registro);
//
//                trabajos.add(registro);
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        db.close();
//        return trabajos;
//    }
//
//    // Eliminar un trabajo
//    public int deleteTrabajo(int id) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        int rows = db.delete("Trabajos", "id = ?", new String[]{String.valueOf(id)});
//        Log.d(TAG, "Eliminando trabajo con ID: " + id + " | filas afectadas: " + rows);
//        db.close();
//        return rows;
//    }
//
//
//
//
//
//}



package com.example.apparend.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.apparend.db.DBHelper;
import com.example.apparend.models.Trabajo;   // ✅ Import del modelo

import java.util.ArrayList;
import java.util.List;

public class TrabajoDao {
    private static final String TAG = "Trabajo Dao Arenado";
    private DBHelper dbHelper;

    public TrabajoDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Insertar un trabajo nuevo
    public long insertTrabajo(String cliente, String descripcion, String fechaHora) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cliente", cliente);
        values.put("descripcion", descripcion);
        values.put("fecha", fechaHora.split("   ")[0]); // antes de los 3 espacios va la fecha
        values.put("hora", fechaHora.split("   ")[1]);  // después de los 3 espacios va la hora

        long id = db.insert("Trabajos", null, values);
        Log.d(TAG, "Insertando trabajo , id generado: " + id);

        db.close();
        return id;
    }

    // Obtener todos los trabajos (como String)
    public List<String> getAllTrabajos() {
        List<String> trabajos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, cliente, descripcion, fecha, hora FROM Trabajos",
                null
        );

        Log.d(TAG, "Consultando todos los trabajos... registros encontrados: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String cliente = cursor.getString(1);
                String descripcion = cursor.getString(2);
                String fecha = cursor.getString(3);
                String hora = cursor.getString(4);

                String registro = "ID: " + id + " | " + cliente + " | " + descripcion + " | " + fecha + " " + hora;
                Log.d(TAG, "Trabajo leído -> " + registro);

                trabajos.add(registro);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return trabajos;
    }

    // ✅ Nuevo: Obtener todos los trabajos como objetos
    public List<Trabajo> getAllTrabajosObj() {
        List<Trabajo> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, cliente, descripcion, fecha, hora FROM Trabajos", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String cliente = cursor.getString(cursor.getColumnIndexOrThrow("cliente"));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                String hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"));

                lista.add(new Trabajo(id, cliente, descripcion, fecha + " " + hora));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    // Eliminar un trabajo
    public int deleteTrabajo(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("Trabajos", "id = ?", new String[]{String.valueOf(id)});
        Log.d(TAG, "Eliminando trabajo con ID: " + id + " | filas afectadas: " + rows);
        db.close();
        return rows;
    }
}
