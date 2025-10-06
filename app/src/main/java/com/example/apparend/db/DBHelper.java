//package com.example.apparend.db;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
//public class DBHelper extends SQLiteOpenHelper {
//    private static final String DB_NAME = "miapp.db";
//    private static final int DB_VERSION = 1;
//    private Context context;
//    private static final String TAG = "Db Helper arenado";
//
//    public DBHelper(Context context) {
//        super(context, DB_NAME, null, DB_VERSION);
//        this.context = context;
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        Log.d(TAG, "Iniciando creación de la base de datos desde schema.sql...");
//
//        try {
//            ejecutarSQLDesdeAssets(db, "schema.sql");
//            Log.d(TAG, "Tablas creadas correctamente desde schema.sql");
//        } catch (IOException e) {
//            Log.e(TAG, "Error leyendo schema.sql", e);
//        }
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.w(TAG, "Actualizando base de datos de versión " + oldVersion + " a " + newVersion);
//
//        db.execSQL("DROP TABLE IF EXISTS Piezas");
//        db.execSQL("DROP TABLE IF EXISTS Estructuras");
//        db.execSQL("DROP TABLE IF EXISTS Trabajos");
//
//        Log.d(TAG, "Tablas eliminadas. Se recrearán...");
//        onCreate(db);
//    }
//
//    //  Método para leer y ejecutar el archivo SQL
//    private void ejecutarSQLDesdeAssets(SQLiteDatabase db, String fileName) throws IOException {
//        InputStream is = context.getAssets().open(fileName);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//
//        StringBuilder sb = new StringBuilder();
//        String linea;
//        while ((linea = reader.readLine()) != null) {
//            sb.append(linea).append("\n");
//        }
//        reader.close();
//
//        String[] comandos = sb.toString().split(";");
//        for (String comando : comandos) {
//            comando = comando.trim();
//            if (!comando.isEmpty()) {
//                //Log.d(TAG, "Ejecutando SQL: " + comando);
//                db.execSQL(comando + ";");
//            }
//        }
//    }
//}


package com.example.apparend.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "miapp.db";
    private static final int DB_VERSION = 2; // ← Incrementado de 1 a 2
    private Context context;
    private static final String TAG = "Db Helper arenado";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Iniciando creación de la base de datos desde schema.sql...");

        try {
            ejecutarSQLDesdeAssets(db, "schema.sql");
            Log.d(TAG, "Tablas creadas correctamente desde schema.sql");
        } catch (IOException e) {
            Log.e(TAG, "Error leyendo schema.sql", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Actualizando base de datos de versión " + oldVersion + " a " + newVersion);

        // Migración de versión 1 a 2: agregar columna unidadMedida
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE Piezas ADD COLUMN unidadMedida TEXT DEFAULT 'Metros'");
                Log.d(TAG, "✅ Columna unidadMedida agregada exitosamente a tabla Piezas");
            } catch (Exception e) {
                Log.e(TAG, "❌ Error al agregar columna unidadMedida", e);
            }
        }

        // Aquí puedes agregar más migraciones para futuras versiones
        // Ejemplo:
        // if (oldVersion < 3) {
        //     db.execSQL("ALTER TABLE ...");
        // }
    }

    // Método para leer y ejecutar el archivo SQL
    private void ejecutarSQLDesdeAssets(SQLiteDatabase db, String fileName) throws IOException {
        InputStream is = context.getAssets().open(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();
        String linea;
        while ((linea = reader.readLine()) != null) {
            sb.append(linea).append("\n");
        }
        reader.close();

        String[] comandos = sb.toString().split(";");
        for (String comando : comandos) {
            comando = comando.trim();
            if (!comando.isEmpty()) {
                db.execSQL(comando + ";");
            }
        }
    }
}