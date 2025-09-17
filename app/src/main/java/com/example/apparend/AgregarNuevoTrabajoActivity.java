package com.example.apparend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AgregarNuevoTrabajoActivity extends AppCompatActivity {

    EditText etCliente, etDescripcion;
    Button btnAgregarEstructura, btnGenerarReporte, btnFinalizar;
    TextView tvCantidadEstructuras, tvMetrosCuadrados, tvMontoTotal;
    int cantidadEstructuras = 0;
    double metrosCuadrados = 0;
    double montoTotal = 0;

    // RecyclerView y Adapter
    RecyclerView recyclerViewPiezas;
    PiezaAdapter piezaAdapter;
    List<Pieza> listaPiezas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_nuevo_trabajo);

        // Inicialización de elementos
        etCliente = findViewById(R.id.etCliente);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnAgregarEstructura = findViewById(R.id.btnAgregarEstructura);
        btnGenerarReporte = findViewById(R.id.btnGenerarReporte);
        btnFinalizar = findViewById(R.id.btnFinalizar);
        tvCantidadEstructuras = findViewById(R.id.tvCantidadEstructuras);
        tvMetrosCuadrados = findViewById(R.id.tvMetrosCuadrados);
        tvMontoTotal = findViewById(R.id.tvMontoTotal);

        // RecyclerView - CONSTRUCTOR MODIFICADO (3 parámetros)
        recyclerViewPiezas = findViewById(R.id.recyclerViewPiezas);
        recyclerViewPiezas.setLayoutManager(new LinearLayoutManager(this));

        piezaAdapter = new PiezaAdapter(listaPiezas, this, new PiezaAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Pieza pieza, int position) {
                // Acción para editar una pieza
                // Ejemplo: abrir actividad de edición
                // Intent intent = new Intent(AgregarNuevoTrabajoActivity.this, EditarPiezaActivity.class);
                // intent.putExtra("pieza", pieza);
                // intent.putExtra("position", position);
                // startActivityForResult(intent, 1);
            }

            @Override
            public void onDeleteClick(Pieza pieza, int position) {
                // Eliminar pieza
                listaPiezas.remove(position);
                piezaAdapter.notifyItemRemoved(position);
                // Actualizar totales
                actualizarTotales();
            }
        });

        recyclerViewPiezas.setAdapter(piezaAdapter);

        // Lógica de agregar estructura
        btnAgregarEstructura.setOnClickListener(v -> {
            // Redirige a la pantalla para agregar una nueva estructura
            Intent intent = new Intent(AgregarNuevoTrabajoActivity.this, AgregarEstructuraActivity.class);
            startActivityForResult(intent, 1); // Usar startActivityForResult para recibir datos
        });

        // Lógica para generar el reporte
        btnGenerarReporte.setOnClickListener(v -> {
            // Aquí puedes generar el reporte de las piezas
            generarReporte();
        });

        // Lógica para finalizar el trabajo
        btnFinalizar.setOnClickListener(v -> {
            // Guardar trabajo y finalizar la actividad
            guardarTrabajo();
            finish();
        });
    }

    // Método para recibir datos de AgregarEstructuraActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Pieza nuevaPieza = (Pieza) data.getSerializableExtra("nuevaPieza");
            if (nuevaPieza != null) {
                listaPiezas.add(nuevaPieza);
                piezaAdapter.notifyItemInserted(listaPiezas.size() - 1);
                actualizarTotales();
            }
        }
    }

    private void actualizarTotales() {
        // Calcular totales basados en las piezas
        cantidadEstructuras = listaPiezas.size();
        metrosCuadrados = 0;
        montoTotal = 0;

        for (Pieza pieza : listaPiezas) {
            metrosCuadrados += pieza.getTotalM2();
            // Aquí puedes agregar cálculo de monto si tienes precios
        }

        // Actualizar TextViews
        tvCantidadEstructuras.setText(String.valueOf(cantidadEstructuras));
        tvMetrosCuadrados.setText(String.format("%.2f m²", metrosCuadrados));
        tvMontoTotal.setText(String.format("$%.2f", montoTotal));
    }

    private void generarReporte() {
        // Lógica para generar reporte
        // Puedes crear un PDF, mostrar un diálogo, etc.
    }

    private void guardarTrabajo() {
        // Lógica para guardar el trabajo en base de datos o SharedPreferences
        String cliente = etCliente.getText().toString();
        String descripcion = etDescripcion.getText().toString();

        // Guardar los datos del trabajo
    }

    // Método para agregar piezas de ejemplo (opcional)
    private void agregarPiezasEjemplo() {
        listaPiezas.add(new Pieza("Madera", "2x4", 10, 80.0f));
        listaPiezas.add(new Pieza("Metal", "1x1", 5, 5.0f));
        piezaAdapter.notifyDataSetChanged();
        actualizarTotales();
    }
}