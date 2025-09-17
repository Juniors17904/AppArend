package com.example.apparend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Importamos Log para utilizar los logcat
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "arenado"; // Definimos el tag "arenado" para los logs

    Button btnNuevoTrabajo, btnHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Botón para agregar nuevo trabajo
        btnNuevoTrabajo = findViewById(R.id.btnNuevoTrabajo);
        btnNuevoTrabajo.setOnClickListener(v -> {
            // Redirige a la pantalla para agregar un nuevo trabajo
            Log.d(TAG, "Botón 'Nuevo Trabajo' presionado.");
            Intent intent = new Intent(MainActivity.this, AgregarNuevoTrabajoActivity.class);
            startActivity(intent);
        });

        // Botón para ver historial de trabajos
        btnHistorial = findViewById(R.id.btnHistorial);
        btnHistorial.setOnClickListener(v -> {
            // Aquí puedes agregar la lógica para ver los trabajos realizados
            Log.d(TAG, "Botón 'Historial' presionado.");
            Intent intent = new Intent(MainActivity.this, HistorialActivity.class); // Suponiendo que tienes HistorialActivity
            startActivity(intent);
        });
    }
}
