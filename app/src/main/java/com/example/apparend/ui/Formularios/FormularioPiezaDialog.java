package com.example.apparend.ui.Formularios;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.example.apparend.ui.VisorCotasActivity;
import com.example.apparend.utils.CotasOverlay;
import com.example.apparend.R;
import com.example.apparend.adapters.PiezaAdapter;
import com.example.apparend.models.Pieza;

import java.util.List;

public class FormularioPiezaDialog {
    private static final String TAG = "Form Pieza arenado";

    // Variable estática para trackear índices de cotas por campo
    private static int indiceCotaAncho = -1;
    private static int indiceCotaAlto = -1;
    private static int indiceCotaLargo = -1;

    // Map para trackear toques por EditText
    private static java.util.Map<EditText, Integer> contadorToques = new java.util.HashMap<>();

    public static void mostrar(Context context,
                               List<Pieza> listaPiezas,
                               PiezaAdapter piezaAdapter,
                               AreaCalculator calculadora,
                               CotasOverlay cotasOverlay,
                               TextView txtInstruccion,
                               Pieza piezaExistente) {
        mostrar(context, listaPiezas, piezaAdapter, calculadora, cotasOverlay, txtInstruccion, piezaExistente, null, -1);
    }

    public static void mostrar(Context context,
                               List<Pieza> listaPiezas,
                               PiezaAdapter piezaAdapter,
                               AreaCalculator calculadora,
                               CotasOverlay cotasOverlay,
                               TextView txtInstruccion,
                               Pieza piezaExistente,
                               String campoAEnfocar) {
        mostrar(context, listaPiezas, piezaAdapter, calculadora, cotasOverlay, txtInstruccion, piezaExistente, campoAEnfocar, -1);
    }

    public static void mostrar(Context context,
                               List<Pieza> listaPiezas,
                               PiezaAdapter piezaAdapter,
                               AreaCalculator calculadora,
                               CotasOverlay cotasOverlay,
                               TextView txtInstruccion,
                               Pieza piezaExistente,
                               String campoAEnfocar,
                               int indiceCotaExistente) {

        Log.d(TAG, "========================================");
        Log.d(TAG, "mostrar() - campoAEnfocar: " + campoAEnfocar);
        Log.d(TAG, "mostrar() - indiceCotaExistente: " + indiceCotaExistente);

        // Ocultar botones al abrir el formulario
        if (context instanceof VisorCotasActivity) {
            ((VisorCotasActivity) context).ocultarBotones();
        }

        // Resetear índices al abrir formulario nuevo SOLO si no vienen de edición
        if (indiceCotaExistente < 0) {
            Log.d(TAG, "NO viene de edición -> Reseteando todos los índices a -1");
            indiceCotaAncho = -1;
            indiceCotaAlto = -1;
            indiceCotaLargo = -1;
        } else {
            Log.d(TAG, "SÍ viene de edición -> Asignando índice " + indiceCotaExistente + " al campo " + campoAEnfocar);
            // Asignar el índice al campo correspondiente
            switch (campoAEnfocar) {
                case "Ancho":
                    indiceCotaAncho = indiceCotaExistente;
                    Log.d(TAG, "indiceCotaAncho = " + indiceCotaAncho);
                    break;
                case "Alto":
                    indiceCotaAlto = indiceCotaExistente;
                    Log.d(TAG, "indiceCotaAlto = " + indiceCotaAlto);
                    break;
                case "Largo":
                    indiceCotaLargo = indiceCotaExistente;
                    Log.d(TAG, "indiceCotaLargo = " + indiceCotaLargo);
                    break;
            }
        }

        Log.d(TAG, "ESTADO INDICES ACTUALES -> Ancho:" + indiceCotaAncho + " Alto:" + indiceCotaAlto + " Largo:" + indiceCotaLargo);
        Log.d(TAG, "========================================");

        contadorToques.clear();

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_agregar_pieza, null);

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

        final String[] tiposMaterial = {"Cuadrado", "Circular", "Plancha", "Ángulo", "Viga H ", "Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, tiposMaterial);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterial.setAdapter(adapter);

        if (piezaExistente != null) {
            // Primero establecer el spinner de material
            String tipo = piezaExistente.getTipoMaterial();
            for (int i = 0; i < spinnerMaterial.getCount(); i++) {
                if (spinnerMaterial.getItemAtPosition(i).toString().equalsIgnoreCase(tipo)) {
                    spinnerMaterial.setSelection(i);
                    break;
                }
            }

            // Establecer el spinner de unidades si la pieza tiene unidad guardada
            String unidadGuardada = piezaExistente.getUnidadMedida();
            if (unidadGuardada != null && !unidadGuardada.isEmpty()) {
                for (int i = 0; i < spinnerUnidades.getCount(); i++) {
                    if (spinnerUnidades.getItemAtPosition(i).toString().equals(unidadGuardada)) {
                        spinnerUnidades.setSelection(i);
                        break;
                    }
                }
            }

            // Esperar a que el spinner se actualice antes de convertir valores
            spinnerUnidades.post(() -> {
                // Obtener unidad seleccionada (ahora debería ser la correcta)
                String unidadSeleccionada = spinnerUnidades.getSelectedItem() != null ?
                        spinnerUnidades.getSelectedItem().toString() : "Metros";

                // Convertir de metros a la unidad seleccionada para mostrar
                float anchoEnUnidad = convertirDeMetros(piezaExistente.getAncho(), unidadSeleccionada);
                float altoEnUnidad = convertirDeMetros(piezaExistente.getAlto(), unidadSeleccionada);
                float largoEnMetros = piezaExistente.getLargo(); // Largo siempre en metros

                // Formatear a máximo 2 decimales
                etAncho.setText(anchoEnUnidad > 0 ? formatearDecimales(anchoEnUnidad) : "");
                etAlto.setText(altoEnUnidad > 0 ? formatearDecimales(altoEnUnidad) : "");
                etLargo.setText(largoEnMetros > 0 ? formatearDecimales(largoEnMetros) : "");
            });

            etCantidad.setText(piezaExistente.getCantidad() > 0 ? String.valueOf(piezaExistente.getCantidad()) : "");
            etDescripcionPieza.setText(piezaExistente.getDescripcion());
        }

        spinnerMaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private String seleccionAnterior = spinnerMaterial.getSelectedItem() != null ?
                    spinnerMaterial.getSelectedItem().toString() : "Cuadrado";

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccionado = parent.getItemAtPosition(position).toString();

                // Si cambió de perfil y hay cotas dibujadas, mostrar alerta
                if (!seleccionado.equals(seleccionAnterior) && cotasOverlay.hasCotas()) {
                    new androidx.appcompat.app.AlertDialog.Builder(context)
                            .setTitle("Cambio de perfil")
                            .setMessage("Al cambiar de perfil se borrarán todas las cotas. ¿Desea continuar?")
                            .setPositiveButton("Sí", (dialogInterface, which) -> {
                                cotasOverlay.clearAllCotas();

                                // IMPORTANTE: Resetear los índices de cotas
                                indiceCotaAncho = -1;
                                indiceCotaAlto = -1;
                                indiceCotaLargo = -1;
                                Log.d(TAG, "Índices reseteados después de borrar cotas");

                                // Limpiar los campos
                                etAncho.setText("");
                                etAlto.setText("");
                                etLargo.setText("");

                                seleccionAnterior = seleccionado;
                                aplicarCambiosPerfil(seleccionado, etAncho, etAlto, textX, lblLado, lblLargo, etLargo, textViewM);
                            })
                            .setNegativeButton("No", (dialogInterface, which) -> {
                                // Revertir selección al perfil anterior
                                for (int i = 0; i < spinnerMaterial.getCount(); i++) {
                                    if (spinnerMaterial.getItemAtPosition(i).toString().equals(seleccionAnterior)) {
                                        spinnerMaterial.setSelection(i);
                                        break;
                                    }
                                }
                            })
                            .show();
                } else {
                    seleccionAnterior = seleccionado;
                    aplicarCambiosPerfil(seleccionado, etAncho, etAlto, textX, lblLado, lblLargo, etLargo, textViewM);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setPositiveButton("Agregar", null)
                .setNegativeButton("Cancelar", (dialogInterface, which) -> {
                    // Mostrar botones al cancelar
                    if (context instanceof VisorCotasActivity) {
                        ((VisorCotasActivity) context).mostrarBotones();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Enfocar campo específico si se solicitó
        if (campoAEnfocar != null) {
            dialogView.postDelayed(() -> {
                EditText campoTarget = null;
                switch (campoAEnfocar) {
                    case "Ancho":
                        campoTarget = etAncho;
                        break;
                    case "Alto":
                        campoTarget = etAlto;
                        break;
                    case "Largo":
                        campoTarget = etLargo;
                        break;
                }

                if (campoTarget != null) {
                    campoTarget.requestFocus();
                    campoTarget.selectAll();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(campoTarget, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 300);
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try {
                // Obtener unidad seleccionada
                String unidadSeleccionada = spinnerUnidades.getSelectedItem().toString();
                String simboloUnidad = obtenerSimbolo(unidadSeleccionada);

                // Leer valores ingresados y limpiar unidades
                String anchoTexto = etAncho.getText().toString().trim().replaceAll("[^0-9.]", "");
                String altoTexto = etAlto.getText().toString().trim().replaceAll("[^0-9.]", "");
                String largoTexto = etLargo.getText().toString().trim().replaceAll("[^0-9.]", "");

                float anchoIngresado = anchoTexto.isEmpty() ? 0 : Float.parseFloat(anchoTexto);
                float altoIngresado = altoTexto.isEmpty() ? 0 : Float.parseFloat(altoTexto);
                float largoIngresado = largoTexto.isEmpty() ? 0 : Float.parseFloat(largoTexto);

//                // Convertir a metros para cálculos internos
//                float ancho = convertirAMetros(anchoIngresado, unidadSeleccionada);
//                float alto = convertirAMetros(altoIngresado, unidadSeleccionada);
//                float largo = largoIngresado; // Largo siempre en metros

                // Guardar tal cual lo ingresó el usuario
                float ancho = anchoIngresado;
                float alto = altoIngresado;
                float largo = largoIngresado;


                int cantidad = etCantidad.getText().toString().isEmpty() ? 0 : Integer.parseInt(etCantidad.getText().toString());
                String descripcionPieza = etDescripcionPieza.getText().toString().trim();

                String tipoMaterial = spinnerMaterial.getSelectedItem().toString();

                Pieza piezaEnCurso = null;
                if (!listaPiezas.isEmpty()) {
                    piezaEnCurso = listaPiezas.get(listaPiezas.size() - 1);
                }
                if (piezaEnCurso == null) {
                    piezaEnCurso = new Pieza(tipoMaterial, 0, 0, 0, 1, 0, descripcionPieza);
                    listaPiezas.add(piezaEnCurso);
                }

                if (ancho > 0) piezaEnCurso.setAncho(ancho);
                if (alto > 0) piezaEnCurso.setAlto(alto);
                if (largo > 0) piezaEnCurso.setLargo(largo);
                if (cantidad > 0) piezaEnCurso.setCantidad(cantidad);
                if (!descripcionPieza.isEmpty()) piezaEnCurso.setDescripcion(descripcionPieza);

                // Guardar la unidad de medida usada
                piezaEnCurso.setUnidadMedida(unidadSeleccionada);

                float anchoM = convertirAMetros(piezaEnCurso.getAncho(), piezaEnCurso.getUnidadMedida());
                float altoM = convertirAMetros(piezaEnCurso.getAlto(), piezaEnCurso.getUnidadMedida());
                float largoM = convertirAMetros(piezaEnCurso.getLargo(), "Metros"); // largo siempre en m

                float areaPorPieza = calculadora.calcularArea(tipoMaterial, anchoM, altoM, largoM);
                piezaEnCurso.setTotalM2(areaPorPieza * piezaEnCurso.getCantidad());

                // Actualizar textos de cotas con las unidades correctas
                if (anchoIngresado > 0 && indiceCotaAncho >= 0) {
                    String valorCota = formatearDecimales(anchoIngresado) + " " + simboloUnidad; // Ancho con unidad seleccionada
                    cotasOverlay.actualizarTextoCotaPorIndice(indiceCotaAncho, valorCota);
                    logMedicion("Lado 1 (Ancho)", etAncho, ancho, valorCota);
                }

                if (altoIngresado > 0 && indiceCotaAlto >= 0) {
                    String valorCota = formatearDecimales(altoIngresado) + " " + simboloUnidad; // Alto con unidad seleccionada
                    cotasOverlay.actualizarTextoCotaPorIndice(indiceCotaAlto, valorCota);
                    logMedicion("Lado 2 (Alto)", etAlto, alto, valorCota);
                }

                if (largoIngresado > 0 && indiceCotaLargo >= 0) {
                    String valorCota = formatearDecimales(largoIngresado) + " m"; // Largo siempre en metros
                    cotasOverlay.actualizarTextoCotaPorIndice(indiceCotaLargo, valorCota);
                    logMedicion("Largo", etLargo, largo, valorCota);
                }

                piezaAdapter.notifyDataSetChanged();
                Toast.makeText(context, "Medida agregada a la pieza", Toast.LENGTH_SHORT).show();

                // Mostrar botones al cerrar el formulario
                if (context instanceof VisorCotasActivity) {
                    ((VisorCotasActivity) context).mostrarBotones();
                }

                dialog.dismiss();

            } catch (Exception e) {
                Log.e(TAG, "Error al procesar la cota", e);
                Toast.makeText(context, "Error al procesar la cota", Toast.LENGTH_SHORT).show();
            }
        });

        configurarCampoMedicion(context, dialog, cotasOverlay, txtInstruccion, etAncho, "Ancho", listaPiezas, spinnerUnidades);
        configurarCampoMedicion(context, dialog, cotasOverlay, txtInstruccion, etAlto, "Alto", listaPiezas, spinnerUnidades);
        configurarCampoMedicion(context, dialog, cotasOverlay, txtInstruccion, etLargo, "Largo", listaPiezas, spinnerUnidades);
    }

    private static void configurarCampoMedicion(Context context, AlertDialog dialog,
                                                CotasOverlay cotasOverlay, TextView txtInstruccion,
                                                EditText editText, String nombreCampo,
                                                List<Pieza> listaPiezas,
                                                Spinner spinnerUnidades) {

        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Cuando obtiene foco: mostrar icono y quitar unidad
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_medir, 0);

                // Solo quitar unidad de Ancho y Alto, no de Largo
                if (!nombreCampo.equals("Largo")) {
                    String texto = editText.getText().toString().trim();
                    if (!texto.isEmpty()) {
                        // Remover cualquier texto no numérico al final
                        String soloNumero = texto.replaceAll("[^0-9.]", "").trim();
                        if (!soloNumero.isEmpty()) {
                            editText.setText(soloNumero);
                            editText.setSelection(soloNumero.length()); // Cursor al final
                        }
                    }
                }
            } else {
                // Cuando pierde foco: ocultar icono y agregar unidad
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                // Solo agregar unidad a Ancho y Alto, NO a Largo
                if (!nombreCampo.equals("Largo")) {
                    String texto = editText.getText().toString().trim();
                    if (!texto.isEmpty()) {
                        try {
                            Float.parseFloat(texto); // Validar que sea número

                            // Obtener símbolo de unidad seleccionada
                            String unidadActual = spinnerUnidades.getSelectedItem() != null ?
                                    spinnerUnidades.getSelectedItem().toString() : "Metros";
                            String simbolo = obtenerSimbolo(unidadActual);

                            // Solo agregar si no tiene ya la unidad
                            if (!texto.endsWith(simbolo)) {
                                editText.setText(texto + " " + simbolo);
                            }
                        } catch (NumberFormatException e) {
                            // Si no es número válido, dejar como está
                        }
                    }
                }
            }
        });

        // Listener para actualizar texto en tiempo real mientras escribe
        editText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String texto = s.toString().trim();
                if (!texto.isEmpty()) {
                    try {
                        float valor = Float.parseFloat(texto);
                        int indice = -1;

                        switch (nombreCampo) {
                            case "Ancho":
                                indice = indiceCotaAncho;
                                break;
                            case "Alto":
                                indice = indiceCotaAlto;
                                break;
                            case "Largo":
                                indice = indiceCotaLargo;
                                break;
                        }

                        if (indice >= 0) {
                            // Obtener unidad y símbolo
                            String unidadSeleccionada = spinnerUnidades.getSelectedItem().toString();
                            String simboloUnidad;

                            // Largo siempre en metros, Ancho y Alto con unidad seleccionada
                            if (nombreCampo.equals("Largo")) {
                                simboloUnidad = "m";
                            } else {
                                simboloUnidad = obtenerSimbolo(unidadSeleccionada);
                            }

                            cotasOverlay.actualizarTextoCotaPorIndice(indice, formatearDecimales(valor) + " " + simboloUnidad);
                            Log.d(TAG, "Texto actualizado en tiempo real para " + nombreCampo + ": " + valor + " " + simboloUnidad);
                        }
                    } catch (NumberFormatException e) {
                        // Ignorar si no es número válido
                    }
                }
            }
        });

        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int anchoIconoPx = (int) (24 * context.getResources().getDisplayMetrics().density);

                if (event.getX() >= (editText.getWidth() - editText.getPaddingRight() - anchoIconoPx)) {
                    Log.d(TAG, "🔴🔴🔴 CLICK_ICONO_" + nombreCampo + " 🔴🔴🔴");
                    Log.d(TAG, "ESTADO INDICES -> Ancho:" + indiceCotaAncho + " Alto:" + indiceCotaAlto + " Largo:" + indiceCotaLargo);

                    // Verificar si ya existe una cota para este campo
                    int indiceExistente = -1;
                    switch (nombreCampo) {
                        case "Ancho":
                            indiceExistente = indiceCotaAncho;
                            break;
                        case "Alto":
                            indiceExistente = indiceCotaAlto;
                            break;
                        case "Largo":
                            indiceExistente = indiceCotaLargo;
                            break;
                    }

                    // Verificar si el campo ya tiene un valor ingresado
                    String valorActualCampo = editText.getText().toString().trim().replaceAll("[^0-9.]", "");
                    boolean campoTieneValor = !valorActualCampo.isEmpty();

                    Log.d(TAG, "Índice existente para " + nombreCampo + ": " + indiceExistente);
                    Log.d(TAG, "Campo tiene valor: " + campoTieneValor + " (" + valorActualCampo + ")");
                    final int indiceExistenteFinal = indiceExistente; // Variable final para lambda

                    // Si ya existe cota O el campo tiene valor, mostrar confirmación
                    if (indiceExistenteFinal >= 0 || campoTieneValor) {
                        // Ya existe una línea o un valor, mostrar diálogo de confirmación
                        String mensaje = indiceExistenteFinal >= 0
                                ? "¿Desea reemplazar la línea existente?"
                                : "El campo ya tiene un valor. ¿Desea reemplazarlo dibujando una nueva línea?";

                        Log.d(TAG, "⚠️ YA EXISTE COTA O VALOR -> Mostrando AlertDialog de confirmación");
                        new androidx.appcompat.app.AlertDialog.Builder(context)
                                .setTitle("Ya existe una medición")
                                .setMessage(mensaje)
                                .setPositiveButton("Borrar y redibujar", (dialogInterface, which) -> {
                                    // Eliminar la cota anterior si existe
                                    if (indiceExistenteFinal >= 0) {
                                        cotasOverlay.eliminarCota(indiceExistenteFinal);

                                        // CRÍTICO: Actualizar TODOS los índices que sean mayores al eliminado
                                        if (indiceCotaAncho > indiceExistenteFinal) indiceCotaAncho--;
                                        if (indiceCotaAlto > indiceExistenteFinal) indiceCotaAlto--;
                                        if (indiceCotaLargo > indiceExistenteFinal) indiceCotaLargo--;

                                        Log.d(TAG, "Índices ajustados después de eliminar índice " + indiceExistenteFinal);
                                        Log.d(TAG, "Nuevos índices -> Ancho:" + indiceCotaAncho + " Alto:" + indiceCotaAlto + " Largo:" + indiceCotaLargo);
                                    }

                                    // Resetear el índice del campo actual
                                    switch (nombreCampo) {
                                        case "Ancho":
                                            indiceCotaAncho = -1;
                                            break;
                                        case "Alto":
                                            indiceCotaAlto = -1;
                                            break;
                                        case "Largo":
                                            indiceCotaLargo = -1;
                                            break;
                                    }

                                    // Limpiar el campo
                                    editText.setText("");

                                    // Iniciar nueva medición
                                    iniciarMedicion(dialog, cotasOverlay, txtInstruccion);

                                    cotasOverlay.setOnMedicionTerminadaListener(() -> {
                                        dialog.show();
                                        cotasOverlay.setDrawingEnabled(false);

                                        // Ocultar botón Retroceder al terminar medición
                                        if (context instanceof VisorCotasActivity) {
                                            ((VisorCotasActivity) context).ocultarBotonRetroceder();
                                        }

                                        int indiceCotaDibujada = cotasOverlay.getCantidadCotas() - 1;

                                        switch (nombreCampo) {
                                            case "Ancho":
                                                indiceCotaAncho = indiceCotaDibujada;
                                                break;
                                            case "Alto":
                                                indiceCotaAlto = indiceCotaDibujada;
                                                break;
                                            case "Largo":
                                                indiceCotaLargo = indiceCotaDibujada;
                                                break;
                                        }

                                        editText.requestFocus();
                                        editText.postDelayed(() -> {
                                            InputMethodManager imm = (InputMethodManager)
                                                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                                        }, 200);
                                    });
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    } else {
                        // No existe línea previa, iniciar medición normal
                        iniciarMedicion(dialog, cotasOverlay, txtInstruccion);

                        cotasOverlay.setOnMedicionTerminadaListener(() -> {
                            dialog.show();
                            cotasOverlay.setDrawingEnabled(false);

                            int indiceCotaDibujada = cotasOverlay.getCantidadCotas() - 1;

                            switch (nombreCampo) {
                                case "Ancho":
                                    indiceCotaAncho = indiceCotaDibujada;
                                    Log.d(TAG, "Índice cota Ancho: " + indiceCotaAncho);
                                    break;
                                case "Alto":
                                    indiceCotaAlto = indiceCotaDibujada;
                                    Log.d(TAG, "Índice cota Alto: " + indiceCotaAlto);
                                    break;
                                case "Largo":
                                    indiceCotaLargo = indiceCotaDibujada;
                                    Log.d(TAG, "Índice cota Largo: " + indiceCotaLargo);
                                    break;
                            }

                            String valorActual = editText.getText().toString().trim();
                            if (!valorActual.isEmpty()) {
                                try {
                                    float valor = Float.parseFloat(valorActual);

                                    // Obtener símbolo de unidad
                                    String unidadSeleccionada = spinnerUnidades.getSelectedItem().toString();
                                    String simboloUnidad;

                                    // Largo siempre en metros, Ancho y Alto con unidad seleccionada
                                    if (nombreCampo.equals("Largo")) {
                                        simboloUnidad = "m";
                                    } else {
                                        simboloUnidad = obtenerSimbolo(unidadSeleccionada);
                                    }

                                    cotasOverlay.actualizarTextoCotaPorIndice(indiceCotaDibujada, formatearDecimales(valor) + " " + simboloUnidad);
                                    Log.d(TAG, "Valor actualizado inmediatamente en cota " + indiceCotaDibujada + ": " + valor + " " + simboloUnidad);
                                } catch (NumberFormatException e) {
                                    Log.w(TAG, "Valor no numérico, mantiene placeholder");
                                }
                            }

                            editText.requestFocus();
                            editText.postDelayed(() -> {
                                InputMethodManager imm = (InputMethodManager)
                                        context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                            }, 200);
                        });
                    }
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * Convierte un valor de la unidad seleccionada a metros
     */
    private static float convertirAMetros(float valor, String unidad) {
        switch (unidad) {
            case "Pulgadas":
                return valor * 0.0254f;
            case "Centimetros":
                return valor * 0.01f;
            case "Milimetros":
                return valor * 0.001f;
            case "Metros":
            default:
                return valor;
        }
    }

    /**
     * Convierte un valor de metros a la unidad seleccionada
     */
    private static float convertirDeMetros(float valorEnMetros, String unidad) {
        switch (unidad) {
            case "Pulgadas":
                return valorEnMetros / 0.0254f;
            case "Centimetros":
                return valorEnMetros / 0.01f;
            case "Milimetros":
                return valorEnMetros / 0.001f;
            case "Metros":
            default:
                return valorEnMetros;
        }
    }

    /**
     * Aplica los cambios visuales según el perfil seleccionado
     */
    private static void aplicarCambiosPerfil(String seleccionado, EditText etAncho, EditText etAlto,
                                             TextView textX, TextView lblLado, TextView lblLargo,
                                             EditText etLargo, TextView textViewM) {
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

    /**
     * Formatea un número para mostrar máximo 2 decimales, eliminando ceros innecesarios
     */
    private static String formatearDecimales(float valor) {
        // Redondear a 2 decimales
        valor = Math.round(valor * 100f) / 100f;

        // Si es un número entero, mostrar sin decimales
        if (valor == (int) valor) {
            return String.valueOf((int) valor);
        }

        // Mostrar con máximo 2 decimales, eliminando ceros finales
        return String.format("%.2f", valor).replaceAll("0*$", "").replaceAll("\\.$", "");
    }

    /**
     * Obtiene el símbolo de la unidad
     */
    private static String obtenerSimbolo(String unidad) {
        switch (unidad) {
            case "Pulgadas":
                return "\"";
            case "Centimetros":
                return "cm";
            case "Milimetros":
                return "mm";
            case "Metros":
                return "m";
            default:
                return "m";
        }
    }

    public interface AreaCalculator {
        float calcularArea(String tipoMaterial, float ancho, float alto, float largo);
    }

    private static void iniciarMedicion(AlertDialog dialog, CotasOverlay cotasOverlay, TextView txtInstruccion) {
        Log.e(TAG, "iniciar medidas");
        dialog.hide();
        cotasOverlay.setVisibility(View.VISIBLE);
        cotasOverlay.setDrawingEnabled(true);
        cotasOverlay.setModo(0);
        cotasOverlay.resetPuntos(); // Solo limpia puntos temporales, NO las cotas guardadas

        txtInstruccion.setVisibility(View.VISIBLE);
        txtInstruccion.setText("Toque la pantalla para agregar el primer punto");

        // Mostrar botón Retroceder y configurarlo para cancelar dibujo
        if (dialog.getContext() instanceof VisorCotasActivity) {
            VisorCotasActivity activity = (VisorCotasActivity) dialog.getContext();
            activity.mostrarBotonRetroceder();
            activity.configurarBotonRetrocederParaCancelar(() -> {
                // Cancelar dibujo actual
                cotasOverlay.resetPuntos();
                cotasOverlay.setDrawingEnabled(false);
                txtInstruccion.setVisibility(View.GONE);
                activity.ocultarBotonRetroceder();
                dialog.show(); // Volver a mostrar el formulario
            });
        }
    }

    private static void logMedicion(String nombreCampo, EditText editText, float valor, String valorCota) {
        Log.d(TAG, "Campo del formulario: " + nombreCampo);
        Log.d(TAG, "Texto en EditText: " + editText.getText().toString());
        Log.d(TAG, "Valor numérico ingresado: " + valor);
        Log.d(TAG, "Texto mostrado en la imagen: " + valorCota);
    }
}