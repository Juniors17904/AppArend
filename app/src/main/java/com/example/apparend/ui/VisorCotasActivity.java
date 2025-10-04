//package com.example.apparend.ui;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.app.AlertDialog;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.apparend.utils.CotasOverlay;
//import com.example.apparend.ui.Formularios.FormularioPiezaDialog;
//import com.example.apparend.adapters.PiezaAdapter;
//import com.example.apparend.R;
//import com.example.apparend.utils.ToastHelper;
//import com.example.apparend.adapters.Pieza;
//import com.yalantis.ucrop.UCrop;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//public class VisorCotasActivity extends AppCompatActivity {
//
//    private static final String TAG = "arenado Visor de Cotas";
//    private ImageView imgVisor;
//    private Button btnRecortar,btnAgrePunto,btnRetroceder,btnGuardar ;
//    private TextView txtInstruccion;
//    private Button btnMedir,btnSave,btnBack;
//    private Uri imagenUri;
//    private boolean modoMedicionPrecisa = false;
//    private String modoSeleccionado = "linea"; // por defecto
//    private List<Pieza> listaPiezas = new ArrayList<>();
//    private PiezaAdapter piezaAdapter;
//
//
//
//    private final ActivityResultLauncher<Intent> cropLauncher =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//            final Uri resultUri = UCrop.getOutput(result.getData());
//            //Log.d(TAG, "Uri devuelto por uCrop: " + resultUri); // 👈 comprobar que no sea null
//
//            if (resultUri != null) {
//                imagenUri = resultUri;
//                imgVisor.setImageURI(imagenUri);
//                //Log.e(TAG, "Imagen cargada en el ImageView: " + imagenUri); // 👈 confirmar carga
//                Log.e(TAG,"Imagen cargada");
//                Toast.makeText(this, "Imagen recortada", Toast.LENGTH_SHORT).show();
//            } else {
//                Log.w(TAG, "Resultado de uCrop es null");
//                Toast.makeText(this, "Recorte cancelado", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Log.w(TAG, "Resultado de uCrop cancelado o data es null");
//        }
//    });
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_visor_cotas);
//        Log.d(TAG, "Pantalla visor cotas iniciada");
//
//        btnRetroceder = findViewById(R.id.btnRetroceder);
//        btnGuardar = findViewById(R.id.btnGuardar);
//        imgVisor = findViewById(R.id.imgVisor);
//        btnRecortar = findViewById(R.id.btnRecortar);
//        btnMedir = findViewById(R.id. btnMedir);
//        btnSave  = findViewById(R.id. btnSave);
//        btnBack  = findViewById(R.id. btnBack);
//        btnAgrePunto  = findViewById(R.id.btnAgrePunto);
//        txtInstruccion= findViewById(R.id.txtInstruccion);
//        LinearLayout containerButtons = findViewById(R.id.containerButtons);
//        CotasOverlay cotasOverlay = findViewById(R.id.cotasOverlay);
//        RadioGroup radioGroupModo = findViewById(R.id.radioGroupModo);
//        RadioButton radioLinea = findViewById(R.id.radioLinea);
//        RadioButton radioEtiqueta = findViewById(R.id.radioEtiqueta);
//
//
//
//        piezaAdapter = new PiezaAdapter(listaPiezas, this, new PiezaAdapter.OnItemClickListener() {
//            @Override
//            public void onEditClick(Pieza pieza, int position) {
//                // editar pieza (si quieres)
//            }
//
//            @Override
//            public void onDeleteClick(Pieza pieza, int position) {
//                // eliminar pieza (si quieres)
//            }
//        });
//
//
//
//
//        radioGroupModo.setVisibility(View.GONE);
//
//
//        Intent intent = getIntent();
//        String uriString = intent.getStringExtra("imagenUri");
//        if (uriString != null) {
//            imagenUri = Uri.parse(uriString);
//            //Log.e(TAG, "Imagen URI recibida: " + imagenUri.toString());  // Log cuando se recibe el URI de la imagen
//            imgVisor.setImageURI(imagenUri);
//            //Log.e(TAG, "Imagen recibida y mostrada: " + imagenUri.toString());
//        } else {
//            Log.w(TAG, "No se recibió imagenUri en el intent");
//        }
//
//
//
//        radioGroupModo.setOnCheckedChangeListener((group, checkedId) -> {
//            if (checkedId == R.id.radioLinea) {
//                modoSeleccionado = "linea";
//                txtInstruccion.setText("Modo: Medidas (cotas)");
//            } else if (checkedId == R.id.radioEtiqueta) {
//                modoSeleccionado = "etiqueta";
//                txtInstruccion.setText("Modo: Etiquetas");
//            }
//        });
//
//
//
//
//
//
//
//        btnRecortar.setOnClickListener(v -> {
//            Log.d(TAG, "Botón Recortar presionado");
//
//            // VERIFICAR SI HAY COTAS EXISTENTES (USANDO MÉTODO PÚBLICO)
//            if (cotasOverlay.hasCotas()) {
//                new AlertDialog.Builder(this)
//                        .setTitle("Advertencia")
//                        .setMessage("¿Recortar imagen? Se borrarán todas las cotas existentes.")
//                        .setPositiveButton("Sí", (dialog, which) -> {
//                            cotasOverlay.clearAllCotas(); // ← MÉTODO PÚBLICO
//                            recortarImagen();
//                        })
//                        .setNegativeButton("No", null)
//                        .show();
//            } else {
//                recortarImagen();
//            }
//        });
//
//
//        btnMedir.setOnClickListener(v -> {
//
//
////            radioGroupModo.setVisibility(View.VISIBLE);
////            cotasOverlay.setVisibility(View.VISIBLE);
////            cotasOverlay.setDrawingEnabled(true);
////
////            // Detectar modo seleccionado (0 = Medidas, 1 = Etiqueta)
////            if (radioGroupModo.getCheckedRadioButtonId() == R.id.radioLinea) {
////                cotasOverlay.setModo(0); // modo medidas
////                txtInstruccion.setText("Toque la pantalla para agregar el primer punto");
////            } else {
////                cotasOverlay.setModo(1); // modo etiquetas
////                txtInstruccion.setText("Toque la pantalla para agregar una etiqueta");
////            }
////
////            txtInstruccion.setVisibility(View.VISIBLE);
////            btnRecortar.setVisibility(View.INVISIBLE);
////            btnMedir.setVisibility(View.INVISIBLE);
////
////            cotasOverlay.resetPuntos();
//
////adaptando agregando el cotasoverlay y el textview
//
//            Log.e(TAG, "btn medir");
//
//            FormularioPiezaDialog.mostrar(
//                    this,
//                    getListaPiezas(),
//                    getPiezaAdapter(),
//                    (tipo, ancho, alto, largo) -> calcularArea(tipo, ancho, alto, largo),cotasOverlay,
//                    txtInstruccion
//            );
//
//
//
//        });
//
//
//
//        btnRetroceder.setOnClickListener(v -> {
//            Log.d(TAG, "Botón Retroceder presionado");
//
//            Intent resultIntent = new Intent();
//            resultIntent.putExtra("imagenUri", imagenUri.toString());
//            setResult(RESULT_OK, resultIntent);
//
//            finish();
//        });
//
////        btnGuardar.setOnClickListener(v -> {
////            if (imagenUri != null) {
////                Log.e(TAG, "btn guardar");
////                ToastHelper.showShortToast(this, "Modo cotas Desactivado", 1000);
////
////                Intent resultIntent = new Intent();
////                resultIntent.putExtra("imagenUri", imagenUri.toString());
////                setResult(RESULT_OK, resultIntent);
////                finish();
////            } else {
////                Toast.makeText(this, "No hay imagen para guardar", Toast.LENGTH_SHORT).show();
////            }
////        });
//
//        btnGuardar.setOnClickListener(v -> {
//            if (imagenUri != null) {
//                Log.e(TAG, "btn guardar");
//                ToastHelper.showShortToast(this, "Modo cotas Desactivado", 1000);
//
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("imagenUri", imagenUri.toString());
//
//                // Enviar también las piezas
//                resultIntent.putExtra("listaPiezas", new ArrayList<>(listaPiezas));
//
//
//                setResult(RESULT_OK, resultIntent);
//                finish();
//            } else {
//                Toast.makeText(this, "No hay imagen para guardar", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//    }
//
//    //        btnSave.setOnClickListener(v -> {
////            Log.d(TAG, "BOTON SAVE - Guardando cotas");
////            ToastHelper.showShortToast(this, "Modo cotas Desactivado", 1000);
////
////            cotasOverlay.setDrawingEnabled(false);
////            cotasOverlay.setVisibility(View.GONE);
////
////            btnRetroceder.setVisibility(View.VISIBLE);
////            btnRecortar.setVisibility(View.VISIBLE);
////            btnMedir.setVisibility(View.VISIBLE);
////            btnGuardar.setVisibility(View.VISIBLE);
////            btnAgrePunto.setVisibility(View.GONE);
////
////            btnSave.setVisibility(View.GONE);
////            btnBack.setVisibility(View.GONE);
////
////            Log.d(TAG, "Cotas guardadas - Modo desactivado");
////        });
//
////        btnBack.setOnClickListener(v -> {
////            cotasOverlay.setDrawingEnabled(false);
////            cotasOverlay.setVisibility(View.GONE);
////
////            btnRetroceder.setVisibility(View.VISIBLE);
////            btnRecortar.setVisibility(View.VISIBLE);
////            btnMedir.setVisibility(View.VISIBLE);
////            btnGuardar.setVisibility(View.VISIBLE);
////
////            btnSave.setVisibility(View.GONE);
////            btnBack.setVisibility(View.GONE);
////
////            Log.d(TAG, "Modo cotas cancelado");
////        });
//
////        btnAgrePunto.setOnClickListener(v -> {
////            Log.e(TAG,"btn Agegar punto");
////
////
////            cotasOverlay.setVisibility(View.VISIBLE);
////            cotasOverlay.setDrawingEnabled(true);
////            txtInstruccion.setVisibility(View.VISIBLE);
////
////            cotasOverlay.resetPuntos();
////            txtInstruccion.setText("Toque la pantalla para agregar el primer punto");
////
////
////
////
//
//
//
////            modoMedicionPrecisa = !modoMedicionPrecisa;
////
////            if (modoMedicionPrecisa) {
////                ToastHelper.showShortToast(this, "Modo medición precisa activado", 1000);
////                cotasOverlay.setModoMedicionPrecisa(true);
////                cotasOverlay.setDrawingEnabled(false);
////            } else {
////                cotasOverlay.setModoMedicionPrecisa(false);
////            }
////        });
//
////    private void guardarImagen(Uri imageUri) {
////        try {
////            Log.e(TAG, "Iniciando guardado de imagen: " + imageUri.toString());
////            File outputFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "imagen_guardada.jpg");
////
////            InputStream inputStream = getContentResolver().openInputStream(imageUri);
////            FileOutputStream outputStream = new FileOutputStream(outputFile);
////
////            byte[] buffer = new byte[1024];
////            int length;
////            while ((length = inputStream.read(buffer)) != -1) {
////                outputStream.write(buffer, 0, length);
////            }
////
////            inputStream.close();
////            outputStream.close();
////
////            Log.d(TAG, "Imagen guardada correctamente en el almacenamiento");
////            imagenUri = Uri.fromFile(outputFile);
////
////            Toast.makeText(this, "Imagen guardada correctamente", Toast.LENGTH_SHORT).show();
////        } catch (IOException e) {
////            Log.e(TAG, "Error guardando la imagen", e);
////            Toast.makeText(this, "No se pudo guardar la imagen", Toast.LENGTH_SHORT).show();
////        }
////    }
//
//    private void recortarImagen() {
//        if (imagenUri == null) {
//            Toast.makeText(this, "No hay imagen para recortar", Toast.LENGTH_SHORT).show();
//            Log.w(TAG, "Intent de recorte fallido: imagenUri es null");
//            return;
//        }
//
//        try {
//            Uri destinoUri = Uri.fromFile(new File(getCacheDir(), "recortada_" + System.currentTimeMillis() + ".jpg"));
//
//            UCrop uCrop = UCrop.of(imagenUri, destinoUri)
//                    .withMaxResultSize(2000, 2000);
//
//            UCrop.Options options = new UCrop.Options();
//            options.setFreeStyleCropEnabled(true);
//            options.withAspectRatio(0, 0);
//
//            uCrop.withOptions(options);
//
//            //Log.d(TAG, "Lanzando uCrop para imagenUri: " + imagenUri.toString());
//            cropLauncher.launch(uCrop.getIntent(this));
//        } catch (Exception e) {
//            Log.e(TAG, "Error recortando imagen con uCrop", e);
//            Toast.makeText(this, "No se puede recortar esta imagen", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void finish() {
//        //Log.wtf(TAG, "VisorCotasActivity finalizando");
//        super.finish();
//    }
//
//    public void setInstruccion(String texto) {
//        txtInstruccion.setText(texto);
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
//    public float calcularArea(String tipoMaterial, float ancho, float alto, float largo) {
//       // Log.d(TAG, "calcularArea() -> material=" + tipoMaterial + ", ancho=" + ancho + ", alto=" + alto + ", largo=" + largo);
//
//        switch (tipoMaterial) {
//            case "Cuadrado":
//            case "Tubo Cuadrado":
//                // Sección cuadrada: lado * lado * largo
//                return (ancho * ancho) * largo;
//
//            case "Circular":
//            case "Tubo Circular":
//                //  ancho = diámetro → área círculo = (π * d² / 4) * largo
//                return (float) (Math.PI * Math.pow(ancho, 2) / 4f) * largo;
//
//            case "Plancha":
//                //  Lámina plana: ancho * alto
//                return ancho * alto;
//
//            case "Ángulo":
//                //  Perfil en "L": (ancho * alto / 2) * largo
//                return ((ancho * alto) / 2f) * largo;
//
//            case "Barra Liza Cuadrada":
//                //  Barra cuadrada maciza: lado * lado * largo
//                return (ancho * ancho) * largo;
//
//            case "Barra Liza Redonda":
//                //  Barra redonda maciza: (π * d² / 4) * largo
//                return (float) (Math.PI * Math.pow(ancho, 2) / 4f) * largo;
//
//            default:
//                Log.w(TAG, "Material no reconocido: " + tipoMaterial);
//                return 0;
//        }
//    }
//
//}

//nuevo codigoooooo

package com.example.apparend.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.apparend.utils.CotasOverlay;
import com.example.apparend.ui.Formularios.FormularioPiezaDialog;
import com.example.apparend.adapters.PiezaAdapter;
import com.example.apparend.R;
import com.example.apparend.utils.ToastHelper;
import com.example.apparend.models.Pieza;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VisorCotasActivity extends AppCompatActivity {

    private static final String TAG = "Visor de Cotas arenado";
    private ImageView imgVisor;
    private Button btnRecortar, btnAgrePunto, btnRetroceder, btnGuardar;
    private TextView txtInstruccion;
    private Button btnMedir;
    private Uri imagenUri;
    private List<Pieza> listaPiezas = new ArrayList<>();
    private PiezaAdapter piezaAdapter;

    // ActivityResult para crop con uCrop
    private final ActivityResultLauncher<Intent> cropLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    final Uri resultUri = UCrop.getOutput(result.getData());
                    if (resultUri != null) {
                        imagenUri = resultUri;
                        imgVisor.setImageURI(imagenUri);
                        Log.d(TAG, "Imagen recortada: ");
                        Toast.makeText(this, "Imagen recortada", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "Resultado de uCrop es null");
                        Toast.makeText(this, "Recorte cancelado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.w(TAG, "Recorte cancelado o data null");
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visor_cotas);
        Log.d(TAG, "Pantalla visor cotas iniciada");

        // Referencias UI
        btnRetroceder = findViewById(R.id.btnRetroceder);
        btnGuardar = findViewById(R.id.btnGuardar);
        imgVisor = findViewById(R.id.imgVisor);
        btnRecortar = findViewById(R.id.btnRecortar);
        btnMedir = findViewById(R.id.btnMedir);
        txtInstruccion = findViewById(R.id.txtInstruccion);
        LinearLayout containerButtons = findViewById(R.id.containerButtons);
        CotasOverlay cotasOverlay = findViewById(R.id.cotasOverlay);


        // Adapter de piezas
        piezaAdapter = new PiezaAdapter(listaPiezas, this, new PiezaAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Pieza pieza, int position) {
                // Acción editar (si aplica)
            }

            @Override
            public void onDeleteClick(Pieza pieza, int position) {
                // Acción eliminar (si aplica)
            }
        });



        // Recibir imagen
        Intent intent = getIntent();
        String uriString = intent.getStringExtra("imagenUri");
        if (uriString != null) {
            imagenUri = Uri.parse(uriString);
            imgVisor.setImageURI(imagenUri);
            Log.d(TAG, "Imagen recibida ");
        } else {
            Log.w(TAG, "No se recibió imagenUri en el intent");
        }

        btnRecortar.setOnClickListener(v -> {
            Log.d(TAG, "Botón Recortar presionado");
            if (cotasOverlay.hasCotas()) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Advertencia")
                        .setMessage("¿Recortar imagen? Se borrarán todas las cotas existentes.")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            cotasOverlay.clearAllCotas();
                            recortarImagen();
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                recortarImagen();
            }
        });

        // Botón Medir → abre formulario de pieza
        btnMedir.setOnClickListener(v -> {
            Log.d(TAG, "Botón Medir presionado");
            FormularioPiezaDialog.mostrar(
                    this,
                    getListaPiezas(),
                    getPiezaAdapter(),
                    this::calcularArea,
                    cotasOverlay,
                    txtInstruccion
            );
        });

        // Botón Retroceder
        btnRetroceder.setOnClickListener(v -> {
            Log.d(TAG, "Botón Retroceder presionado");
            Intent resultIntent = new Intent();
            resultIntent.putExtra("imagenUri", imagenUri.toString());
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Botón Guardar
        btnGuardar.setOnClickListener(v -> {
            if (imagenUri != null) {
                Log.d(TAG, "Botón Guardar presionado");
                ToastHelper.showShortToast(this, "Datos guardados", 1000);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("imagenUri", imagenUri.toString());
                resultIntent.putExtra("listaPiezas", new ArrayList<>(listaPiezas));

                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "No hay imagen para guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void recortarImagen() {
        if (imagenUri == null) {
            Toast.makeText(this, "No hay imagen para recortar", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Intent de recorte fallido: imagenUri es null");
            return;
        }

        try {
            Uri destinoUri = Uri.fromFile(new File(getCacheDir(),
                    "recortada_" + System.currentTimeMillis() + ".jpg"));

            UCrop uCrop = UCrop.of(imagenUri, destinoUri)
                    .withMaxResultSize(2000, 2000);

            UCrop.Options options = new UCrop.Options();
            options.setFreeStyleCropEnabled(true);
            options.withAspectRatio(0, 0);

            uCrop.withOptions(options);
            cropLauncher.launch(uCrop.getIntent(this));
        } catch (Exception e) {
            Log.e(TAG, "Error recortando imagen con uCrop", e);
            Toast.makeText(this, "No se puede recortar esta imagen", Toast.LENGTH_SHORT).show();
        }
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
        switch (tipoMaterial) {
            case "Cuadrado":
            case "Tubo Cuadrado":
                return (ancho * ancho) * largo;

            case "Circular":
            case "Tubo Circular":
                return (float) (Math.PI * Math.pow(ancho, 2) / 4f) * largo;

            case "Plancha":
                return ancho * alto;

            case "Ángulo":
                return ((ancho * alto) / 2f) * largo;

            case "Barra Liza Cuadrada":
                return (ancho * ancho) * largo;

            case "Barra Liza Redonda":
                return (float) (Math.PI * Math.pow(ancho, 2) / 4f) * largo;

            default:
                Log.w(TAG, "Material no reconocido: " + tipoMaterial);
                return 0;
        }
    }
}
