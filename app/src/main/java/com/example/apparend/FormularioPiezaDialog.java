package com.example.apparend;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.example.apparend.R;
import com.example.apparend.Pieza;

import java.util.List;

public class FormularioPiezaDialog {
    private static final String TAG = "arenado Form Pieza";

    public static void mostrar(Context context,
                               List<Pieza> listaPiezas,
                               PiezaAdapter piezaAdapter,
                               AreaCalculator calculadora,
                               CotasOverlay cotasOverlay,
                               TextView txtInstruccion) {

        Log.d(TAG, "inflando formulario");
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_agregar_pieza, null);

        // Referencias
        final Spinner spinnerMaterial = dialogView.findViewById(R.id.spinnerMaterial);
        final Spinner spinnerUnidades = dialogView.findViewById(R.id.spinnerUnidades);
        final EditText etAncho = dialogView.findViewById(R.id.etAncho);
        final TextView textX = dialogView.findViewById(R.id.textX);
        final EditText etAlto = dialogView.findViewById(R.id.etAlto);
        final EditText etLargo = dialogView.findViewById(R.id.etLargo);
        final EditText etCantidad = dialogView.findViewById(R.id.etCantidad);
        final TextView tvTotalM2 = dialogView.findViewById(R.id.tvTotalM2);
        final Button btnDIMedir = dialogView.findViewById(R.id.btnDIMedir);
        final Button btnDIEtiquetar = dialogView.findViewById(R.id.btnDIEtiquetar);
        final Button btnCalcular = dialogView.findViewById(R.id.btnCalcular);
        final TextView lblLargo = dialogView.findViewById(R.id.lblLargo);
        final TextView textViewM = dialogView.findViewById(R.id.textViewM);

        // Spinner items
        final String[] tiposMaterial = {"Cuadrado", "Circular", "Plancha", "Ángulo", "Viga H ", "Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, tiposMaterial);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterial.setAdapter(adapter);

        // Botón Calcular
        btnCalcular.setOnClickListener(v -> {
            String material = spinnerMaterial.getSelectedItem().toString();
            String sAncho = etAncho.getText().toString().trim();
            String sAlto = etAlto.getText().toString().trim();
            String sLargo = etLargo.getText().toString().trim();
            String sCantidad = etCantidad.getText().toString().trim();

            try {
                float ancho = sAncho.isEmpty() ? 0 : Float.parseFloat(sAncho);
                float alto = sAlto.isEmpty() ? 0 : Float.parseFloat(sAlto);
                float largo = sLargo.isEmpty() ? 0 : Float.parseFloat(sLargo);
                int cantidad = sCantidad.isEmpty() ? 0 : Integer.parseInt(sCantidad);

                float areaPorPieza = calculadora.calcularArea(material, ancho, alto, largo);
                float totalM2 = areaPorPieza * cantidad;

                tvTotalM2.setText(String.format("Total m²: %.3f", totalM2));
            } catch (Exception e) {
                Toast.makeText(context, "Error en los datos", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Agregar
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setPositiveButton("Agregar", null)
                .setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // 🔹 Listener de btnDIMedir
        btnDIMedir.setOnClickListener(v -> {
            Log.e(TAG, "btn dimedir");

            // Ocultar el formulario temporalmente
            dialog.hide();

            // Activar el overlay en modo medidas
            cotasOverlay.setVisibility(View.VISIBLE);
            cotasOverlay.setDrawingEnabled(true);
            cotasOverlay.setModo(0);
            cotasOverlay.resetPuntos();

            txtInstruccion.setVisibility(View.VISIBLE);
            txtInstruccion.setText("Toque la pantalla para agregar el primer punto");

            // 👇 Aquí escuchamos cuando se agregue el segundo punto
            cotasOverlay.setOnMedicionTerminadaListener(() -> {
                // Cuando termina la medición, volvemos a mostrar el formulario
                dialog.show();
            });
        });

        // Sobrescribir click en el botón Agregar
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try {
                String tipoMaterial = spinnerMaterial.getSelectedItem().toString();
                float ancho = etAncho.getText().toString().isEmpty() ? 0 : Float.parseFloat(etAncho.getText().toString());
                float alto = etAlto.getText().toString().isEmpty() ? 0 : Float.parseFloat(etAlto.getText().toString());
                float largo = etLargo.getText().toString().isEmpty() ? 0 : Float.parseFloat(etLargo.getText().toString());
                int cantidad = etCantidad.getText().toString().isEmpty() ? 0 : Integer.parseInt(etCantidad.getText().toString());

                String dimensiones = String.format("%.0f\" x %.0f\" x %.0fm", ancho, alto, largo);
                float areaPorPieza = calculadora.calcularArea(tipoMaterial, ancho, alto, largo);
                float totalM2 = areaPorPieza * cantidad;

                listaPiezas.add(new Pieza(tipoMaterial, dimensiones, cantidad, totalM2));
                piezaAdapter.notifyDataSetChanged();

                Toast.makeText(context, "Pieza agregada correctamente", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(context, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ⚡ Interfaz para que tu Activity pase la lógica de cálculo
    public interface AreaCalculator {
        float calcularArea(String tipoMaterial, float ancho, float alto, float largo);
    }
}



