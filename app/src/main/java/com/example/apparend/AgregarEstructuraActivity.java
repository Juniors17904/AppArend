package com.example.apparend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.core.content.FileProvider;

import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AgregarEstructuraActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final String TAG = "arenado";

    private Button btnAgregarImagen, btnFinalizarEstructura;
    private ImageView imgPreview;
    private EditText etDescripcion;
    private RecyclerView recyclerViewItems;

    private Uri photoUri; // Guardar la foto tomada
    private File photoFile; // Referencia al archivo creado

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

        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));

        btnAgregarImagen.setOnClickListener(v -> {
            Log.d(TAG, "Agrear imagen: ");
            if (photoUri == null) {
                mostrarOpcionesImagen();
            } else {
                borrarImagen();
            }
        });

        btnFinalizarEstructura.setOnClickListener(v -> guardarEstructura());
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

        Intent resultIntent = new Intent();
        resultIntent.putExtra("descripcion", descripcion);
        if (photoUri != null) {
            resultIntent.putExtra("imagenUri", photoUri.toString());
        }
        setResult(RESULT_OK, resultIntent);
        finish();
    }

















}
