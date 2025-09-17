package com.example.apparend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
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
        Log.d(TAG, "onCreate: Activity iniciada");

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
        btnAgregarPieza.setOnClickListener(v -> mostrarFormularioAgregarPieza());

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
    private void mostrarFormularioAgregarPieza() {
        // Inflar el layout personalizado
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_pieza, null);

        // Obtener referencias a las vistas
        final Spinner spinnerMaterial = dialogView.findViewById(R.id.spinnerMaterial);
        final EditText etAncho = dialogView.findViewById(R.id.etAncho);
        final EditText etAlto = dialogView.findViewById(R.id.etAlto);
        final EditText etLargo = dialogView.findViewById(R.id.etLargo);
        final EditText etCantidad = dialogView.findViewById(R.id.etCantidad);
        final TextView tvTotalM2 = dialogView.findViewById(R.id.tvTotalM2);
        final Button btnCalcular = dialogView.findViewById(R.id.btnCalcular);

        // Configurar spinner
        final String[] tiposMaterial = {"Tubo Cuadrado", "Tubo Circular", "Plancha", "Ángulo", "Barra Liza Cuadrada", "Barra Liza Redonda"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposMaterial);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterial.setAdapter(adapter);

        // Configurar botón de calcular
        btnCalcular.setOnClickListener(v -> {
            try {
                float ancho = Float.parseFloat(etAncho.getText().toString());
                float alto = Float.parseFloat(etAlto.getText().toString());
                float largo = Float.parseFloat(etLargo.getText().toString());
                int cantidad = Integer.parseInt(etCantidad.getText().toString());

                // Calcular área según el tipo de material
                String material = spinnerMaterial.getSelectedItem().toString();
                float areaPorPieza = calcularArea(material, ancho, alto, largo);
                float totalM2 = areaPorPieza * cantidad;

                tvTotalM2.setText(String.format("Total m²: %.3f", totalM2));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show();
            }
        });

        // Crear y mostrar el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Pieza")
                .setView(dialogView)
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
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private float calcularArea(String tipoMaterial, float ancho, float alto, float largo) {
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

    private void mostrarOpcionesImagen() {
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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = crearArchivoImagen();
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this,
                            getApplicationContext().getPackageName() + ".fileprovider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, REQUEST_CAMERA);
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
}