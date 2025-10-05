package com.example.apparend.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.apparend.R;
import com.example.apparend.ui.VisorCotasActivity;

import java.util.ArrayList;
import java.util.List;

public class CotasOverlay extends View {
    private static final String TAG = "Cotas Overlay arenado";
    private Paint paint = new Paint();
    private PointF start, end;
    private boolean drawingEnabled = false;
    private List<PointF> startPoints = new ArrayList<>();
    private List<PointF> endPoints = new ArrayList<>();
    private PointF primerPunto = null;
    private PointF segundoPunto = null;
    private boolean mostrarLupa = false;
    private PointF puntoLupa = null;
    private float zoomFactor = 2.0f;
    private int modo = 0;
    private Runnable onMedicionTerminada;
    private List<String> textosCotas = new ArrayList<>();


    // VARIABLES NUEVAS PARA MODO MEDICIÓN PRECISA
    private boolean modoMedicionPrecisa = false;
    private PointF puntoInicialMedicion = null;
    private PointF puntoActualTemporal = null;

    public CotasOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5f);
        paint.setTextSize(40f);
    }

    public void agregarCota(PointF start, PointF end, String texto) {
        startPoints.add(start);
        endPoints.add(end);
        textosCotas.add(texto);
        invalidate();
    }


    public void agregarTextoCota(String texto) {
        if (!endPoints.isEmpty()) {
            textosCotas.set(textosCotas.size() - 1, texto);
            invalidate();
        }
    }


    public void setDrawingEnabled(boolean enabled) {
        drawingEnabled = enabled;
        invalidate();
    }

    // MÉTODO NUEVO PARA ACTIVAR/DESACTIVAR MODO MEDICIÓN PRECISA
    public void setModoMedicionPrecisa(boolean activado) {
        this.modoMedicionPrecisa = activado;
        this.puntoInicialMedicion = null;
        this.puntoActualTemporal = null;
        invalidate();
    }
    public void setOnMedicionTerminadaListener(Runnable listener) {
        this.onMedicionTerminada = listener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!drawingEnabled) return false;

        ImageView imageView = getRootView().findViewById(R.id.imgVisor);
        if (imageView == null || imageView.getDrawable() == null) return false;

        RectF imageRect = getImageBounds(imageView);

        if (!imageRect.contains(event.getX(), event.getY())) {
            Log.d(TAG, "Ignorado: toque fuera de la imagen");
            return false;
        }

        switch (modo) {
            case 0: // 🔹 MODO MEDIDAS
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        mostrarLupa = true;
                        puntoLupa = new PointF(event.getX(), event.getY());

                        if (primerPunto != null && segundoPunto == null) {
                            puntoActualTemporal = new PointF(event.getX(), event.getY());
                        }

                        ((VisorCotasActivity) getContext()).runOnUiThread(() -> {
                            ((VisorCotasActivity) getContext()).setInstruccion("Suelte para agregar punto");

                        });

                        invalidate();
                        break;

                    case MotionEvent.ACTION_UP:
                        mostrarLupa = false;
                        puntoLupa = null;

                        if (primerPunto == null) {
                            primerPunto = new PointF(event.getX(), event.getY());
                            ((VisorCotasActivity) getContext()).runOnUiThread(() -> {
                                ((VisorCotasActivity) getContext()).setInstruccion("Toque para agregar el segundo punto");
                                Log.d(TAG, "Primer punto agregado: ");
                                    // + primerPunto


                            });

                        } else if (segundoPunto == null) {
                            segundoPunto = new PointF(event.getX(), event.getY());
                            startPoints.add(primerPunto);
                            endPoints.add(segundoPunto);

                            ((VisorCotasActivity) getContext()).runOnUiThread(() -> {
                                VisorCotasActivity activity = (VisorCotasActivity) getContext();
                                activity.setInstruccion("Finalizado");
                                Log.d(TAG, "Segundo punto agregado: ");

                            });

                            resetPuntos();

                            // Avisar al formulario que se terminó la medición
                            if (onMedicionTerminada != null) {
                                postDelayed(onMedicionTerminada, 500);

                            }

                        }
                        puntoActualTemporal = null;
                        invalidate();
                        break;
                }
                break;
        }
        return true;
    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 🔍 Mostrar lupa cuadrada si está activa
        if (mostrarLupa && puntoLupa != null) {
            int size = 350; // tamaño del cuadro de la lupa
            float lupaX = getWidth() - size - 40;  // esquina superior derecha
            float lupaY = 40;

            // Buscar el ImageView
            ImageView imageView = getRootView().findViewById(R.id.imgVisor);
            if (imageView != null && imageView.getDrawable() != null) {
                // Crear un Bitmap temporal del ImageView
                android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(
                        imageView.getWidth(), imageView.getHeight(),
                        android.graphics.Bitmap.Config.ARGB_8888
                );
                Canvas tempCanvas = new Canvas(bmp);
                imageView.draw(tempCanvas);

                // Definir el área de zoom alrededor del toque
                RectF srcRect = new RectF(
                        puntoLupa.x - size / (2 * zoomFactor),
                        puntoLupa.y - size / (2 * zoomFactor),
                        puntoLupa.x + size / (2 * zoomFactor),
                        puntoLupa.y + size / (2 * zoomFactor)
                );

                RectF dstRect = new RectF(lupaX, lupaY, lupaX + size, lupaY + size);

                // Dibujar el recorte ampliado en la lupa
                canvas.drawBitmap(
                        bmp,
                        new android.graphics.Rect(
                                (int) srcRect.left, (int) srcRect.top,
                                (int) srcRect.right, (int) srcRect.bottom
                        ),
                        dstRect,
                        null
                );

                // ⚡ Transformación de coordenadas a la lupa
                java.util.function.Function<PointF, PointF> toLupa = (p) -> {
                    float x = (p.x - srcRect.left) * (dstRect.width() / srcRect.width()) + dstRect.left;
                    float y = (p.y - srcRect.top) * (dstRect.height() / srcRect.height()) + dstRect.top;
                    return new PointF(x, y);
                };

                // 🔴 Solo mostrar primer punto en la lupa
                if (primerPunto != null  && segundoPunto == null && puntoActualTemporal == null) {
                    PointF p1 = toLupa.apply(primerPunto);
                    canvas.drawCircle(p1.x, p1.y, 8, paint);
                }

                // Borde de la lupa
                Paint borde = new Paint();
                borde.setStyle(Paint.Style.STROKE);
                borde.setStrokeWidth(6);
                borde.setColor(Color.BLACK);
                canvas.drawRect(dstRect, borde);

                // Cruz guía en el centro
                Paint mira = new Paint();
                mira.setColor(Color.RED);
                mira.setStyle(Paint.Style.FILL);
                canvas.drawCircle(dstRect.centerX(), dstRect.centerY(), 6, mira);
            }
        }

        // 🔴 Dibujar todas las cotas guardadas (fuera de la lupa)
        for (int i = 0; i < startPoints.size(); i++) {
            PointF s = startPoints.get(i);
            PointF e = endPoints.get(i);
            canvas.drawLine(s.x, s.y, e.x, e.y, paint);

            float midX = (s.x + e.x) / 2;
            float midY = (s.y + e.y) / 2;
            //canvas.drawText("25 cm", midX, midY, paint);
            // ⬇️ Aquí usar el texto real guardado
            String texto = (i < textosCotas.size()) ? textosCotas.get(i) : "";
            canvas.drawText(texto, midX, midY, paint);
        }

        // 🟡 Primer punto marcado
        if (primerPunto != null && segundoPunto == null) {
            canvas.drawCircle(primerPunto.x, primerPunto.y, 10, paint);

            // Línea temporal fuera de la lupa
            if (puntoActualTemporal != null) {
                canvas.drawLine(primerPunto.x, primerPunto.y,
                        puntoActualTemporal.x, puntoActualTemporal.y, paint);
            }
        }

        // 🟢 Línea definitiva
        if (primerPunto != null && segundoPunto != null) {
            canvas.drawLine(primerPunto.x, primerPunto.y,
                    segundoPunto.x, segundoPunto.y, paint);
            float midX = (primerPunto.x + segundoPunto.x) / 2;
            float midY = (primerPunto.y + segundoPunto.y) / 2;
            canvas.drawText("25 cm", midX, midY, paint);
        }
    }


    // Método auxiliar: calcula el área real de la imagen dentro del ImageView
    private RectF getImageBounds(ImageView imageView) {
        RectF rect = new RectF();
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) return rect;

        Matrix matrix = imageView.getImageMatrix();
        RectF drawableRect = new RectF(
                0, 0,
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight()
        );
        matrix.mapRect(rect, drawableRect);
        return rect;
    }

    public void clearAllCotas() {
        startPoints.clear();
        endPoints.clear();
        start = null;
        end = null;
        puntoInicialMedicion = null;
        puntoActualTemporal = null;
        invalidate();
    }

    public boolean hasCotas() {
        return !startPoints.isEmpty();
    }






    public void resetPuntos() {
        primerPunto = null;
        segundoPunto = null;
        invalidate();
    }

    public void setModo(int nuevoModo) {
        this.modo = nuevoModo;
    }




//------------------------------

    public PointF getUltimoStart() {
        return startPoints.isEmpty() ? null : startPoints.get(startPoints.size() - 1);
    }

    public PointF getUltimoEnd() {
        return endPoints.isEmpty() ? null : endPoints.get(endPoints.size() - 1);
    }

}








