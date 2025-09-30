package com.example.apparend;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.example.apparend.R;
import com.example.apparend.Pieza;

import java.util.List;

public class FormularioPiezaDialog {
    private static final String TAG = "arenado Form Pieza";

    public static void mostrar(Context context,List<Pieza> listaPiezas,PiezaAdapter piezaAdapter, AreaCalculator calculadora,CotasOverlay cotasOverlay,TextView txtInstruccion) {

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
       // final Button btnDIMedir = dialogView.findViewById(R.id.btnDIMedir);
        //final Button btnDIEtiquetar = dialogView.findViewById(R.id.btnDIEtiquetar);
        final Button btnCalcular = dialogView.findViewById(R.id.btnCalcular);
        final TextView lblLargo = dialogView.findViewById(R.id.lblLargo);
        final TextView textViewM = dialogView.findViewById(R.id.textViewM);
        final TextView textView2 = dialogView.findViewById(R.id.textView2);

//        spinnerUnidades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String unidad = parent.getItemAtPosition(position).toString();
//
//                switch (unidad) {
//                    case "Centímetros":
//                        textViewM.setText("(cm)");
//                        textView2.setText("(unid)"); // puedes dejarlo fijo o adaptarlo también
//                        break;
//                    case "Milímetros":
//                        textViewM.setText("(mm)");
//                        textView2.setText("(unid)");
//                        break;
//                    case "Pulgadas":
//                        textViewM.setText("( \")");
//                        textView2.setText("(unid)");
//                        break;
//                    default:
//                        textViewM.setText("(m)");
//                        textView2.setText("(unid)");
//                        break;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });








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

//        // 🔹 Listener de btnDIMedir
//        btnDIMedir.setOnClickListener(v -> {
//            iniciarMedicion(dialog, cotasOverlay, txtInstruccion);
//        });
//



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
                Log.d(TAG, "FOCO_" + nombreCampo);
            } else {
                Log.d(TAG, "PERDER_FOCO_" + nombreCampo + " → ocultar icono y reset contador");
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
                        Log.d(TAG, "1ER_TOQUE_" + nombreCampo + " → solo icono, sin teclado");
                        editText.setShowSoftInputOnFocus(false);
                    } else if (count == 2) {
                        Log.d(TAG, "2DO_TOQUE_" + nombreCampo + " → abrir teclado");
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








