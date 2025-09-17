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

public class AgregarNuevoTrabajoActivity extends AppCompatActivity implements PiezaAdapter.OnItemClickListener {

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

        // RecyclerView
        recyclerViewPiezas = findViewById(R.id.recyclerViewPiezas);
        recyclerViewPiezas.setLayoutManager(new LinearLayoutManager(this));

        piezaAdapter = new PiezaAdapter(listaPiezas, this);
        recyclerViewPiezas.setAdapter(piezaAdapter);

        // Lógica de agregar estructura
        btnAgregarEstructura.setOnClickListener(v -> {


            // Redirige a la pantalla para agregar un nuevo trabajo
            Intent intent = new Intent(AgregarNuevoTrabajoActivity.this,AgregarEstructuraActivity.class);
            startActivity(intent);



//            cantidadEstructuras++;
//            float largo = 10; // Ejemplo, valor por defecto (puedes obtener este valor de etDimensiones)
//            float ancho = 5; // Ejemplo, valor por defecto (puedes obtener este valor de etDimensiones)
//            metrosCuadrados += largo * ancho;
//            montoTotal += 500; // Ejemplo, calculas el monto por estructura
//
//            // Agregar pieza a la lista
//           listaPiezas.add(new Pieza("Tubo", largo, ancho));
//
//            // Actualizar UI
//            piezaAdapter.notifyDataSetChanged();
//            tvCantidadEstructuras.setText("Cantidad de estructuras: " + cantidadEstructuras);
//            tvMetrosCuadrados.setText("Metros Cuadrados: " + metrosCuadrados);
//            tvMontoTotal.setText("Monto Total: S/ " + montoTotal);

        });

        // Lógica para generar el reporte
        btnGenerarReporte.setOnClickListener(v -> {
            // Aquí puedes generar el reporte de las piezas
        });

        // Lógica para finalizar el trabajo
        btnFinalizar.setOnClickListener(v -> {
            // Guardar trabajo y finalizar la actividad
            finish();
        });
    }

    @Override
    public void onEditClick(Pieza pieza, int position) {
        // Acción para editar una pieza (Mostrar algún formulario de edición)
    }

    @Override
    public void onDeleteClick(Pieza pieza, int position) {
        // Eliminar pieza
        listaPiezas.remove(position);
        piezaAdapter.notifyItemRemoved(position);
    }
}
