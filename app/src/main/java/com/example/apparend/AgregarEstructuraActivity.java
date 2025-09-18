package com.example.apparend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgregarEstructuraActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final String TAG = "arenado";

    private Button btnAgregarImagen, btnFinalizarEstructura, btnAgregarPieza;
    private ImageView imgPreview;
    private EditText etDescripcion;
    private RecyclerView recyclerViewItems;

    private Uri photoUri;
    private File photoFile;

    // Lista para almacenar las piezas agregadas
    private List<Pieza> listaPiezas = new ArrayList<>();
    private PiezaAdapter piezaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_estructura);
        Log.d(TAG, "AgregarEstructura iniciada");

        // Inicializar vistas
        btnAgregarImagen = findViewById(R.id.btnAgregarImagen);
        imgPreview = findViewById(R.id.imgPreview);
        etDescripcion = findViewById(R.id.etDescripcion);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        btnFinalizarEstructura = findViewById(R.id.btnFinalizarEstructura);
        btnAgregarPieza = findViewById(R.id.btnAgregarPieza);

        // Configurar RecyclerView
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        piezaAdapter = new PiezaAdapter(listaPiezas, this, new PiezaAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Pieza pieza, int position) {
                // Si no necesitas editar aquí, déjalo vacío
            }

            @Override
            public void onDeleteClick(Pieza pieza, int position) {
                // Si no necesitas eliminar aquí, déjalo vacío
            }
        });
        recyclerViewItems.setAdapter(piezaAdapter);

        // Configurar botones
        btnAgregarPieza.setOnClickListener(v -> {
            Log.d(TAG, "btn agregar pieza");
            mostrarFormularioAgregarPieza();
        });

        btnAgregarImagen.setOnClickListener(v -> {
            Log.d(TAG, "Agregar imagen: ");
            if (photoUri == null) {
                mostrarOpcionesImagen();
            } else {
                borrarImagen();
            }
        });

        btnFinalizarEstructura.setOnClickListener(v -> guardarEstructura());
    }

    // Método para mostrar el formulario emergente
    private void mOostrarFormularioAgregarPieza() {

        Log.d(TAG, "inflando");
        // Inflar el layout personalizado
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_pieza, null);

        // Obtener referencias a las vistas
        final Spinner spinnerMaterial = dialogView.findViewById(R.id.spinnerMaterial);
        final Spinner spinnerUnidades = dialogView.findViewById(R.id.spinnerUnidades);
        final EditText etAncho = dialogView.findViewById(R.id.etAncho);
        final TextView textX = dialogView.findViewById(R.id.textX);
        final EditText etAlto = dialogView.findViewById(R.id.etAlto);
        final EditText etLargo = dialogView.findViewById(R.id.etLargo);
        final EditText etCantidad = dialogView.findViewById(R.id.etCantidad);
        final TextView tvTotalM2 = dialogView.findViewById(R.id.tvTotalM2);
        final Button btnCalcular = dialogView.findViewById(R.id.btnCalcular);
        final TextView lblLargo = dialogView.findViewById(R.id.lblLargo);
        final TextView textViewM = dialogView.findViewById(R.id.textViewM);



        // Configurar spinner
        final String[] tiposMaterial = {"Cuadrado", "Circular", "Plancha", "Ángulo" ,"Viga H " ,"Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposMaterial);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterial.setAdapter(adapter);

        // Configurar botón de calcular
        btnCalcular.setOnClickListener(v -> {
            Log.d(TAG, "btn calculando");
            try {
                String material = spinnerMaterial.getSelectedItem().toString();
                float ancho = 0, alto = 0, largo = 0;
                int cantidad = Integer.parseInt(etCantidad.getText().toString());

                // Capturar valores según el material
                switch (material) {
                    case "Cuadrado":
                    case "Ángulo":
                        ancho = Float.parseFloat(etAncho.getText().toString());
                        alto = Float.parseFloat(etAlto.getText().toString());
                        largo = Float.parseFloat(etLargo.getText().toString());
                        break;

                    case "Circular":
                        ancho = Float.parseFloat(etAncho.getText().toString()); // radio
                        largo = Float.parseFloat(etLargo.getText().toString());
                        break;

                    case "Plancha":
                        ancho = Float.parseFloat(etAncho.getText().toString());
                        alto = Float.parseFloat(etAlto.getText().toString());
                        break;

                    default:
                        ancho = Float.parseFloat(etAncho.getText().toString());
                        alto = Float.parseFloat(etAlto.getText().toString());
                        largo = Float.parseFloat(etLargo.getText().toString());
                        break;
                }

                // Calcular área
                float areaPorPieza = calcularArea(material, ancho, alto, largo);
                float totalM2 = areaPorPieza * cantidad;

                tvTotalM2.setText(String.format("Total m²: %.3f", totalM2));

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show();
            }
        });


        // Crear y mostrar el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    try {
                        String tipoMaterial = spinnerMaterial.getSelectedItem().toString();
                        float ancho = Float.parseFloat(etAncho.getText().toString());
                        float alto = Float.parseFloat(etAlto.getText().toString());
                        float largo = Float.parseFloat(etLargo.getText().toString());
                        int cantidad = Integer.parseInt(etCantidad.getText().toString());

                        // Formatear dimensiones para mostrar
                        String dimensiones = String.format("%.0f\" x %.0f\" x %.0fm", ancho, alto, largo);

                        // Calcular área total
                        float areaPorPieza = calcularArea(tipoMaterial, ancho, alto, largo);
                        float totalM2 = areaPorPieza * cantidad;

                        // Agregar a la lista
                        listaPiezas.add(new Pieza(tipoMaterial, dimensiones, cantidad, totalM2));
                        piezaAdapter.notifyDataSetChanged();

                        Toast.makeText(this, "Pieza agregada correctamente", Toast.LENGTH_SHORT).show();

                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null);

        // Crear y mostrar el AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();



        // Modificar el tamaño del AlertDialog
//        Log.d(TAG, "modificando tamaño");
//        dialog.getWindow().setLayout(
//
//                (int) (getResources().getDisplayMetrics().widthPixels * 0.99),  // 90% del ancho de la pantalla
//                (int) (getResources().getDisplayMetrics().heightPixels * 0.95) // 80% de la altura de la pantalla
//        );







        spinnerMaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccionado = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getApplicationContext(), "Seleccionado: " + seleccionado, Toast.LENGTH_SHORT).show();
                 Log.d(TAG, "Seleccionado: " + seleccionado);


                switch (seleccionado) {
                    case "Cuadrado":
                        etAncho.setVisibility(View.VISIBLE);
                        etAlto.setVisibility(View.VISIBLE);
                        etAncho.setHint("Lado 1");
                        etAlto.setHint("Lado 2");
                        break;

                    case "Circular":
                        etAncho.setVisibility(View.VISIBLE);
                        etAlto.setVisibility(View.GONE);
                        etAncho.setHint("Radio");
                        textX.setVisibility(View.GONE);
                        break;

                    case "Plancha":
                        etAncho.setVisibility(View.VISIBLE);
                        etAlto.setVisibility(View.VISIBLE);
                        etAncho.setHint("Ancho");
                        textX.setVisibility(View.VISIBLE);
                        etAlto.setHint("Largo");
                        lblLargo.setVisibility(View.GONE);
                        etLargo.setVisibility(View.GONE);
                        textViewM.setVisibility(View.GONE);


                        break;

                    default:
                        etAncho.setVisibility(View.VISIBLE);
                        etAlto.setVisibility(View.VISIBLE);
                        etAncho.setHint("Lado 1");
                        etAlto.setHint("Lado 2");
                        break;
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Aquí no haces nada normalmente
            }
        });



        spinnerUnidades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccionado = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getApplicationContext(), "Seleccionado: " + seleccionado, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Seleccionado: " + seleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Aquí no haces nada normalmente
            }
        });





    }

    private void mostrarFormularioAgregarPieza() {
        Log.d(TAG, "mostrarFormularioAgregarPieza: inicio - inflando layout");
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_pieza, null);

        // Referencias
        final Spinner spinnerMaterial = dialogView.findViewById(R.id.spinnerMaterial);
        final Spinner spinnerUnidades = dialogView.findViewById(R.id.spinnerUnidades);
        final EditText etAncho = dialogView.findViewById(R.id.etAncho);
        final TextView textX = dialogView.findViewById(R.id.textX);
        final EditText etAlto = dialogView.findViewById(R.id.etAlto);
        final EditText etLargo = dialogView.findViewById(R.id.etLargo);
        final EditText etCantidad = dialogView.findViewById(R.id.etCantidad);
        final TextView tvTotalM2 = dialogView.findViewById(R.id.tvTotalM2);
        final Button btnCalcular = dialogView.findViewById(R.id.btnCalcular);
        final TextView lblLargo = dialogView.findViewById(R.id.lblLargo);
        final TextView textViewM = dialogView.findViewById(R.id.textViewM);

        Log.d(TAG, "views inicializadas -> spinnerMaterial=" + (spinnerMaterial != null)
                + " etAncho=" + (etAncho != null)
                + " etAlto=" + (etAlto != null)
                + " etLargo=" + (etLargo != null)
                + " etCantidad=" + (etCantidad != null));

        // Spinner items
        final String[] tiposMaterial = {"Cuadrado", "Circular", "Plancha", "Ángulo", "Viga H ", "Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposMaterial);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterial.setAdapter(adapter);
        Log.d(TAG, "spinnerMaterial adapter seteado. Primer item: " + tiposMaterial[0]);

        // Listener del spinner -> mostrar/ocultar campos
        spinnerMaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccionado = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "spinnerMaterial.onItemSelected -> position=" + position + " seleccionado=" + seleccionado);

                switch (seleccionado) {
                    case "Cuadrado":
                        Log.d(TAG, "Config: Cuadrado -> mostrar ancho, alto y largo");
                        etAncho.setVisibility(View.VISIBLE);
                        etAlto.setVisibility(View.VISIBLE);
                        textX.setVisibility(View.VISIBLE);
                        etAncho.setHint("Lado 1");
                        etAlto.setHint("Lado 2");
                        lblLargo.setVisibility(View.VISIBLE);
                        etLargo.setVisibility(View.VISIBLE);
                        textViewM.setVisibility(View.VISIBLE);
                        break;

                    case "Circular":
                        Log.d(TAG, "Config: Circular -> usar radio (etAncho) y largo");
                        etAncho.setVisibility(View.VISIBLE);
                        etAlto.setVisibility(View.GONE);
                        textX.setVisibility(View.GONE);
                        etAncho.setHint("Radio");
                        lblLargo.setVisibility(View.VISIBLE);
                        etLargo.setVisibility(View.VISIBLE);
                        textViewM.setVisibility(View.VISIBLE);
                        break;

                    case "Plancha":
                        Log.d(TAG, "Config: Plancha -> ancho y alto, ocultar largo");
                        etAncho.setVisibility(View.VISIBLE);
                        etAlto.setVisibility(View.VISIBLE);
                        textX.setVisibility(View.VISIBLE);
                        etAncho.setHint("Ancho");
                        etAlto.setHint("Alto");
                        lblLargo.setVisibility(View.GONE);
                        etLargo.setVisibility(View.GONE);
                        textViewM.setVisibility(View.GONE);
                        break;

                    default:
                        Log.d(TAG, "Config: default -> mostrar todo por defecto");
                        etAncho.setVisibility(View.VISIBLE);
                        etAlto.setVisibility(View.VISIBLE);
                        textX.setVisibility(View.VISIBLE);
                        etAncho.setHint("Lado 1");
                        etAlto.setHint("Lado 2");
                        lblLargo.setVisibility(View.VISIBLE);
                        etLargo.setVisibility(View.VISIBLE);
                        textViewM.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "spinnerMaterial.onNothingSelected");
            }
        });

        // Listener spinnerUnidades (solo para ver qué pasa)
        spinnerUnidades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String u = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "spinnerUnidades.onItemSelected -> position=" + position + " unidades=" + u);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "spinnerUnidades.onNothingSelected");
            }
        });

        // Botón Calcular con logs detallados
        btnCalcular.setOnClickListener(v -> {
            Log.d(TAG, "btnCalcular clicked");
            String material = spinnerMaterial.getSelectedItem().toString();
            Log.d(TAG, "Material seleccionado al calcular: " + material);

            String sAncho = etAncho.getText().toString().trim();
            String sAlto = etAlto.getText().toString().trim();
            String sLargo = etLargo.getText().toString().trim();
            String sCantidad = etCantidad.getText().toString().trim();

            Log.d(TAG, "Inputs raw -> ancho:'" + sAncho + "', alto:'" + sAlto + "', largo:'" + sLargo + "', cantidad:'" + sCantidad + "'");

            try {
                float ancho = 0f, alto = 0f, largo = 0f;
                int cantidad = 0;

                // Validaciones según material
                if (material.equals("Circular")) {
                    if (sAncho.isEmpty() || sLargo.isEmpty() || sCantidad.isEmpty()) {
                        Log.w(TAG, "Faltan campos para Circular. ancho/radio o largo o cantidad vacíos.");
                        Toast.makeText(AgregarEstructuraActivity.this, "Complete radio, largo y cantidad.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ancho = Float.parseFloat(sAncho); // radio
                    largo = Float.parseFloat(sLargo);
                    cantidad = Integer.parseInt(sCantidad);
                } else if (material.equals("Plancha")) {
                    if (sAncho.isEmpty() || sAlto.isEmpty() || sCantidad.isEmpty()) {
                        Log.w(TAG, "Faltan campos para Plancha. ancho/alto o cantidad vacíos.");
                        Toast.makeText(AgregarEstructuraActivity.this, "Complete ancho, alto y cantidad.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ancho = Float.parseFloat(sAncho);
                    alto = Float.parseFloat(sAlto);
                    cantidad = Integer.parseInt(sCantidad);
                } else { // Cuadrado, Ángulo, Viga H, Otro -> intentamos leer ancho, alto, largo, cantidad
                    if (sAncho.isEmpty() || sAlto.isEmpty() || sLargo.isEmpty() || sCantidad.isEmpty()) {
                        Log.w(TAG, "Faltan campos para " + material + ". Verifique todos los campos.");
                        Toast.makeText(AgregarEstructuraActivity.this, "Complete los campos necesarios.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ancho = Float.parseFloat(sAncho);
                    alto = Float.parseFloat(sAlto);
                    largo = Float.parseFloat(sLargo);
                    cantidad = Integer.parseInt(sCantidad);
                }

                Log.d(TAG, String.format("Parsed values -> ancho=%.3f, alto=%.3f, largo=%.3f, cantidad=%d", ancho, alto, largo, cantidad));

                // AVISO IMPORTANTE: el método calcularArea() en tu clase usa cadenas como "Tubo Cuadrado" / "Tubo Circular".
                // Si tus spinner items son "Cuadrado" / "Circular" -> calcularArea podría devolver 0.
                // Aquí logueamos esa posibilidad:
                Log.d(TAG, "Llamando calcularArea con material='" + material + "'");
                float areaPorPieza = calcularArea(material, ancho, alto, largo);
                Log.d(TAG, "calcularArea -> areaPorPieza = " + areaPorPieza);

                if (areaPorPieza == 0f) {
                    Log.w(TAG, "Area por pieza es 0. Revisa nombres esperados en calcularArea() o la fórmula.");
                }

                float totalM2 = areaPorPieza * cantidad;
                Log.d(TAG, "totalM2 calculado = " + totalM2);

                tvTotalM2.setText(String.format("Total m²: %.3f", totalM2));
                Log.d(TAG, "tvTotalM2 actualizado en UI");

            } catch (NumberFormatException e) {
                Log.e(TAG, "NumberFormatException al parsear campos (ancho/alto/largo/cantidad). Valores: ancho='" + sAncho + "' alto='" + sAlto + "' largo='" + sLargo + "' cantidad='" + sCantidad + "'", e);
                Toast.makeText(AgregarEstructuraActivity.this, "Por favor complete los campos correctamente (números).", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error inesperado en btnCalcular", e);
                Toast.makeText(AgregarEstructuraActivity.this, "Error al calcular. Revisa Logcat.", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Agregar (cuando presionan + en el diálogo)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    Log.d(TAG, "PositiveButton Agregar pulsado");
                    try {
                        String tipoMaterial = spinnerMaterial.getSelectedItem().toString();
                        String sAncho = etAncho.getText().toString().trim();
                        String sAlto = etAlto.getText().toString().trim();
                        String sLargo = etLargo.getText().toString().trim();
                        String sCantidad = etCantidad.getText().toString().trim();

                        Log.d(TAG, "Valores al agregar -> tipo=" + tipoMaterial + " ancho='" + sAncho + "' alto='" + sAlto + "' largo='" + sLargo + "' cantidad='" + sCantidad + "'");

                        float ancho = sAncho.isEmpty() ? 0f : Float.parseFloat(sAncho);
                        float alto = sAlto.isEmpty() ? 0f : Float.parseFloat(sAlto);
                        float largo = sLargo.isEmpty() ? 0f : Float.parseFloat(sLargo);
                        int cantidad = sCantidad.isEmpty() ? 0 : Integer.parseInt(sCantidad);

                        String dimensiones = String.format("%.0f\" x %.0f\" x %.0fm", ancho, alto, largo);
                        float areaPorPieza = calcularArea(tipoMaterial, ancho, alto, largo);
                        float totalM2 = areaPorPieza * cantidad;

                        listaPiezas.add(new Pieza(tipoMaterial, dimensiones, cantidad, totalM2));
                        piezaAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Pieza agregada -> tipo=" + tipoMaterial + " dimensiones=" + dimensiones + " cantidad=" + cantidad + " totalM2=" + totalM2 + " listaSize=" + listaPiezas.size());

                        Toast.makeText(AgregarEstructuraActivity.this, "Pieza agregada correctamente", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing al presionar Agregar", e);
                        Toast.makeText(AgregarEstructuraActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error inesperado al agregar pieza", e);
                        Toast.makeText(AgregarEstructuraActivity.this, "Error al agregar pieza. Ver Logcat.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
        Log.d(TAG, "Dialog mostrado (mostrarFormularioAgregarPieza) - FIN");
    }


    private float cCalcularArea(String tipoMaterial, float ancho, float alto, float largo) {
        // Conversión de pulgadas a metros (1 pulgada = 0.0254 metros)
        float anchoM = ancho * 0.0254f;
        float altoM = alto * 0.0254f;
        float largoM = largo;

        switch (tipoMaterial) {
            case "Tubo Cuadrado":
                // Perímetro * largo (4 lados)
                return 4 * anchoM * largoM;
            case "Tubo Circular":
                // Circunferencia * largo (π * diámetro * largo)
                return (float) (Math.PI * anchoM * largoM);
            case "Plancha":
                // Área de ambas caras (ancho * alto * 2)
                return anchoM * altoM * 2;
            case "Ángulo":
                // Perímetro aproximado * largo (suma de los 3 lados visibles)
                return (anchoM + altoM + Math.max(anchoM, altoM)) * largoM;
            case "Barra Liza Cuadrada":
                // Perímetro * largo (4 lados)
                return 4 * anchoM * largoM;
            case "Barra Liza Redonda":
                // Circunferencia * largo
                return (float) (Math.PI * anchoM * largoM);
            default:
                return 0;
        }
    }

    private float calcularArea(String tipoMaterial, float ancho, float alto, float largo) {
        Log.d(TAG, "calcularArea() llamado con -> material=" + tipoMaterial + ", ancho=" + ancho + ", alto=" + alto + ", largo=" + largo);

        float anchoM = ancho * 0.0254f;
        float altoM = alto * 0.0254f;
        float largoM = largo;

        switch (tipoMaterial) {
            case "Cuadrado":
            case "Tubo Cuadrado":
                Log.d(TAG, "Usando fórmula Cuadrado/Tubo Cuadrado");
                return 4 * anchoM * largoM;

            case "Circular":
            case "Tubo Circular":
                Log.d(TAG, "Usando fórmula Circular/Tubo Circular");
                return (float) (Math.PI * anchoM * largoM);

            case "Plancha":
                Log.d(TAG, "Usando fórmula Plancha");
                return anchoM * altoM * 2;

            case "Ángulo":
                Log.d(TAG, "Usando fórmula Ángulo");
                return (anchoM + altoM + Math.max(anchoM, altoM)) * largoM;

            case "Barra Liza Cuadrada":
                Log.d(TAG, "Usando fórmula Barra Liza Cuadrada");
                return 4 * anchoM * largoM;

            case "Barra Liza Redonda":
                Log.d(TAG, "Usando fórmula Barra Liza Redonda");
                return (float) (Math.PI * anchoM * largoM);

            default:
                Log.w(TAG, "Material no reconocido: " + tipoMaterial);
                return 0;
        }
    }




    private void mostrarOpcionesImagen() {
        Log.d(TAG, "Mostrar opciones");
        CharSequence[] opciones = {"Cámara", "Galería", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar imagen desde:");
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) {
                abrirCamara();
            } else if (which == 1) {
                seleccionarDeGaleria();
            }
        });
        builder.show();
    }

    private void abrirCamara() {
        Log.d(TAG, "Abrir camara");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = crearArchivoImagen();
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this,
                            getApplicationContext().getPackageName() + ".fileprovider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    //startActivityForResult(intent, REQUEST_CAMERA);
                    cameraLauncher.launch(intent);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error creando archivo", e);
                Toast.makeText(this, "No se pudo crear archivo", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se encontró la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private File crearArchivoImagen() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreArchivo = "IMG_" + timeStamp + "_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(nombreArchivo, ".jpg", directorio);
    }

    private void seleccionarDeGaleria() {
        Log.d(TAG, "abrir galeria");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                if (photoUri != null) {
                    imgPreview.setVisibility(View.VISIBLE);
                    imgPreview.setImageURI(photoUri);
                    btnAgregarImagen.setText("🗑️ Borrar Imagen");
                    Toast.makeText(this, "Foto tomada con éxito", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_GALLERY) {
                if (data != null && data.getData() != null) {
                    photoUri = data.getData();
                    imgPreview.setVisibility(View.VISIBLE);
                    imgPreview.setImageURI(photoUri);
                    btnAgregarImagen.setText("🗑️ Borrar Imagen");
                }
            }
        }
    }

    private void borrarImagen() {
        imgPreview.setVisibility(View.GONE);
        imgPreview.setImageURI(null);
        btnAgregarImagen.setText("➕ Agregar Imagen");

        // Borrar archivo de foto si existe
        if (photoFile != null && photoFile.exists()) {
            boolean deleted = photoFile.delete();
            Log.d(TAG, "Archivo borrado: " + deleted);
        }

        photoUri = null;
        photoFile = null;
    }

    private void guardarEstructura() {
        String descripcion = etDescripcion.getText().toString().trim();

        if (descripcion.isEmpty()) {
            Toast.makeText(this, "Ingresa una descripción", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listaPiezas.isEmpty()) {
            Toast.makeText(this, "Debes agregar al menos una pieza", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calcular total de metros cuadrados
        float totalM2Estructura = 0;
        for (Pieza pieza : listaPiezas) {
            totalM2Estructura += pieza.getTotalM2();
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("descripcion", descripcion);
        resultIntent.putExtra("totalM2", totalM2Estructura);
        resultIntent.putExtra("cantidadPiezas", listaPiezas.size());

        if (photoUri != null) {
            resultIntent.putExtra("imagenUri", photoUri.toString());
        }

        setResult(RESULT_OK, resultIntent);
        finish();
    }


    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && photoUri != null) {
                    imgPreview.setVisibility(View.VISIBLE);
                    imgPreview.setImageURI(photoUri);
                    btnAgregarImagen.setText("🗑️ Borrar Imagen");
                    Toast.makeText(this, "Foto tomada con éxito", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "FOTO");
                }
            });
























}