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
    private static final int DB_VERSION = 1;
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

        db.execSQL("DROP TABLE IF EXISTS Piezas");
        db.execSQL("DROP TABLE IF EXISTS Estructuras");
        db.execSQL("DROP TABLE IF EXISTS Trabajos");

        Log.d(TAG, "Tablas eliminadas. Se recrearán...");
        onCreate(db);
    }

    //  Método para leer y ejecutar el archivo SQL
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
                //Log.d(TAG, "Ejecutando SQL: " + comando);
                db.execSQL(comando + ";");
            }
        }
    }
}
