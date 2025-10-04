//package com.example.apparend.ui;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.FileProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.apparend.Dao.PiezaDao;
//import com.example.apparend.adapters.PiezaAdapter;
//import com.example.apparend.R;
//import com.example.apparend.adapters.Pieza;
//import com.example.apparend.Dao.EstructuraDao;
//import com.example.apparend.models.Estructura;
//import com.example.apparend.ui.Formularios.FormularioPiezaDialog;
//
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class AgregarEstructuraActivity extends AppCompatActivity {
//
//    private static final int REQUEST_CAMERA = 1;
//    private static final int REQUEST_GALLERY = 2;
//    private static final int REQUEST_CROP_IMAGE = 3;
//    private static final String TAG = "arenado Agregar Estructuras";
//
//    private Button btnBorrarImagen,btnAgregarImagen, btnGuardarEstructura, btnAgregarPieza;
//    private ImageView imgPreview;
//    private EditText etDescripcion;
//    private RecyclerView recyclerViewItems;
//
//    private Uri photoUri;
//    private File photoFile;
//
//    private List<Pieza> listaPiezas = new ArrayList<>();
//    private PiezaAdapter piezaAdapter;
//
//
//
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_agregar_estructura);
//        Log.d(TAG, "AgregarEstructura iniciada");
//
//        iniciarVistas();
//
//
//    }
//
//
//    private void iniciarVistas() {
//        // Referencias UI
//        btnAgregarImagen = findViewById(R.id.btnAgregarImagen);
//        btnBorrarImagen = findViewById(R.id.btnBorrarImagen);
//        imgPreview = findViewById(R.id.imgPreview);
//        etDescripcion = findViewById(R.id.etDescripcion);
//        recyclerViewItems = findViewById(R.id.recyclerViewItems);
//        btnGuardarEstructura = findViewById(R.id.btnGuardarEstructura);
//        btnAgregarPieza = findViewById(R.id.btnAgregarPieza);
//
//        // Configuración del RecyclerView
//        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
//        piezaAdapter = new PiezaAdapter(listaPiezas, this, new PiezaAdapter.OnItemClickListener() {
//            @Override
//            public void onEditClick(Pieza pieza, int position) {
//                // Acción para editar (si aplica)
//            }
//
//            @Override
//            public void onDeleteClick(Pieza pieza, int position) {
//                // Acción para eliminar (si aplica)
//            }
//        });
//        recyclerViewItems.setAdapter(piezaAdapter);
//
//        btnAgregarPieza.setOnClickListener(v -> {
//            Log.d(TAG, "btn detallar (agregar pieza)");
//            if (photoUri != null) {
//                Intent intent = new Intent(this, VisorCotasActivity.class);
//                intent.putExtra("imagenUri", photoUri.toString());
//                startActivityForResult(intent, REQUEST_CROP_IMAGE);
//            } else {
//                Toast.makeText(this, "Agrega primero una imagen", Toast.LENGTH_SHORT).show();
//            }
//        });
//
////
////        btnAgregarPieza.setOnClickListener(v -> {
////            Log.d(TAG, "btn detallar (agregar pieza)");
////
////            // 🔹 Mantengo tu lógica de validar imagen primero
////            if (photoUri != null) {
////                // Abre el visor de cotas como antes
////                Intent intent = new Intent(this, VisorCotasActivity.class);
////                intent.putExtra("imagenUri", photoUri.toString());
////                startActivityForResult(intent, REQUEST_CROP_IMAGE);
////
////                // 🔹 NUEVO: Abrir el Formulario de Pieza con referencia directa al método
////                FormularioPiezaDialog.mostrar(
////                        AgregarEstructuraActivity.this,
////                        listaPiezas,
////                        piezaAdapter,
////                        AgregarEstructuraActivity.this::calcularArea,
////                        null, // si usas CotasOverlay
////                        null  // si usas txtInstruccion
////                );
////
////            } else {
////                Toast.makeText(this, "Agrega primero una imagen", Toast.LENGTH_SHORT).show();
////            }
////        });
////
//
//
//        btnBorrarImagen.setOnClickListener(v -> {
//            Log.d(TAG, "btn borrar imagen");
//            borrarImagen();
//        });
//
//        btnAgregarImagen.setOnClickListener(v -> {
//            Log.d(TAG, "btn agregar imagen");
//            mostrarOpcionesImagen();
//        });
//
//        btnGuardarEstructura.setOnClickListener(v -> {
//            Log.d(TAG, "btn Guardar estructura");
//            guardarEstructura();
//        });
//    }
//
////    public float calcularArea(String tipoMaterial, float ancho, float alto, float largo) {
////        Log.d(TAG, "calcularArea() llamado con -> material=" + tipoMaterial + ", ancho=" + ancho + ", alto=" + alto + ", largo=" + largo);
////
////        float anchoM = ancho * 0.0254f;
////        float altoM = alto * 0.0254f;
////        float largoM = largo;
////
////        switch (tipoMaterial) {
////            case "Cuadrado":
////            case "Tubo Cuadrado":
////                Log.d(TAG, "Usando fórmula Cuadrado/Tubo Cuadrado");
////                return 4 * anchoM * largoM;
////
////            case "Circular":
////            case "Tubo Circular":
////                Log.d(TAG, "Usando fórmula Circular/Tubo Circular");
////                return (float) (Math.PI * anchoM * largoM);
////
////            case "Plancha":
////                Log.d(TAG, "Usando fórmula Plancha");
////                return anchoM * altoM * 2;
////
////            case "Ángulo":
////                Log.d(TAG, "Usando fórmula Ángulo");
////                return (anchoM + altoM + Math.max(anchoM, altoM)) * largoM;
////
////            case "Barra Liza Cuadrada":
////                Log.d(TAG, "Usando fórmula Barra Liza Cuadrada");
////                return 4 * anchoM * largoM;
////
////            case "Barra Liza Redonda":
////                Log.d(TAG, "Usando fórmula Barra Liza Redonda");
////                return (float) (Math.PI * anchoM * largoM);
////
////            default:
////                Log.w(TAG, "Material no reconocido: " + tipoMaterial);
////                return 0;
////        }
////    }
//
//    private void mostrarOpcionesImagen() {
//        Log.d(TAG, "Mostrar opciones");
//        CharSequence[] opciones = {"Cámara", "Galería", "Cancelar"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Seleccionar imagen desde:");
//        builder.setItems(opciones, (dialog, which) -> {
//            if (which == 0) {
//                abrirCamara();
//            } else if (which == 1) {
//                seleccionarDeGaleria();
//            }
//        });
//        builder.show();
//    }
//
//    private void abrirCamara() {
//        Log.d(TAG, "Abrir camara");
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            try {
//                photoFile = crearArchivoImagen();
//                if (photoFile != null) {
//                    photoUri = FileProvider.getUriForFile(this,
//                            getApplicationContext().getPackageName() + ".fileprovider",
//                            photoFile);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//                    //startActivityForResult(intent, REQUEST_CAMERA);
//                    cameraLauncher.launch(intent);
//                }
//            } catch (IOException e) {
//                Log.e(TAG, "Error creando archivo", e);
//                Toast.makeText(this, "No se pudo crear archivo", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "No se encontró la cámara", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private File crearArchivoImagen() throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String nombreArchivo = "IMG_" + timeStamp + "_";
//        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        return File.createTempFile(nombreArchivo, ".jpg", directorio);
//    }
//
//    private void seleccionarDeGaleria() {
//        Log.d(TAG, "abrir galeria");
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/*");
//        startActivityForResult(intent, REQUEST_GALLERY);
//    }
//
//    private void borrarImagen() {
//
//        imgPreview.setVisibility(View.GONE);
//        imgPreview.setImageURI(null);
//        //cambia de imagen a agregar imagen
//
//        btnAgregarImagen.setVisibility(View.VISIBLE);
//        btnBorrarImagen.setVisibility(View.GONE);
//
//        // Borrar archivo de foto si existe
//        if (photoFile != null && photoFile.exists()) {
//            boolean deleted = photoFile.delete();
//            Log.d(TAG, "Archivo borrado: " + deleted);
//        }
//
//        photoUri = null;
//        photoFile = null;
//    }
//
////    private void guardarEstructura() {
////        Log.d(TAG, "Guardando");
////
////        String descripcion = etDescripcion.getText().toString().trim();
////      //  Log.d(TAG, "guardarEstructura -> descripcion ingresada: " + descripcion);
////
////        if (descripcion.isEmpty()) {
////            Toast.makeText(this, "Ingresa una descripción", Toast.LENGTH_SHORT).show();
////            Log.w(TAG, "descripción vacía");
////            return;
////        }
////
////        if (listaPiezas.isEmpty()) {
////            Toast.makeText(this, "Debes agregar al menos una pieza", Toast.LENGTH_SHORT).show();
////            Log.w(TAG, "listaPiezas vacía");
////            return;
////        }
////
////        // Calcular total de metros cuadrados
////        float totalM2Estructura = 0;
////        for (Pieza pieza : listaPiezas) {
////            totalM2Estructura += pieza.getTotalM2();
////            Log.d(TAG, "guardarEstructura -> pieza procesada: material=" + pieza.getTipoMaterial()
////                    + " ancho=" + pieza.getAncho()
////                    + " alto=" + pieza.getAlto()
////                    + " largo=" + pieza.getLargo()
////                    + " cantidad=" + pieza.getCantidad()
////                    + " totalM2=" + pieza.getTotalM2());
////        }
////        Log.d(TAG, "guardarEstructura -> totalM2 acumulado: " + totalM2Estructura);
////
////        Intent resultIntent = new Intent();
////        resultIntent.putExtra("descripcion", descripcion);
////        resultIntent.putExtra("totalM2", totalM2Estructura);
////        resultIntent.putExtra("cantidadPiezas", listaPiezas.size());
////
////        if (photoUri != null) {
////            resultIntent.putExtra("imagenUri", photoUri.toString());
////            Log.d(TAG, "guardarEstructura -> se adjunta imagen: " + photoUri.toString());
////        } else {
////            Log.d(TAG, "guardarEstructura -> sin imagen asociada");
////        }
////
////        // 🔹 Guardar en la BD
////        String imagen = (photoUri != null) ? photoUri.toString() : null;
////        long estructuraId = estructuraDao.insertEstructura(trabajoId, descripcion, imagen);
////
////        // 🔹 Guardar piezas asociadas a la estructura
////        PiezaDao piezaDao = new PiezaDao(this);
////        for (Pieza p : listaPiezas) {
////            long idPieza = piezaDao.insertPieza(p, (int) estructuraId);
////            Log.d(TAG, "guardarEstructura -> pieza guardada con id=" + idPieza);
////        }
////
////        Log.i(TAG, "guardarEstructura -> estructura insertada en BD con id=" + estructuraId
////                + ", trabajoId=" + trabajoId
////                + ", descripcion=" + descripcion
////                + ", totalM2=" + totalM2Estructura
////                + ", piezas=" + listaPiezas.size());
////
////        setResult(RESULT_OK, resultIntent);
////        Log.d(TAG, "guardarEstructura -> finalizando actividad");
////        finish();
////    }
//
//    private void guardarEstructura() {
//        Log.d(TAG, "Guardando");
//
//        String descripcion = etDescripcion.getText().toString().trim();
//
//        if (descripcion.isEmpty()) {
//            Toast.makeText(this, "Ingresa una descripción", Toast.LENGTH_SHORT).show();
//            Log.w(TAG, "descripción vacía");
//            return;
//        }
//
//        if (listaPiezas.isEmpty()) {
//            Toast.makeText(this, "Debes agregar al menos una pieza", Toast.LENGTH_SHORT).show();
//            Log.w(TAG, "listaPiezas vacía");
//            return;
//        }
//
//        // Calcular total de metros cuadrados
//        float totalM2Estructura = 0;
//        for (Pieza pieza : listaPiezas) {
//            totalM2Estructura += pieza.getTotalM2();
//            Log.d(TAG, "guardarEstructura -> pieza procesada: material=" + pieza.getTipoMaterial()
//                    + " ancho=" + pieza.getAncho()
//                    + " alto=" + pieza.getAlto()
//                    + " largo=" + pieza.getLargo()
//                    + " cantidad=" + pieza.getCantidad()
//                    + " totalM2=" + pieza.getTotalM2());
//        }
//        Log.d(TAG, "guardarEstructura -> totalM2 acumulado: " + totalM2Estructura);
//
//        // 🔹 NO GUARDAMOS EN BD AQUÍ
//        String imagen = (photoUri != null) ? photoUri.toString() : null;
//
//        // Crear objeto temporal (id y trabajoId en -1 porque todavía no existen en BD)
//        Estructura nuevaEstructura = new Estructura(
//                -1,                // id temporal
//                -1,                // trabajoId temporal
//                descripcion,
//                imagen,
//                totalM2Estructura
//        );
//
//        // Preparar resultado para devolver a AgregarNuevoTrabajoActivity
//        Intent resultIntent = new Intent();
//        resultIntent.putExtra("nuevaEstructura", nuevaEstructura);
//        resultIntent.putExtra("listaPiezas", new ArrayList<>(listaPiezas));
//
//        setResult(RESULT_OK, resultIntent);
//        Log.d(TAG, "guardarEstructura -> devolviendo estructura temporal y piezas");
//        finish();
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_CAMERA) {
//                if (photoUri != null) {
//                    imgPreview.setVisibility(View.VISIBLE);
//                    imgPreview.setImageURI(photoUri);
//
//                    btnAgregarImagen.setVisibility(View.GONE);
//                    btnBorrarImagen.setVisibility(View.VISIBLE);
//
//                    Log.d("arenado", "imagen");
//                    Toast.makeText(this, "Imagen agregadaaaaaa", Toast.LENGTH_SHORT).show();
//                }
//            } else if (requestCode == REQUEST_GALLERY) {
//                if (data != null && data.getData() != null) {
//                    photoUri = data.getData();
//                    imgPreview.setVisibility(View.VISIBLE);
//                    Log.d(TAG, "Imagen agregada");
//                    imgPreview.setImageURI(photoUri);
//
//                    btnAgregarImagen.setVisibility(View.GONE);
//                    btnBorrarImagen.setVisibility(View.VISIBLE);
//                }
//            }
//            // --- NUEVO: Manejo del resultado del crop --- //
//            else if (requestCode == REQUEST_CROP_IMAGE) {
//                if (data != null) {
//                    // 🔹 Recuperar imagen
//                    if (data.hasExtra("imagenUri")) {
//                        String uriString = data.getStringExtra("imagenUri");
//                        photoUri = Uri.parse(uriString);
//                        imgPreview.setVisibility(View.VISIBLE);
//                        imgPreview.setImageURI(photoUri);
//
//                        btnAgregarImagen.setVisibility(View.GONE);
//                        btnBorrarImagen.setVisibility(View.VISIBLE);
//
//                        Log.d(TAG, "Imagen recortada actualizada: " + uriString);
//                    }
//
//                    // 🔹 Recuperar lista de piezas
//                    if (data.hasExtra("listaPiezas")) {
//                        ArrayList<Pieza> piezasRecibidas = (ArrayList<Pieza>) data.getSerializableExtra("listaPiezas");
//                        if (piezasRecibidas != null) {
//                            listaPiezas.clear();
//                            listaPiezas.addAll(piezasRecibidas);
//                            piezaAdapter.notifyDataSetChanged();
//                            Log.d(TAG, "Se recibieron " + piezasRecibidas.size() + " piezas desde VisorCotasActivity");
//                        }
//                    }
//
//                }
//            }
//
//        }
//    }
//
//
//    //Muestra la imagen en el ImageView
//    private final ActivityResultLauncher<Intent> cameraLauncher =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//                if (result.getResultCode() == RESULT_OK && photoUri != null) {
//                    imgPreview.setVisibility(View.VISIBLE);
//                    imgPreview.setImageURI(photoUri);
//
//                    btnAgregarImagen.setVisibility(View.GONE);
//                    btnBorrarImagen.setVisibility(View.VISIBLE);
//
//                    Toast.makeText(this, "Foto Agregada", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "Imagen agregada");
//                }
//            });
//
//
//    public List<Pieza> getListaPiezas() {
//        return listaPiezas;
//    }
//
//    public PiezaAdapter getPiezaAdapter() {
//        return piezaAdapter;
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//}


package com.example.apparend.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apparend.R;
import com.example.apparend.models.Pieza;
import com.example.apparend.adapters.PiezaAdapter;
import com.example.apparend.models.Estructura;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgregarEstructuraActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_CROP_IMAGE = 3;
    private static final String TAG = "Agregar Estructuras arenado";

    private Button btnBorrarImagen, btnAgregarImagen, btnGuardarEstructura, btnAgregarPieza;
    private ImageView imgPreview;
    private EditText etDescripcion;
    private RecyclerView recyclerViewItems;

    private Uri photoUri;
    private File photoFile;

    private List<Pieza> listaPiezas = new ArrayList<>();
    private PiezaAdapter piezaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_estructura);
        Log.d(TAG, "AgregarEstructura iniciada");

        iniciarVistas();
    }

    private void iniciarVistas() {
        // Referencias UI
        btnAgregarImagen = findViewById(R.id.btnAgregarImagen);
        btnBorrarImagen = findViewById(R.id.btnBorrarImagen);
        imgPreview = findViewById(R.id.imgPreview);
        etDescripcion = findViewById(R.id.etDescripcionPieza);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        btnGuardarEstructura = findViewById(R.id.btnGuardarEstructura);
        btnAgregarPieza = findViewById(R.id.btnAgregarPieza);

        // Configuración del RecyclerView
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        piezaAdapter = new PiezaAdapter(listaPiezas, this, new PiezaAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Pieza pieza, int position) {
                // Acción para editar (si aplica)
            }

            @Override
            public void onDeleteClick(Pieza pieza, int position) {
                // Acción para eliminar (si aplica)
            }
        });
        recyclerViewItems.setAdapter(piezaAdapter);

        btnAgregarPieza.setOnClickListener(v -> {
            Log.d(TAG, "btn detallar (agregar pieza)");
            if (photoUri != null) {
                Intent intent = new Intent(this, VisorCotasActivity.class);
                intent.putExtra("imagenUri", photoUri.toString());
                startActivityForResult(intent, REQUEST_CROP_IMAGE);
            } else {
                Toast.makeText(this, "Agrega primero una imagen", Toast.LENGTH_SHORT).show();
            }
        });

        btnBorrarImagen.setOnClickListener(v -> {
            Log.d(TAG, "btn borrar imagen");
            borrarImagen();
        });

        btnAgregarImagen.setOnClickListener(v -> {
            Log.d(TAG, "btn agregar imagen");
            mostrarOpcionesImagen();
        });

        btnGuardarEstructura.setOnClickListener(v -> {
            Log.d(TAG, "btn Guardar estructura");
            guardarEstructura();
        });
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

    private void borrarImagen() {
        imgPreview.setVisibility(View.GONE);
        imgPreview.setImageURI(null);

        btnAgregarImagen.setVisibility(View.VISIBLE);
        btnBorrarImagen.setVisibility(View.GONE);

        if (photoFile != null && photoFile.exists()) {
            boolean deleted = photoFile.delete();
            Log.d(TAG, "Archivo borrado: " + deleted);
        }

        photoUri = null;
        photoFile = null;
    }

    private void guardarEstructura() {
        Log.d(TAG, "Guardando");

        String descripcion = etDescripcion.getText().toString().trim();

        if (descripcion.isEmpty()) {
            Toast.makeText(this, "Ingresa una descripción", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "descripción vacía");
            return;
        }

        if (listaPiezas.isEmpty()) {
            Toast.makeText(this, "Debes agregar al menos una pieza", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "listaPiezas vacía");
            return;
        }

        float totalM2Estructura = 0;
        for (Pieza pieza : listaPiezas) {
            totalM2Estructura += pieza.getTotalM2();
            Log.d(TAG, "pieza procesada: material=" + pieza.getTipoMaterial()
                    + " descripcion=" + pieza.getDescripcion()
                    + " ancho=" + pieza.getAncho()
                    + " alto=" + pieza.getAlto()
                    + " largo=" + pieza.getLargo()
                    + " cantidad=" + pieza.getCantidad()
                    + " totalM2=" + pieza.getTotalM2());

        }
        Log.d(TAG, "totalM2 acumulado: " + totalM2Estructura);

        String imagen = (photoUri != null) ? photoUri.toString() : null;

        // Crear objeto temporal
        Estructura nuevaEstructura = new Estructura(
                -1,
                -1,
                descripcion,
                imagen,
                totalM2Estructura
        );

        // 🔹 ASIGNAR piezas a la estructura
        nuevaEstructura.setListaPiezas(new ArrayList<>(listaPiezas));

        // Preparar resultado
        Intent resultIntent = new Intent();
        resultIntent.putExtra("nuevaEstructura", nuevaEstructura);

        resultIntent.putExtra("listaPiezas", new ArrayList<>(listaPiezas));

        setResult(RESULT_OK, resultIntent);
        Log.d(TAG, "devolviendo estructura con " + listaPiezas.size() + " piezas");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                if (photoUri != null) {
                    mostrarImagenSeleccionada(photoUri);
                    Toast.makeText(this, "Imagen agregada", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_GALLERY) {
                if (data != null && data.getData() != null) {
                    photoUri = data.getData();
                    mostrarImagenSeleccionada(photoUri);
                }
            } else if (requestCode == REQUEST_CROP_IMAGE) {
                if (data != null) {
                    if (data.hasExtra("imagenUri")) {
                        String uriString = data.getStringExtra("imagenUri");
                        photoUri = Uri.parse(uriString);
                        mostrarImagenSeleccionada(photoUri);
                        Log.d(TAG, "Mostrar Imagen ");
                    }

                    if (data.hasExtra("listaPiezas")) {
                        ArrayList<Pieza> piezasRecibidas = (ArrayList<Pieza>) data.getSerializableExtra("listaPiezas");
                        if (piezasRecibidas != null) {
                            listaPiezas.addAll(piezasRecibidas);
                            piezaAdapter.notifyDataSetChanged();
                            Log.d(TAG, "Se recibieron " + piezasRecibidas.size() + " piezas desde VisorCotasActivity");
                        }
                    }
                }
            }
        }
    }

    // Método auxiliar para no repetir código
    private void mostrarImagenSeleccionada(Uri uri) {
        imgPreview.setVisibility(View.VISIBLE);
        imgPreview.setImageURI(uri);
        btnAgregarImagen.setVisibility(View.GONE);
        btnBorrarImagen.setVisibility(View.VISIBLE);
    }

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && photoUri != null) {
                    mostrarImagenSeleccionada(photoUri);
                    Toast.makeText(this, "Foto agregada", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Imagen agregada");
                }
            });

    public List<Pieza> getListaPiezas() {
        return listaPiezas;
    }

    public PiezaAdapter getPiezaAdapter() {
        return piezaAdapter;
    }
}
