package com.example.apparend.ui.Formularios;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.example.apparend.utils.CotasOverlay;
import com.example.apparend.R;
import com.example.apparend.adapters.PiezaAdapter;
import com.example.apparend.models.Pieza;

import java.util.List;

public class FormularioPiezaDialog {
    private static final String TAG = "Form Pieza arenado";

    public static void mostrar(Context context, List<Pieza> listaPiezas, PiezaAdapter piezaAdapter, AreaCalculator calculadora, CotasOverlay cotasOverlay, TextView txtInstruccion) {

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
        final TextView lblLargo = dialogView.findViewById(R.id.lblLargo);
        final TextView lblLado = dialogView.findViewById(R.id.lblLado);
        final TextView textViewM = dialogView.findViewById(R.id.textViewM);
        final TextView textView2 = dialogView.findViewById(R.id.textView2);
        final EditText etDescripcionPieza = dialogView.findViewById(R.id.etDescripcionPieza);



        // Spinner items
        final String[] tiposMaterial = {"Cuadrado", "Circular", "Plancha", "Ángulo", "Viga H ", "Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, tiposMaterial);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMaterial.setAdapter(adapter);

// 👇 Agrega aquí el listener dinámico
        spinnerMaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccionado = parent.getItemAtPosition(position).toString();

                switch (seleccionado) {
                    case "Cuadrado":
                        Log.d(TAG, "Perfil cuadrado");
                        etAncho.setVisibility(View.VISIBLE);
                        etAlto.setVisibility(View.VISIBLE);
                        textX.setVisibility(View.VISIBLE);
                        etAncho.setHint("Lado 1");
                        lblLado.setText("Lados");
                        etAlto.setHint("Lado 2");
                        lblLargo.setVisibility(View.VISIBLE);
                        etLargo.setVisibility(View.VISIBLE);
                        textViewM.setVisibility(View.VISIBLE);
                        break;

                    case "Circular":
                        Log.d(TAG, "Perfil circular");
                        etAncho.setVisibility(View.VISIBLE);
                        etAlto.setVisibility(View.GONE);
                        textX.setVisibility(View.GONE);
                        etAncho.setHint("Diámetro");
                        lblLado.setText("Diametro");
                        lblLargo.setVisibility(View.VISIBLE);
                        etLargo.setVisibility(View.VISIBLE);
                        textViewM.setVisibility(View.VISIBLE);
                        break;

                    case "Plancha":
                        Log.d(TAG, "Perfil plancha");
                        etAncho.setVisibility(View.VISIBLE);
                        etAlto.setVisibility(View.VISIBLE);
                        textX.setVisibility(View.VISIBLE);
                        etAncho.setHint("Ancho");
                        etAlto.setHint("Alto");
                        lblLado.setText("Lados");
                        lblLargo.setVisibility(View.GONE);
                        etLargo.setVisibility(View.GONE);
                        textViewM.setVisibility(View.GONE);
                        break;

                    default:
                        etAncho.setVisibility(View.VISIBLE);
                        etAlto.setVisibility(View.VISIBLE);
                        textX.setVisibility(View.VISIBLE);
                        etAncho.setHint("Lado 1");
                        etAlto.setHint("Lado 2");
                        lblLado.setText("Lados");
                        lblLargo.setVisibility(View.VISIBLE);
                        etLargo.setVisibility(View.VISIBLE);
                        textViewM.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView).setPositiveButton("Agregar", null).setNegativeButton("Cancelar", null);




        AlertDialog dialog = builder.create();
        dialog.show();

//original
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try {
                // 1️⃣ Lee los EditText
                float ancho = etAncho.getText().toString().isEmpty() ? 0 : Float.parseFloat(etAncho.getText().toString());
                float alto = etAlto.getText().toString().isEmpty() ? 0 : Float.parseFloat(etAlto.getText().toString());
                float largo = etLargo.getText().toString().isEmpty() ? 0 : Float.parseFloat(etLargo.getText().toString());
                int cantidad = etCantidad.getText().toString().isEmpty() ? 0 : Integer.parseInt(etCantidad.getText().toString());
                String descripcionPieza = etDescripcionPieza.getText().toString().trim();

                // 2️⃣ Obtiene el material
                String tipoMaterial = spinnerMaterial.getSelectedItem().toString();

                // 3️⃣ Calcula área por pieza
                float areaPorPieza = calculadora.calcularArea(tipoMaterial, ancho, alto, largo);

                // 4️⃣ Multiplica por cantidad
                float totalM2 = areaPorPieza * cantidad;

                // 5️⃣ Crea objeto Pieza
                Pieza nuevaPieza = new Pieza(tipoMaterial, ancho, alto, largo, cantidad, totalM2, descripcionPieza);

                // 6️⃣ Lo agrega a la lista
                listaPiezas.add(nuevaPieza);

                // 🔹 Log unificado según el perfil
                Log.d(TAG, "✅ Pieza agregada | Perfil: " + tipoMaterial
                        + " | Desc: " + descripcionPieza
                        + (tipoMaterial.equals("Circular") ? " | Diametro=" + ancho : " | Ancho=" + ancho + " | Alto=" + alto)
                        + (tipoMaterial.equals("Plancha") ? "" : " | Largo=" + largo)
                        + " | Cant=" + cantidad
                        + " | Total=" + String.format("%.2f m²", totalM2));

                // 7️⃣ Notifica al adaptador
                piezaAdapter.notifyDataSetChanged();

                // 8️⃣ Muestra Toast
                Toast.makeText(context, "Pieza agregada correctamente", Toast.LENGTH_SHORT).show();

                // 9️⃣ Cierra el diálogo
                dialog.dismiss();

            } catch (Exception e) {
                Log.e(TAG, "Error al procesar los datos", e);
                Toast.makeText(context, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
            }
        });




        configurarCampoMedicion(context, dialog, cotasOverlay, txtInstruccion, etAncho, "Ancho");
        configurarCampoMedicion(context, dialog, cotasOverlay, txtInstruccion, etAlto, "Alto");
        configurarCampoMedicion(context, dialog, cotasOverlay, txtInstruccion, etLargo, "Largo");


    }

    private static void configurarCampoMedicion(Context context, AlertDialog dialog,
                                                CotasOverlay cotasOverlay, TextView txtInstruccion,
                                                EditText editText, String nombreCampo) {

        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        editText.setTag(0); // contador de toques

        // --- Foco ---
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_medir, 0);
               // Log.d(TAG, "FOCO_" + nombreCampo);
            } else {
                //Log.d(TAG, "PERDER_FOCO_" + nombreCampo + " → ocultar icono y reset contador");
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                editText.setTag(0);
            }
        });


        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                final int DRAWABLE_END = 2;

                // ancho fijo para el área del icono (ej. 48dp convertido a px)
                int anchoIconoPx = (int) (24 * context.getResources().getDisplayMetrics().density);

                boolean clickEnIcono = false;
                if (event.getX() >= (editText.getWidth() - editText.getPaddingRight() - anchoIconoPx)) {
                    clickEnIcono = true;
                }

                if (clickEnIcono) {
                    Log.d(TAG, "CLICK_ICONO_" + nombreCampo);
                    iniciarMedicion(dialog, cotasOverlay, txtInstruccion);

                    cotasOverlay.setOnMedicionTerminadaListener(() -> {
                        dialog.show();
                        cotasOverlay.setDrawingEnabled(false);

                        editText.requestFocus();
                        editText.postDelayed(() -> {
                            InputMethodManager imm = (InputMethodManager)
                                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                        }, 200);
                    });
                    return true;
                } else {
                    // 👉 toque seguro en el campo
                    int count = (int) editText.getTag();
                    count++;
                    editText.setTag(count);

                    if (count == 1) {
                        //Log.d(TAG, "1ER_TOQUE_" + nombreCampo + " → solo icono, sin teclado");
                        editText.setShowSoftInputOnFocus(false);
                    } else if (count == 2) {
                        //Log.d(TAG, "2DO_TOQUE_" + nombreCampo + " → abrir teclado");
                        editText.setShowSoftInputOnFocus(true);
                        editText.postDelayed(() -> {
                            InputMethodManager imm = (InputMethodManager)
                                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                        }, 200);
                        editText.setTag(0); // reset
                    }
                }
            }
            return false;
        });

    }





    // ⚡ Interfaz para que tu Activity pase la lógica de cálculo
    public interface AreaCalculator {
        float calcularArea(String tipoMaterial, float ancho, float alto, float largo);
    }



    private static void iniciarMedicion(AlertDialog dialog, CotasOverlay cotasOverlay,TextView txtInstruccion) {
        Log.e(TAG, "iniciar medidas");

        dialog.hide();

        cotasOverlay.setVisibility(View.VISIBLE);
        cotasOverlay.setDrawingEnabled(true);
        //modo cotas (innecesario)
        cotasOverlay.setModo(0);
        cotasOverlay.resetPuntos();

        txtInstruccion.setVisibility(View.VISIBLE);
        txtInstruccion.setText("Toque la pantalla para agregar el primer punto");

        cotasOverlay.setOnMedicionTerminadaListener(() -> {
            dialog.show();
            cotasOverlay.setDrawingEnabled(false);

        });
    }



}








