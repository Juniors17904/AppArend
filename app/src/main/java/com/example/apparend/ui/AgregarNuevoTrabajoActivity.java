package com.example.apparend.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apparend.Dao.EstructuraDao;
import com.example.apparend.Dao.PiezaDao;
import com.example.apparend.R;
import com.example.apparend.models.Pieza;
import com.example.apparend.models.Estructura;          // <- agregado
import com.example.apparend.adapters.EstructuraAdapter; // <- agregado
import com.example.apparend.Dao.TrabajoDao;
import java.util.ArrayList;
import java.util.List;

public class AgregarNuevoTrabajoActivity extends AppCompatActivity {

    private static final String TAG = "Agregar Nuevo Trabajo arenado";

    EditText etCliente, etDescripcion;
    Button btnAgregarEstructura, btnGenerarReporte, btnGuardarTrabajo;
    TextView tvCantidadEstructuras, tvMetrosCuadrados, tvMontoTotal;
    int cantidadEstructuras = 0;
    double metrosCuadrados = 0;
    double montoTotal = 0;

    // RecyclerView y Adapter
    RecyclerView recyclerViewEstructuras;
    EstructuraAdapter estructuraAdapter;
    List<Estructura> listaEstructuras = new ArrayList<>();
    TextView tvFechaHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_nuevo_trabajo);

        // Inicialización de elementos
        etCliente = findViewById(R.id.etCliente);
        etDescripcion = findViewById(R.id.etDescripcionPieza);
        btnAgregarEstructura = findViewById(R.id.btnAgregarEstructura);
        btnGenerarReporte = findViewById(R.id.btnGenerarReporte);
        btnGuardarTrabajo = findViewById(R.id.btnGuardarTrabajo);
        tvCantidadEstructuras = findViewById(R.id.tvCantidadEstructuras);
        tvMetrosCuadrados = findViewById(R.id.tvMetrosCuadrados);
        tvMontoTotal = findViewById(R.id.tvMontoTotal);

        recyclerViewEstructuras = findViewById(R.id.recyclerViewEstructura);
        recyclerViewEstructuras.setLayoutManager(new LinearLayoutManager(this));
        estructuraAdapter = new EstructuraAdapter(listaEstructuras, this);
        recyclerViewEstructuras.setAdapter(estructuraAdapter);

        tvFechaHora = findViewById(R.id.tvFechaHora);

        // Obtener la fecha y hora actuales
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy   HH:mm");
        String currentDateAndTime = dateFormat.format(new Date());
        tvFechaHora.setText(currentDateAndTime);


        // Lógica de agregar estructura
        btnAgregarEstructura.setOnClickListener(v -> {
            String cliente = etCliente.getText().toString();
            String descripcion = etDescripcion.getText().toString();
            String fechaHora = tvFechaHora.getText().toString();

            // 👉 Ya NO insertamos en la BD aquí
            // Solo pasamos datos en memoria
            Intent intent = new Intent(AgregarNuevoTrabajoActivity.this, AgregarEstructuraActivity.class);
            intent.putExtra("cliente", cliente);
            intent.putExtra("descripcion", descripcion);
            intent.putExtra("fechaHora", fechaHora);
            startActivityForResult(intent, 1); // importante usar forResult
        });

//        // Lógica de agregar estructura
//        btnAgregarEstructura.setOnClickListener(v -> {
//            String cliente = etCliente.getText().toString();
//            String descripcion = etDescripcion.getText().toString();
//            String fechaHora = tvFechaHora.getText().toString();
//
//            // 👉 Ya NO insertamos en la BD aquí
//            // Solo pasamos datos en memoria
//            Intent intent = new Intent(AgregarNuevoTrabajoActivity.this, AgregarEstructuraActivity.class);
//            intent.putExtra("cliente", cliente);
//            intent.putExtra("descripcion", descripcion);
//            intent.putExtra("fechaHora", fechaHora);
//            startActivityForResult(intent, 1); // importante usar forResult
//        });

        btnGenerarReporte.setOnClickListener(v -> {
            generarReporte();
        });

        // Lógica para finalizar el trabajo
        btnGuardarTrabajo.setOnClickListener(v -> {
            guardarTrabajo();

        });
    }

    // Método para recibir datos de AgregarEstructuraActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Estructura nuevaEstructura = (Estructura) data.getSerializableExtra("nuevaEstructura");

            if (nuevaEstructura != null) {
                listaEstructuras.add(nuevaEstructura);
                estructuraAdapter.notifyItemInserted(listaEstructuras.size() - 1);
                actualizarTotales();

                Log.d(TAG, "onActivityResult -> estructura recibida con id="
                        + nuevaEstructura.getId() + ", descripcion="
                        + nuevaEstructura.getDescripcion()
                        + ", totalM2=" + nuevaEstructura.getTotalM2());
            } else {
                Log.w(TAG, "onActivityResult -> no se recibió estructura");
            }
        }
    }


    private void actualizarTotales() {
        cantidadEstructuras = listaEstructuras.size();
        metrosCuadrados = 0;
        montoTotal = 0;

        for (Estructura estructura : listaEstructuras) {
            metrosCuadrados += estructura.getTotalM2();
        }

        tvCantidadEstructuras.setText(String.valueOf(cantidadEstructuras));
        tvMetrosCuadrados.setText(String.format("%.2f m²", metrosCuadrados));
        tvMontoTotal.setText(String.format("$%.2f", montoTotal));
    }

    private void generarReporte() {
        // Lógica para generar reporte
    }

//    private void guardarTrabajo() {
//        String cliente = etCliente.getText().toString().trim();
//        String descripcion = etDescripcion.getText().toString().trim();
//        String fechaHora = tvFechaHora.getText().toString();
//
//        // 🔹 Validaciones
//        if (cliente.isEmpty()) {
//            Toast.makeText(this, "Ingresa el nombre del cliente", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (listaEstructuras.isEmpty()) {
//            Toast.makeText(this, "Debes agregar al menos una estructura", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // 🔹 Insertar el trabajo en BD
//        TrabajoDao trabajoDao = new TrabajoDao(this);
//        long idTrabajo = trabajoDao.insertTrabajo(cliente, descripcion, fechaHora);
//        Log.d(TAG, "Trabajo guardado en BD con id=" + idTrabajo);
//
//        // 🔹 Insertar estructuras y piezas
//        EstructuraDao estructuraDao = new EstructuraDao(this);
//        PiezaDao piezaDao = new PiezaDao(this);
//
//        for (Estructura estructura : listaEstructuras) {
//            long idEstructura = estructuraDao.insertEstructura(
//                    (int) idTrabajo,
//                    estructura.getDescripcion(),
//                    estructura.getImagenUri()
//            );
//            Log.d(TAG, "Estructura guardada con id=" + idEstructura);
//
//            if (estructura.getListaPiezas() != null) {
//                for (Pieza pieza : estructura.getListaPiezas()) {
//                    long idPieza = piezaDao.insertPieza(pieza, (int) idEstructura);
//                    Log.d(TAG, "Pieza guardada con id=" + idPieza);
//                }
//            }
//        }
//
//        Toast.makeText(this, "Trabajo completo guardado en BD", Toast.LENGTH_LONG).show();
//        Log.i(TAG, "Trabajo completo guardado en BD con todas sus estructuras y piezas.");
//
//        finish();
//    }

    private void guardarTrabajo() {
        String cliente = etCliente.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String fechaHora = tvFechaHora.getText().toString();

        // 🔹 Validaciones
        if (cliente.isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre del cliente", Toast.LENGTH_SHORT).show();
            return;
        }
        if (listaEstructuras.isEmpty()) {
            Toast.makeText(this, "Debes agregar al menos una estructura", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Guardar Trabajo
            TrabajoDao trabajoDao = new TrabajoDao(this);
            long idTrabajo = trabajoDao.insertTrabajo(cliente, descripcion, fechaHora);
            Log.d(TAG, "✅ Trabajo guardado en BD con id=" + idTrabajo);

            // Guardar estructuras y piezas
            EstructuraDao estructuraDao = new EstructuraDao(this);
            PiezaDao piezaDao = new PiezaDao(this);

            for (Estructura estructura : listaEstructuras) {
                try {
                    long idEstructura = estructuraDao.insertEstructura(
                            (int) idTrabajo,
                            estructura.getDescripcion(),
                            estructura.getImagenUri()
                    );
                    Log.d(TAG, "✅ Estructura guardada con id=" + idEstructura);

                    if (estructura.getListaPiezas() != null) {
                        for (Pieza pieza : estructura.getListaPiezas()) {
                            try {
                                long idPieza = piezaDao.insertPieza(pieza, (int) idEstructura);
                                Log.d(TAG, "✅ Pieza guardada con id=" + idPieza);
                            } catch (Exception e) {
                                Log.e(TAG, "⚠️ Error al guardar pieza: " + pieza.getDescripcion(), e);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "⚠️ Error al guardar estructura: " + estructura.getDescripcion(), e);
                }
            }

            Toast.makeText(this, "Trabajo completo guardado en BD", Toast.LENGTH_LONG).show();
            Log.i(TAG, "✅ Trabajo completo guardado en BD con todas sus estructuras y piezas.");

            finish();

        } catch (Exception e) {
            Log.e(TAG, "⚠️ Error general al guardar el trabajo", e);
            Toast.makeText(this, "Error al guardar el trabajo", Toast.LENGTH_SHORT).show();
        }
    }






}
