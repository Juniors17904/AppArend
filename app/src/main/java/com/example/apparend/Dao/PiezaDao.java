//package com.example.apparend.Dao;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//import com.example.apparend.db.DBHelper;
//import com.example.apparend.models.Pieza;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class PiezaDao {
//    private DBHelper dbHelper;
//
//    public PiezaDao(Context context) {
//        dbHelper = new DBHelper(context);
//    }
//
//    // ✅ Insertar una pieza en la BD
//    public long insertPieza(Pieza pieza, int estructuraId) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("estructura_id", estructuraId);
//        values.put("tipoMaterial", pieza.getTipoMaterial());
//        values.put("ancho", pieza.getAncho());
//        values.put("alto", pieza.getAlto());
//        values.put("largo", pieza.getLargo());
//        values.put("cantidad", pieza.getCantidad());
//        values.put("descripcion", pieza.getDescripcion()); // <- ahora sí
//        values.put("totalM2", pieza.getTotalM2());
//
//        long id = db.insert("Piezas", null, values);
//        db.close();
//        return id;
//    }
//
//    // ✅ Obtener todas las piezas de una estructura
//    public List<Pieza> getPiezasByEstructura(int estructuraId) {
//        List<Pieza> piezas = new ArrayList<>();
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        Cursor cursor = db.rawQuery(
//                "SELECT id, tipoMaterial, ancho, alto, largo, cantidad, descripcion, totalM2 " +
//                        "FROM Piezas WHERE estructura_id = ?",
//                new String[]{String.valueOf(estructuraId)}
//        );
//
//        if (cursor.moveToFirst()) {
//            do {
//                Pieza pieza = new Pieza(
//                        cursor.getString(1), // tipoMaterial
//                        cursor.getFloat(2),  // ancho
//                        cursor.getFloat(3),  // alto
//                        cursor.getFloat(4),  // largo
//                        cursor.getInt(5),    // cantidad
//                        cursor.getFloat(7)   // totalM2
//                );
//                pieza.setDescripcion(cursor.getString(6)); // <- se asigna descripción
//                piezas.add(pieza);
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        db.close();
//        return piezas;
//    }
//
//
//
//    public List<Pieza> getAllPiezas() {
//        List<Pieza> lista = new ArrayList<>();
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        Cursor cursor = db.rawQuery(
//                "SELECT tipoMaterial, ancho, alto, largo, cantidad, totalM2, descripcion FROM Piezas",
//                null
//        );
//
//        if (cursor.moveToFirst()) {
//            do {
//                String tipoMaterial = cursor.getString(cursor.getColumnIndexOrThrow("tipoMaterial"));
//                float ancho = cursor.getFloat(cursor.getColumnIndexOrThrow("ancho"));
//                float alto = cursor.getFloat(cursor.getColumnIndexOrThrow("alto"));
//                float largo = cursor.getFloat(cursor.getColumnIndexOrThrow("largo"));
//                int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));
//                float totalM2 = cursor.getFloat(cursor.getColumnIndexOrThrow("totalM2"));
//                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
//
//                lista.add(new Pieza(tipoMaterial, ancho, alto, largo, cantidad, totalM2, descripcion));
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        db.close();
//        return lista;
//    }
//
//}

package com.example.apparend.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.apparend.db.DBHelper;
import com.example.apparend.models.Pieza;

import java.util.ArrayList;
import java.util.List;

public class PiezaDao {
    private DBHelper dbHelper;

    public PiezaDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    // ✅ Insertar una pieza en la BD
    public long insertPieza(Pieza pieza, int estructuraId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("estructura_id", estructuraId);
        values.put("tipoMaterial", pieza.getTipoMaterial());
        values.put("ancho", pieza.getAncho());
        values.put("alto", pieza.getAlto());
        values.put("largo", pieza.getLargo());
        values.put("cantidad", pieza.getCantidad());
        values.put("descripcion", pieza.getDescripcion());
        values.put("totalM2", pieza.getTotalM2());

        long id = db.insert("Piezas", null, values);
        db.close();

        // 🔹 Asignar valores al objeto en memoria
        pieza.setId((int) id);
        pieza.setEstructura_id(estructuraId);

        return id;
    }

    // ✅ Obtener todas las piezas de una estructura
    public List<Pieza> getPiezasByEstructura(int estructuraId) {
        List<Pieza> piezas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, estructura_id, tipoMaterial, ancho, alto, largo, cantidad, descripcion, totalM2 " +
                        "FROM Piezas WHERE estructura_id = ?",
                new String[]{String.valueOf(estructuraId)}
        );

        if (cursor.moveToFirst()) {
            do {
                Pieza pieza = new Pieza(
                        cursor.getInt(0),     // id
                        cursor.getInt(1),     // estructura_id
                        cursor.getString(2),  // tipoMaterial
                        cursor.getFloat(3),   // ancho
                        cursor.getFloat(4),   // alto
                        cursor.getFloat(5),   // largo
                        cursor.getInt(6),     // cantidad
                        cursor.getFloat(8),   // totalM2
                        cursor.getString(7)   // descripcion
                );
                piezas.add(pieza);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return piezas;
    }

    // ✅ Obtener todas las piezas (sin filtrar por estructura)
    public List<Pieza> getAllPiezas() {
        List<Pieza> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, estructura_id, tipoMaterial, ancho, alto, largo, cantidad, descripcion, totalM2 FROM Piezas",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Pieza pieza = new Pieza(
                        cursor.getInt(0),     // id
                        cursor.getInt(1),     // estructura_id
                        cursor.getString(2),  // tipoMaterial
                        cursor.getFloat(3),   // ancho
                        cursor.getFloat(4),   // alto
                        cursor.getFloat(5),   // largo
                        cursor.getInt(6),     // cantidad
                        cursor.getFloat(8),   // totalM2
                        cursor.getString(7)   // descripcion
                );
                lista.add(pieza);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }
}
