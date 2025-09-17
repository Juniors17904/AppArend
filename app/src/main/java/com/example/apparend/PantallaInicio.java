package com.example.apparend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log; // Importamos Log para utilizar los logcat
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

public class PantallaInicio extends AppCompatActivity {

    private static final String TAG = "arenado"; // Definimos el tag "arenado" para los logs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inicio); // Layout que tiene la imagen de inicio

        // Referencia a la imagen
        ImageView splashImage = findViewById(R.id.splashImage);
        Log.d(TAG, "Pantalla de inicio cargada correctamente.");

        // Puedes agregar más configuraciones si lo deseas.

        // Usamos Handler para esperar 5 segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Después de 5 segundos, iniciar MainActivity
                Log.d(TAG, "Iniciando MainActivity.");
                Intent intent = new Intent(PantallaInicio.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finaliza PantallaInicio para que no vuelva atrás
                Log.d(TAG, "Pantalla de inicio finalizada.");
            }
        }, 2500); // 5000 milisegundos = 5 segundos
    }
}
