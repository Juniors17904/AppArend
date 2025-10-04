package com.example.apparend.ui;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apparend.R;
import com.example.apparend.Dao.TrabajoDao;
import com.example.apparend.adapters.HistorialAdapter;


import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private static final String TAG = "arenado Historial";

    RecyclerView recyclerViewHistorial;
    HistorialAdapter historialAdapter;
    TrabajoDao trabajoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        recyclerViewHistorial = findViewById(R.id.recyclerViewHistorial);
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar DAO
        trabajoDao = new TrabajoDao(this);

        // Obtener lista de trabajos desde la BD
        List<String> listaTrabajos = trabajoDao.getAllTrabajos();
        Log.d(TAG, "Trabajos encontrados: " + listaTrabajos.size());

        // Asignar adapter
        historialAdapter = new HistorialAdapter(listaTrabajos);
        recyclerViewHistorial.setAdapter(historialAdapter);
    }
}
