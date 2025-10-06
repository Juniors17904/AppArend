package com.example.apparend.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.io.FileOutputStream;
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
    private CotasOverlay cotasOverlay;

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
        cotasOverlay = findViewById(R.id.cotasOverlay);

        // Ocultar botón Retroceder al inicio
        btnRetroceder.setVisibility(View.GONE);

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

        // Detectar toque en líneas del overlay usando el listener
        cotasOverlay.setOnLineaTocadaListener(indiceLineaTocada -> {
            Log.d(TAG, "✅ Callback recibido - Línea tocada en índice: " + indiceLineaTocada);
            runOnUiThread(() -> abrirFormularioParaEdicion(indiceLineaTocada));
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
            Pieza piezaActual = null;
            if (!listaPiezas.isEmpty()) {
                piezaActual = listaPiezas.get(listaPiezas.size() - 1);
            }

            FormularioPiezaDialog.mostrar(
                    this,
                    getListaPiezas(),
                    getPiezaAdapter(),
                    this::calcularSuperficie,
                    cotasOverlay,
                    txtInstruccion,
                    piezaActual
            );
        });

        // Botón Retroceder - funcionalidad por defecto (volver atrás)
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

                // 👉 Generar nueva imagen con las cotas dibujadas
                Uri nuevaImagen = generarImagenConCotas();

                if (nuevaImagen != null) {
                    ToastHelper.showShortToast(this, "Imagen con cotas guardada", 1000);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("imagenUri", nuevaImagen.toString()); // 👉 ahora manda la nueva
                    resultIntent.putExtra("listaPiezas", new ArrayList<>(listaPiezas));

                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Error al generar imagen con cotas", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "No hay imagen para guardar", Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void abrirFormularioParaEdicion(int indiceLinea) {
        // Determinar qué campo corresponde a esta línea
        // Asumiendo que las líneas se agregan en orden: Ancho(0), Alto(1), Largo(2)
        // Esto puede necesitar ajustes según tu lógica específica
        String campoAEditar = null;

        // Aquí necesitas lógica para mapear el índice a un campo
        // Por ahora, un mapeo simple:
        if (indiceLinea == 0) {
            campoAEditar = "Ancho";
        } else if (indiceLinea == 1) {
            campoAEditar = "Alto";
        } else if (indiceLinea == 2) {
            campoAEditar = "Largo";
        }

        if (campoAEditar != null) {
            Pieza piezaActual = null;
            if (!listaPiezas.isEmpty()) {
                piezaActual = listaPiezas.get(listaPiezas.size() - 1);
            }

            FormularioPiezaDialog.mostrar(
                    this,
                    getListaPiezas(),
                    getPiezaAdapter(),
                    this::calcularSuperficie,
                    cotasOverlay,
                    txtInstruccion,
                    piezaActual,
                    campoAEditar,
                    indiceLinea
            );
        }
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

    public float calcularSuperficie(String tipoMaterial, float ancho, float alto, float largo) {
        switch (tipoMaterial) {
            case "Plancha":
                // Superficie plana
                return ancho * alto;

            case "Tubo Cuadrado":
                // Superficie lateral de un tubo cuadrado: perímetro × largo
                float perimetroCuadrado = 2 * (ancho + alto);
                return perimetroCuadrado * largo;

            case "Cuadrado":
            case "Barra Liza Cuadrada":
                // Superficie lateral de una barra sólida cuadrada
                float perimetroBarra = 2 * (ancho + alto);
                return perimetroBarra * largo;

            case "Tubo Circular":
                // Superficie lateral de un tubo circular: circunferencia × largo
                float circunferenciaTubo = (float) (Math.PI * ancho); // ancho = diámetro
                return circunferenciaTubo * largo;

            case "Circular":
            case "Barra Liza Redonda":
                // Superficie lateral de una barra cilíndrica: circunferencia × largo
                float circunferenciaBarra = (float) (Math.PI * ancho);
                return circunferenciaBarra * largo;

            case "Ángulo":
                // Superficie de las 2 alas (interno y externo) = 4 lados
                float perimetroAngulo = 2 * (ancho + alto);
                return perimetroAngulo * largo;


            default:
                Log.w(TAG, "Material no reconocido: " + tipoMaterial);
                return 0;
        }
    }


    // Métodos para ocultar/mostrar botones cuando se abre el formulario
    public void ocultarBotones() {
        btnRecortar.setVisibility(View.GONE);
        btnGuardar.setVisibility(View.GONE);
        btnMedir.setVisibility(View.GONE);
    }

    public void mostrarBotones() {
        btnRecortar.setVisibility(View.VISIBLE);
        btnGuardar.setVisibility(View.VISIBLE);
        btnMedir.setVisibility(View.VISIBLE);
    }

    // Métodos para controlar el botón Retroceder durante el dibujo de cotas
    public void mostrarBotonRetroceder() {
        btnRetroceder.setVisibility(View.VISIBLE);
    }

    public void ocultarBotonRetroceder() {
        btnRetroceder.setVisibility(View.GONE);
    }

    public void configurarBotonRetrocederParaCancelar(Runnable onCancelar) {
        btnRetroceder.setOnClickListener(v -> {
            if (onCancelar != null) {
                onCancelar.run();
            }
        });
    }



    private Uri generarImagenConCotas() {
        try {
            Bitmap bitmap = Bitmap.createBitmap(
                    imgVisor.getWidth(),
                    imgVisor.getHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(bitmap);

            // Dibujar primero la imagen original
            imgVisor.draw(canvas);

            // Dibujar las cotas encima
            cotasOverlay.draw(canvas);

            // Guardar en archivo temporal
            File file = new File(getCacheDir(), "imagen_cotas_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            return Uri.fromFile(file);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "❌ Error generando imagen con cotas", e);
            return null;
        }
    }

}