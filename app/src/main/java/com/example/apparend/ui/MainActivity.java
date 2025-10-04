package com.example.apparend.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apparend.R;
import com.example.apparend.db.DBHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "arenado Main Activity";

    Button btnNuevoTrabajo, btnHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DBHelper dbHelper = new DBHelper(this);
        dbHelper.getWritableDatabase();
        Log.d(TAG, "Base de datos inicializada (DBHelper)");

        // Botón para agregar nuevo trabajo
        btnNuevoTrabajo = findViewById(R.id.btnNuevoTrabajo);
        btnNuevoTrabajo.setOnClickListener(v -> {
            Log.d(TAG, "Botón 'Nuevo Trabajo' presionado.");
            Intent intent = new Intent(MainActivity.this, AgregarNuevoTrabajoActivity.class);
            startActivity(intent);
        });

        // Botón para ver historial de trabajos
        btnHistorial = findViewById(R.id.btnHistorial);
        btnHistorial.setOnClickListener(v -> {
            Log.d(TAG, "Botón 'Historial' presionado.");
            Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
            startActivity(intent);
        });



        Button btnVerBaseDatos = findViewById(R.id.btnVerBaseDatos);

        btnVerBaseDatos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VerBaseDatosActivity.class);
            startActivity(intent);
        });

    }
}
