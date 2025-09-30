package com.example.apparend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;

public class VisorCotasActivity extends AppCompatActivity {

    private static final String TAG = "arenado Visor de Cotas";
    private ImageView imgVisor;
    private Button btnRecortar,btnAgrePunto,btnRetroceder,btnGuardar ;
    private TextView txtInstruccion;
    private Button btnMedir,btnSave,btnBack;
    private Uri imagenUri;
    private boolean modoMedicionPrecisa = false;
    private String modoSeleccionado = "linea"; // por defecto
    private List<Pieza> listaPiezas = new ArrayList<>();
    private PiezaAdapter piezaAdapter;



    private final ActivityResultLauncher<Intent> cropLauncher =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            final Uri resultUri = UCrop.getOutput(result.getData());
            //Log.d(TAG, "Uri devuelto por uCrop: " + resultUri); // 👈 comprobar que no sea null

            if (resultUri != null) {
                imagenUri = resultUri;
                imgVisor.setImageURI(imagenUri);
                //Log.e(TAG, "Imagen cargada en el ImageView: " + imagenUri); // 👈 confirmar carga
                Log.e(TAG,"Imagen cargada");
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
        btnAgrePunto  = findViewById(R.id.btnAgrePunto);
        txtInstruccion= findViewById(R.id.txtInstruccion);
        LinearLayout containerButtons = findViewById(R.id.containerButtons);
        CotasOverlay cotasOverlay = findViewById(R.id.cotasOverlay);
        RadioGroup radioGroupModo = findViewById(R.id.radioGroupModo);
        RadioButton radioLinea = findViewById(R.id.radioLinea);
        RadioButton radioEtiqueta = findViewById(R.id.radioEtiqueta);



        piezaAdapter = new PiezaAdapter(listaPiezas, this, new PiezaAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Pieza pieza, int position) {
                // editar pieza (si quieres)
            }

            @Override
            public void onDeleteClick(Pieza pieza, int position) {
                // eliminar pieza (si quieres)
            }
        });




        radioGroupModo.setVisibility(View.GONE);


        Intent intent = getIntent();
        String uriString = intent.getStringExtra("imagenUri");
        if (uriString != null) {
            imagenUri = Uri.parse(uriString);
            //Log.e(TAG, "Imagen URI recibida: " + imagenUri.toString());  // Log cuando se recibe el URI de la imagen
            imgVisor.setImageURI(imagenUri);
            //Log.e(TAG, "Imagen recibida y mostrada: " + imagenUri.toString());
        } else {
            Log.w(TAG, "No se recibió imagenUri en el intent");
        }



        radioGroupModo.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioLinea) {
                modoSeleccionado = "linea";
                txtInstruccion.setText("Modo: Medidas (cotas)");
            } else if (checkedId == R.id.radioEtiqueta) {
                modoSeleccionado = "etiqueta";
                txtInstruccion.setText("Modo: Etiquetas");
            }
        });







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


        btnMedir.setOnClickListener(v -> {


//            radioGroupModo.setVisibility(View.VISIBLE);
//            cotasOverlay.setVisibility(View.VISIBLE);
//            cotasOverlay.setDrawingEnabled(true);
//
//            // Detectar modo seleccionado (0 = Medidas, 1 = Etiqueta)
//            if (radioGroupModo.getCheckedRadioButtonId() == R.id.radioLinea) {
//                cotasOverlay.setModo(0); // modo medidas
//                txtInstruccion.setText("Toque la pantalla para agregar el primer punto");
//            } else {
//                cotasOverlay.setModo(1); // modo etiquetas
//                txtInstruccion.setText("Toque la pantalla para agregar una etiqueta");
//            }
//
//            txtInstruccion.setVisibility(View.VISIBLE);
//            btnRecortar.setVisibility(View.INVISIBLE);
//            btnMedir.setVisibility(View.INVISIBLE);
//
//            cotasOverlay.resetPuntos();

//adaptando agregando el cotasoverlay y el textview

            Log.e(TAG, "btn medir");

            FormularioPiezaDialog.mostrar(
                    this,
                    getListaPiezas(),
                    getPiezaAdapter(),
                    (tipo, ancho, alto, largo) -> calcularArea(tipo, ancho, alto, largo),cotasOverlay,
                    txtInstruccion
            );



        });



        btnRetroceder.setOnClickListener(v -> {
            Log.d(TAG, "Botón Retroceder presionado");

            Intent resultIntent = new Intent();
            resultIntent.putExtra("imagenUri", imagenUri.toString());
            setResult(RESULT_OK, resultIntent);

            finish();
        });

        btnGuardar.setOnClickListener(v -> {
            if (imagenUri != null) {
                Log.e(TAG, "btn guardar");
                ToastHelper.showShortToast(this, "Modo cotas Desactivado", 1000);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("imagenUri", imagenUri.toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "No hay imagen para guardar", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(v -> {
            Log.d(TAG, "BOTON SAVE - Guardando cotas");
            ToastHelper.showShortToast(this, "Modo cotas Desactivado", 1000);

            cotasOverlay.setDrawingEnabled(false);
            cotasOverlay.setVisibility(View.GONE);

            btnRetroceder.setVisibility(View.VISIBLE);
            btnRecortar.setVisibility(View.VISIBLE);
            btnMedir.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.VISIBLE);
            btnAgrePunto.setVisibility(View.GONE);

            btnSave.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);

            Log.d(TAG, "Cotas guardadas - Modo desactivado");
        });

        btnBack.setOnClickListener(v -> {
            cotasOverlay.setDrawingEnabled(false);
            cotasOverlay.setVisibility(View.GONE);

            btnRetroceder.setVisibility(View.VISIBLE);
            btnRecortar.setVisibility(View.VISIBLE);
            btnMedir.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.VISIBLE);

            btnSave.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);

            Log.d(TAG, "Modo cotas cancelado");
        });

        btnAgrePunto.setOnClickListener(v -> {
            Log.e(TAG,"btn Agegar punto");


            cotasOverlay.setVisibility(View.VISIBLE);
            cotasOverlay.setDrawingEnabled(true);
            txtInstruccion.setVisibility(View.VISIBLE);

            cotasOverlay.resetPuntos();
            txtInstruccion.setText("Toque la pantalla para agregar el primer punto");







//            modoMedicionPrecisa = !modoMedicionPrecisa;
//
//            if (modoMedicionPrecisa) {
//                ToastHelper.showShortToast(this, "Modo medición precisa activado", 1000);
//                cotasOverlay.setModoMedicionPrecisa(true);
//                cotasOverlay.setDrawingEnabled(false);
//            } else {
//                cotasOverlay.setModoMedicionPrecisa(false);
//            }
        });

    }

//    private void guardarImagen(Uri imageUri) {
//        try {
//            Log.e(TAG, "Iniciando guardado de imagen: " + imageUri.toString());
//            File outputFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "imagen_guardada.jpg");
//
//            InputStream inputStream = getContentResolver().openInputStream(imageUri);
//            FileOutputStream outputStream = new FileOutputStream(outputFile);
//
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, length);
//            }
//
//            inputStream.close();
//            outputStream.close();
//
//            Log.d(TAG, "Imagen guardada correctamente en el almacenamiento");
//            imagenUri = Uri.fromFile(outputFile);
//
//            Toast.makeText(this, "Imagen guardada correctamente", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            Log.e(TAG, "Error guardando la imagen", e);
//            Toast.makeText(this, "No se pudo guardar la imagen", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void recortarImagen() {
        if (imagenUri == null) {
            Toast.makeText(this, "No hay imagen para recortar", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Intent de recorte fallido: imagenUri es null");
            return;
        }

        try {
            Uri destinoUri = Uri.fromFile(new File(getCacheDir(), "recortada_" + System.currentTimeMillis() + ".jpg"));

            UCrop uCrop = UCrop.of(imagenUri, destinoUri)
                    .withMaxResultSize(2000, 2000);

            UCrop.Options options = new UCrop.Options();
            options.setFreeStyleCropEnabled(true);
            options.withAspectRatio(0, 0);

            uCrop.withOptions(options);

            //Log.d(TAG, "Lanzando uCrop para imagenUri: " + imagenUri.toString());
            cropLauncher.launch(uCrop.getIntent(this));
        } catch (Exception e) {
            Log.e(TAG, "Error recortando imagen con uCrop", e);
            Toast.makeText(this, "No se puede recortar esta imagen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finish() {
        //Log.wtf(TAG, "VisorCotasActivity finalizando");
        super.finish();
    }

    public void setInstruccion(String texto) {
        txtInstruccion.setText(texto);
    }












    public List<Pieza> getListaPiezas() {
        return listaPiezas;
    }

    public PiezaAdapter getPiezaAdapter() {
        return piezaAdapter;
    }


    public float calcularArea(String tipoMaterial, float ancho, float alto, float largo) {
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

}
