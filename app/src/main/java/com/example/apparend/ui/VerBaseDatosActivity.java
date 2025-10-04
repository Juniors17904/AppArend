package com.example.apparend.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apparend.R;
import com.example.apparend.Dao.TrabajoDao;
import com.example.apparend.Dao.EstructuraDao;
import com.example.apparend.Dao.PiezaDao;
import com.example.apparend.models.Trabajo;
import com.example.apparend.models.Estructura;
import com.example.apparend.models.Pieza;

import java.util.ArrayList;
import java.util.List;

public class VerBaseDatosActivity extends AppCompatActivity {
    private static final String TAG = "arenado VerBaseDatos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_base_datos);

        // --- RecyclerView de Trabajos ---
        RecyclerView recyclerViewTrabajos = findViewById(R.id.recyclerViewTrabajos);
        recyclerViewTrabajos.setLayoutManager(new LinearLayoutManager(this));

        TrabajoDao trabajoDao = new TrabajoDao(this);
        List<Trabajo> trabajos = trabajoDao.getAllTrabajosObj();
        Log.d(TAG, "Trabajos en BD: " + trabajos.size());
        recyclerViewTrabajos.setAdapter(new SimpleRecyclerAdapter(convertirTrabajosAStrings(trabajos)));

        // --- RecyclerView de Estructuras ---
        RecyclerView recyclerViewEstructuras = findViewById(R.id.recyclerViewEstructuras);
        recyclerViewEstructuras.setLayoutManager(new LinearLayoutManager(this));

        EstructuraDao estructuraDao = new EstructuraDao(this);
        List<Estructura> estructuras = estructuraDao.getAllEstructuras();
        Log.d(TAG, "Estructuras en BD: " + estructuras.size());
        recyclerViewEstructuras.setAdapter(new SimpleRecyclerAdapter(convertirEstructurasAStrings(estructuras)));

        // --- RecyclerView de Piezas ---
        RecyclerView recyclerViewPiezas = findViewById(R.id.recyclerViewPiezas);
        recyclerViewPiezas.setLayoutManager(new LinearLayoutManager(this));

        PiezaDao piezaDao = new PiezaDao(this);
        List<Pieza> piezas = piezaDao.getAllPiezas();
        Log.d(TAG, "Piezas en BD: " + piezas.size());
        recyclerViewPiezas.setAdapter(new SimpleRecyclerAdapter(convertirPiezasAStrings(piezas)));


        Button btnBorrarTodo = findViewById(R.id.btnBorrarTodo);
        btnBorrarTodo.setOnClickListener(v -> {
            Log.w(TAG, "⚠️ Borrando toda la base de datos...");

            // Eliminar archivo de la base de datos
            deleteDatabase("miapp.db"); // 👈 usa el nombre de tu BD definido en DBHelper

            Toast.makeText(this, "Base de datos eliminada", Toast.LENGTH_SHORT).show();

            // Refrescar la pantalla para que ya no muestre nada
            recreate();
        });




    }

    // Métodos auxiliares para convertir objetos en texto
    private List<String> convertirTrabajosAStrings(List<Trabajo> trabajos) {
        List<String> lista = new ArrayList<>();
        for (Trabajo t : trabajos) {
            String texto = "ID: " + t.getId() +
                    "\nCliente: " + (t.getCliente() != null ? t.getCliente() : "N/A") +
                    "\nDesc: " + (t.getDescripcion() != null ? t.getDescripcion() : "N/A") +
                    "\nFechaHora: " + (t.getFechaHora() != null ? t.getFechaHora() : "N/A") +
                    "\n_______________";
          //  Log.d(TAG, "convertirTrabajosAStrings -> " + texto);
            lista.add(texto);
        }
        return lista;
    }

    private List<String> convertirEstructurasAStrings(List<Estructura> estructuras) {
        List<String> lista = new ArrayList<>();
        for (Estructura e : estructuras) {
            String texto = "ID: " + e.getId() +
                    "\nTrabajoID: " + e.getTrabajoId() +
                    "\nDesc: " + (e.getDescripcion() != null ? e.getDescripcion() : "N/A") +
                    "\nM2: " + e.getTotalM2() +
                    "\n_______________";
           // Log.d(TAG, "convertirEstructurasAStrings -> " + texto);
            lista.add(texto);
        }
        return lista;
    }

    private List<String> convertirPiezasAStrings(List<Pieza> piezas) {
        List<String> lista = new ArrayList<>();
        for (Pieza p : piezas) {
            String texto = "ID: " + p.getId() +
                    "\nId Estructura"+p.getEstructura_id()+
                    "\nMaterial: " + (p.getTipoMaterial() != null ? p.getTipoMaterial() : "N/A") +
                    "\nDescripción: " +p.getDescripcion()+
                    "\nAncho: " + p.getAncho() +
                    "\nAlto: " + p.getAlto() +
                    "\nLargo: " + p.getLargo() +
                    "\nCant: " + p.getCantidad() +
                    "\nM2: " + p.getTotalM2() +
                    "\n_______________";
          //  Log.d(TAG, "convertirPiezasAStrings -> " + texto);
            lista.add(texto);
        }
        return lista;
    }




}
