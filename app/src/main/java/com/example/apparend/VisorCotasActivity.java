package com.example.apparend;

import android.content.res.Configuration;
import android.graphics.Color;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.AlertDialog;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class VisorCotasActivity extends AppCompatActivity {

    private static final String TAG = "arenado Visor de Cotas";
    private ImageView imgVisor;
    private Button btnRecortar,btnMedicionPrecisa,btnRetroceder,btnGuardar ;
    private Button btnMedir,btnSave,btnBack;
    private Uri imagenUri;
    private boolean modoMedicionPrecisa = false;

    private final ActivityResultLauncher<Intent> cropLauncher =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    final Uri resultUri = UCrop.getOutput(result.getData());
                    Log.d(TAG, "Uri devuelto por uCrop: " + resultUri); // 👈 comprobar que no sea null

                    if (resultUri != null) {
                        imagenUri = resultUri;
                        imgVisor.setImageURI(imagenUri);
                        Log.e(TAG, "Imagen cargada en el ImageView: " + imagenUri); // 👈 confirmar carga
                        Toast.makeText(this, "Imagen recortada", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "Resultado de uCrop es null");
                        Toast.makeText(this, "Recorte cancelado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.w(TAG, "Resultado de uCrop cancelado o data es null");
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visor_cotas);
        Log.d(TAG, "Pantalla visor cotas iniciada");

        btnRetroceder = findViewById(R.id.btnRetroceder);
        btnGuardar = findViewById(R.id.btnGuardar);
        imgVisor = findViewById(R.id.imgVisor);
        btnRecortar = findViewById(R.id.btnRecortar);
        btnMedir = findViewById(R.id. btnMedir);
        btnSave  = findViewById(R.id. btnSave);
        btnBack  = findViewById(R.id. btnBack);
        btnMedicionPrecisa  = findViewById(R.id. btnMedicionPrecisa);
        LinearLayout containerButtons = findViewById(R.id.containerButtons);
        CotasOverlay cotasOverlay = findViewById(R.id.cotasOverlay);

        Intent intent = getIntent();
        String uriString = intent.getStringExtra("imagenUri");
        if (uriString != null) {
            imagenUri = Uri.parse(uriString);
            Log.e(TAG, "Imagen URI recibida: " + imagenUri.toString());  // Log cuando se recibe el URI de la imagen
            imgVisor.setImageURI(imagenUri);
            Log.e(TAG, "Imagen recibida y mostrada: " + imagenUri.toString());
        } else {
            Log.w(TAG, "No se recibió imagenUri en el intent");
        }




        btnRecortar.setOnClickListener(v -> {
            Log.d(TAG, "Botón Recortar presionado");

            // VERIFICAR SI HAY COTAS EXISTENTES (USANDO MÉTODO PÚBLICO)
            if (cotasOverlay.hasCotas()) {
                new AlertDialog.Builder(this)
                        .setTitle("Advertencia")
                        .setMessage("¿Recortar imagen? Se borrarán todas las cotas existentes.")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            cotasOverlay.clearAllCotas(); // ← MÉTODO PÚBLICO
                            recortarImagen();
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                recortarImagen();
            }
        });

        //medir
        btnMedir.setOnClickListener(v -> {
            cotasOverlay.setVisibility(View.VISIBLE);
            cotasOverlay.setDrawingEnabled(true);

            // DETECTAR MODO OSCURO/CLARO (FALTA ESTO)
            boolean isDarkMode = (getResources().getConfiguration().uiMode &
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES;

            // USAR COLOR SEGÚN MODO
            int colorFondo = isDarkMode ? Color.WHITE : Color.BLACK;

            imgVisor.setBackgroundColor(colorFondo);
            findViewById(R.id.main).setBackgroundColor(colorFondo);



            btnMedicionPrecisa.setVisibility(View.VISIBLE);
            btnRetroceder.setVisibility(View.GONE);
            btnRecortar.setVisibility(View.GONE);
            btnMedir.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);

            Log.e(TAG, "btn agregar cotas");
            ToastHelper.showShortToast(this, "Modo cotas activado", 1000);
        });

        btnRetroceder.setOnClickListener(v -> {
            Log.d(TAG, "Botón Retroceder presionado");
            // Lógica para retroceder o salir
            finish();
        });

        btnGuardar.setOnClickListener(v -> {
            if (imagenUri != null) {
                Log.e(TAG, "btn guardar");
                ToastHelper.showShortToast(this, "Modo cotas Desactivado", 1000);
                // Pasar la imagenUri de vuelta a AgregarEstructuraActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("imagenUri", imagenUri.toString()); // Enviar URI
                Log.d(TAG, "Pasando imagenUri de vuelta: " + imagenUri.toString());
                setResult(RESULT_OK, resultIntent); // Establecer el resultado

                finish(); // Finalizar esta actividad
            } else {
                Toast.makeText(this, "No hay imagen para guardar", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(v -> {
            Log.d(TAG, "BOTON SAVE - Guardando cotas");
            ToastHelper.showShortToast(this, "Modo cotas Desactivado", 1000);

            // DESACTIVAR COTAS PERO SEGUIR EN LA MISMA PANTALLA
            cotasOverlay.setDrawingEnabled(false);
            cotasOverlay.setVisibility(View.GONE);

            // RESTABLECER FONDOS (NUEVO)
//            containerButtons.setBackgroundColor(Color.TRANSPARENT);
//            findViewById(R.id.conStDainerButtons).setBackgroundColor(Color.TRANSPARENT);
//            imgVisor.setBackgroundColor(Color.TRANSPARENT);

//            // DETECTAR MODO OSCURO/CLARO (FALTA ESTO)
//            boolean isDarkMode = (getResources().getConfiguration().uiMode &
//                    android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
//                    android.content.res.Configuration.UI_MODE_NIGHT_YES;
//
//            // USAR COLOR SEGÚN MODO
//            int colorFondo = isDarkMode ? Color.WHITE : Color.BLACK;
//            imgVisor.setBackgroundColor(colorFondo);
//            findViewById(R.id.main).setBackgroundColor(colorFondo);

            configurarFondoModoMedicion();

            // MOSTRAR BOTONES NORMALES
            btnRetroceder.setVisibility(View.VISIBLE);
            btnRecortar.setVisibility(View.VISIBLE);
            btnMedir.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.VISIBLE);

            // OCULTAR BOTONES DE COTAS
            btnSave.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);

            Log.d(TAG, "Cotas guardadas - Modo desactivado");
        });

        btnBack.setOnClickListener(v -> {
            // SOLO DESACTIVAR COTAS SIN GUARDAR
            cotasOverlay.setDrawingEnabled(false);
            cotasOverlay.setVisibility(View.GONE);

//            // RESTABLECER FONDOS (NUEVO)
//            findViewById(R.id.main).setBackgroundColor(getResources().getColor(android.R.color.background_light));
//            containerButtons.setBackgroundColor(Color.TRANSPARENT);
//            findViewById(R.id.conStDainerButtons).setBackgroundColor(Color.TRANSPARENT);
//            imgVisor.setBackgroundColor(Color.TRANSPARENT);

//            // DETECTAR MODO OSCURO/CLARO (FALTA ESTO)
//            boolean isDarkMode = (getResources().getConfiguration().uiMode &
//                    android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
//                    android.content.res.Configuration.UI_MODE_NIGHT_YES;
//
//            // USAR COLOR SEGÚN MODO
//            int colorFondo = isDarkMode ? Color.WHITE : Color.BLACK;
//
//            imgVisor.setBackgroundColor(colorFondo);
//            findViewById(R.id.main).setBackgroundColor(colorFondo);

            configurarFondoModoMedicion();


            // MOSTRAR BOTONES NORMALES
            btnRetroceder.setVisibility(View.VISIBLE);
            btnRecortar.setVisibility(View.VISIBLE);
            btnMedir.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.VISIBLE);

            // OCULTAR BOTONES DE COTAS
            btnSave.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);

            Log.d(TAG, "Modo cotas cancelado");
        });

        btnMedicionPrecisa.setOnClickListener(v -> {
            modoMedicionPrecisa = !modoMedicionPrecisa;

            if (modoMedicionPrecisa) {
                ToastHelper.showShortToast(this, "Modo medición precisa activado", 1000);
                cotasOverlay.setModoMedicionPrecisa(true);
                // Desactivar otras funciones
                cotasOverlay.setDrawingEnabled(false);
            } else {
                cotasOverlay.setModoMedicionPrecisa(false);
            }
        });

    }

    private void guardarImagen(Uri imageUri) {
        try {

            Log.e(TAG, "Iniciando guardado de imagen: " + imageUri.toString());  // Log que indica el inicio del guardado
            // Crear un archivo en almacenamiento interno (puedes modificarlo para almacenamiento externo)
            File outputFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "imagen_guardada.jpg");

            // Obtener el InputStream de la imagen original
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            Log.d(TAG, "Imagen guardada correctamente en el almacenamiento");
            // Actualizar imagenUri con la URI del archivo guardado
            imagenUri = Uri.fromFile(outputFile);  // Asignamos la URI del archivo guardado

            // Muestra mensaje de éxito
            Toast.makeText(this, "Imagen guardada correctamente", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Error guardando la imagen", e);
            Toast.makeText(this, "No se pudo guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void recortarImagen() {
        if (imagenUri == null) {
            Toast.makeText(this, "No hay imagen para recortar", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Intent de recorte fallido: imagenUri es null");
            return;
        }

        try {
            // Crear un archivo temporal para guardar el resultado del recorte
            Uri destinoUri = Uri.fromFile(new File(getCacheDir(), "recortada_" + System.currentTimeMillis() + ".jpg"));

            // Configurar UCrop básico
            UCrop uCrop = UCrop.of(imagenUri, destinoUri)
                    .withMaxResultSize(2000, 2000);

            // Configurar OPCIONES para modo libre - ¡ESTA ES LA CLAVE!
            UCrop.Options options = new UCrop.Options();
            options.setFreeStyleCropEnabled(true);  // ← ACTIVA MODO LIBRE
            options.withAspectRatio(0, 0);          // ← RELACIÓN LIBRE

            uCrop.withOptions(options);

            Log.d(TAG, "Lanzando uCrop para imagenUri: " + imagenUri.toString());
            cropLauncher.launch(uCrop.getIntent(this));
        } catch (Exception e) {
            Log.e(TAG, "Error recortando imagen con uCrop", e);
            Toast.makeText(this, "No se puede recortar esta imagen", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void finish() {
        Log.wtf(TAG, "VisorCotasActivity finalizando");
        super.finish();
    }

    private void configurarFondoModoMedicion() {
        // DETECTAR MODO OSCURO/CLARO
        boolean isDarkMode = (getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        // USAR MISMO COLOR DEL MODO ACTUAL (NO CONTRARIO)
        int colorFondo = isDarkMode ? Color.BLACK : Color.WHITE;

        // LOGS DETALLADOS
        Log.d(TAG, "🔍 Modo detectado: " + (isDarkMode ? "OSCURO" : "CLARO"));
        Log.d(TAG, "🎨 Color de fondo aplicado: " + (colorFondo == Color.WHITE ? "BLANCO" : "NEGRO"));
        Log.d(TAG, "📱 Tema: " + (isDarkMode ? "NEGRO" : "BLANCO") + " → Fondo medición: " + (colorFondo == Color.WHITE ? "BLANCO" : "NEGRO"));

        // APLICAR A ELEMENTOS
        imgVisor.setBackgroundColor(colorFondo);
        findViewById(R.id.main).setBackgroundColor(colorFondo);

        Log.d(TAG, "✅ Fondo configurado correctamente para modo medición");
    }







}
