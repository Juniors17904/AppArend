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
    private List<String> textosCotas = new ArrayList<>();
    // 🎨 Paints separados
    private Paint paintCota = new Paint();       // para líneas y puntos
    private Paint paintTexto = new Paint();      // texto relleno
    private Paint paintTextoBorde = new Paint(); // borde negro del texto


    private PointF primerPunto = null;
    private PointF segundoPunto = null;
    private boolean mostrarLupa = false;
    private PointF puntoLupa = null;
    private float zoomFactor = 2.0f;
    private int modo = 0;
    private Runnable onMedicionTerminada;

    // Listener para cuando se toca una línea
    public interface OnLineaTocadaListener {
        void onLineaTocada(int indice);
    }

    private OnLineaTocadaListener lineaTocadaListener;

    public void setOnLineaTocadaListener(OnLineaTocadaListener listener) {
        this.lineaTocadaListener = listener;
    }

    // VARIABLES NUEVAS PARA MODO MEDICIÓN PRECISA (sin uso actual)
    private boolean modoMedicionPrecisa = false;
    private PointF puntoInicialMedicion = null;
    private PointF puntoActualTemporal = null;

    public CotasOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 🔹 Color principal único (puedes cambiarlo a WHITE, CYAN, etc.)
        int colorPrincipal = Color.YELLOW;

        // Línea y puntos
        paintCota.setColor(colorPrincipal);
        paintCota.setStrokeWidth(6f);
        paintCota.setAntiAlias(true);

        // Texto relleno
        paintTexto.setColor(colorPrincipal);
        paintTexto.setTextSize(50f);
        paintTexto.setFakeBoldText(true);
        paintTexto.setAntiAlias(true);

        // Texto borde negro
        paintTextoBorde.setColor(Color.BLACK);
        paintTextoBorde.setTextSize(50f);
        paintTextoBorde.setFakeBoldText(true);
        paintTextoBorde.setStyle(Paint.Style.STROKE);
        paintTextoBorde.setStrokeWidth(4f);
        paintTextoBorde.setAntiAlias(true);
    }


    public void agregarCota(PointF start, PointF end, String texto) {
        startPoints.add(start);
        endPoints.add(end);
        textosCotas.add(texto);
        invalidate();
    }

    public void agregarTextoCota(String texto) {
        if (!textosCotas.isEmpty()) {
            int index = textosCotas.size() - 1;
            textosCotas.set(index, texto);
            Log.d(TAG, "Texto asignado a la cota en índice " + index + ": " + texto);
            invalidate();
        }
    }

    // Método nuevo para actualizar por índice específico
    public void actualizarTextoCotaPorIndice(int indice, String texto) {
        if (indice >= 0 && indice < textosCotas.size()) {
            textosCotas.set(indice, texto);
            Log.d(TAG, "Texto actualizado en índice " + indice + ": " + texto);
            invalidate();
        } else {
            Log.w(TAG, "Índice fuera de rango: " + indice + " (tamaño: " + textosCotas.size() + ")");
        }
    }

    public int getCantidadCotas() {
        return startPoints.size();
    }

    public void setDrawingEnabled(boolean enabled) {
        drawingEnabled = enabled;
        invalidate();
    }

    public boolean isDrawingEnabled() {
        return drawingEnabled;
    }

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
        Log.d(TAG, "onTouchEvent - Action: " + event.getAction() + " | drawingEnabled: " + drawingEnabled);

        if (!drawingEnabled) {
            Log.d(TAG, "Drawing NO habilitado - verificando si tocó una línea...");

            if (event.getAction() == MotionEvent.ACTION_UP) {
                float tolerancia = 50f; // 50 píxeles de tolerancia (aumentada)
                int indice = detectarToqueEnLinea(event.getX(), event.getY(), tolerancia);

                if (indice >= 0 && lineaTocadaListener != null) {
                    Log.d(TAG, "✅ NOTIFICANDO al listener que se tocó línea " + indice);
                    lineaTocadaListener.onLineaTocada(indice);
                    return true; // Consumir el evento
                }
            }

            return true; // Seguir consumiendo eventos aunque no haya línea tocada
        }

        ImageView imageView = getRootView().findViewById(R.id.imgVisor);
        if (imageView == null || imageView.getDrawable() == null) return false;

        RectF imageRect = getImageBounds(imageView);

        if (!imageRect.contains(event.getX(), event.getY())) {
            Log.d(TAG, "Ignorado: toque fuera de la imagen");
            return false;
        }

        switch (modo) {
            case 0: // MODO MEDIDAS
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
                                Log.d(TAG, "Primer punto agregado: " + primerPunto);
                            });

                        } else if (segundoPunto == null) {
                            segundoPunto = new PointF(event.getX(), event.getY());
                            startPoints.add(primerPunto);
                            endPoints.add(segundoPunto);

                            // Agregar sin texto (vacío)
                            textosCotas.add("");
                            Log.d(TAG, "Cota agregada sin texto en índice: " + (textosCotas.size() - 1));

                            ((VisorCotasActivity) getContext()).runOnUiThread(() -> {
                                VisorCotasActivity activity = (VisorCotasActivity) getContext();
                                activity.setInstruccion("Finalizado");
                                Log.d(TAG, "Segundo punto agregado: " + segundoPunto);
                            });

                            resetPuntos();

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

        // Dibujar TODAS las cotas guardadas PRIMERO
        for (int i = 0; i < startPoints.size(); i++) {
            PointF s = startPoints.get(i);
            PointF e = endPoints.get(i);

            // Línea
            canvas.drawLine(s.x, s.y, e.x, e.y, paintCota);

            // Círculos en los extremos
            canvas.drawCircle(s.x, s.y, 8, paintCota);
            canvas.drawCircle(e.x, e.y, 8, paintCota);

            // Texto en el medio, pero un poco separado de la línea
            float midX = (s.x + e.x) / 2;
            float midY = (s.y + e.y) / 2;
            String texto = (i < textosCotas.size()) ? textosCotas.get(i) : "";

            // Vector de la línea
            float dx = e.x - s.x;
            float dy = e.y - s.y;

            // Longitud de la línea
            float length = (float) Math.sqrt(dx * dx + dy * dy);

            // Normal (perpendicular unitaria)
            float nx = -dy / length;
            float ny = dx / length;

            // Separación en píxeles (puedes ajustar)
            float offset = 40f;

            // Nueva posición del texto, desplazada de la línea
            float textX = midX + nx * offset;
            float textY = midY + ny * offset;

            if (!texto.isEmpty()) {
                canvas.drawText(texto, textX, textY, paintTextoBorde);
                canvas.drawText(texto, textX, textY, paintTexto);
            }

        }

        // Lupa cuadrada si está activa
        if (mostrarLupa && puntoLupa != null) {
            int size = 350;
            float lupaX = getWidth() - size - 40;
            float lupaY = 40;

            ImageView imageView = getRootView().findViewById(R.id.imgVisor);
            if (imageView != null && imageView.getDrawable() != null) {
                android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(
                        imageView.getWidth(), imageView.getHeight(),
                        android.graphics.Bitmap.Config.ARGB_8888
                );
                Canvas tempCanvas = new Canvas(bmp);
                imageView.draw(tempCanvas);

                RectF srcRect = new RectF(
                        puntoLupa.x - size / (2 * zoomFactor),
                        puntoLupa.y - size / (2 * zoomFactor),
                        puntoLupa.x + size / (2 * zoomFactor),
                        puntoLupa.y + size / (2 * zoomFactor)
                );

                RectF dstRect = new RectF(lupaX, lupaY, lupaX + size, lupaY + size);

                canvas.drawBitmap(
                        bmp,
                        new android.graphics.Rect(
                                (int) srcRect.left, (int) srcRect.top,
                                (int) srcRect.right, (int) srcRect.bottom
                        ),
                        dstRect,
                        null
                );

                java.util.function.Function<PointF, PointF> toLupa = (p) -> {
                    float x = (p.x - srcRect.left) * (dstRect.width() / srcRect.width()) + dstRect.left;
                    float y = (p.y - srcRect.top) * (dstRect.height() / srcRect.height()) + dstRect.top;
                    return new PointF(x, y);
                };

                if (primerPunto != null && segundoPunto == null && puntoActualTemporal == null) {
                    PointF p1 = toLupa.apply(primerPunto);
                    canvas.drawCircle(p1.x, p1.y, 8, paintCota);
                }

                Paint borde = new Paint();
                borde.setStyle(Paint.Style.STROKE);
                borde.setStrokeWidth(6);
                borde.setColor(Color.BLACK);
                canvas.drawRect(dstRect, borde);

                Paint mira = new Paint();
                mira.setColor(Color.RED);
                mira.setStyle(Paint.Style.FILL);
                canvas.drawCircle(dstRect.centerX(), dstRect.centerY(), 6, mira);
            }
        }

        // Primer punto marcado (temporal)
        if (primerPunto != null && segundoPunto == null) {
            canvas.drawCircle(primerPunto.x, primerPunto.y, 10, paintCota);

            if (puntoActualTemporal != null) {
                canvas.drawLine(primerPunto.x, primerPunto.y,
                        puntoActualTemporal.x, puntoActualTemporal.y, paintCota);
            }
        }
    }


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
        textosCotas.clear();
        start = null;
        end = null;
        puntoInicialMedicion = null;
        puntoActualTemporal = null;
        primerPunto = null;
        segundoPunto = null;
        invalidate();
    }

    public boolean hasCotas() {
        return !startPoints.isEmpty();
    }

    public void resetPuntos() {
        primerPunto = null;
        segundoPunto = null;
        puntoActualTemporal = null;
        invalidate();
    }

    public void setModo(int nuevoModo) {
        this.modo = nuevoModo;
    }

    public PointF getUltimoStart() {
        return startPoints.isEmpty() ? null : startPoints.get(startPoints.size() - 1);
    }

    public PointF getUltimoEnd() {
        return endPoints.isEmpty() ? null : endPoints.get(endPoints.size() - 1);
    }

    // Método para detectar si se tocó cerca de una línea
    public int detectarToqueEnLinea(float x, float y, float tolerancia) {
        Log.d(TAG, "detectarToqueEnLinea - Coords: (" + x + ", " + y + ") | Tolerancia: " + tolerancia);
        Log.d(TAG, "Cantidad de líneas guardadas: " + startPoints.size());

        for (int i = 0; i < startPoints.size(); i++) {
            PointF start = startPoints.get(i);
            PointF end = endPoints.get(i);

            float distancia = distanciaPuntoALinea(x, y, start.x, start.y, end.x, end.y);

            Log.d(TAG, "Línea " + i + " -> Start(" + start.x + "," + start.y +
                    ") End(" + end.x + "," + end.y + ") | Distancia: " + distancia);

            if (distancia <= tolerancia) {
                Log.d(TAG, "✅ LÍNEA " + i + " DETECTADA (distancia: " + distancia + " <= " + tolerancia + ")");
                return i; // Retorna el índice de la línea tocada
            }
        }
        Log.d(TAG, "❌ Ninguna línea detectada dentro de la tolerancia");
        return -1; // No se tocó ninguna línea
    }

    // Calcula la distancia de un punto a una línea
    private float distanciaPuntoALinea(float px, float py, float x1, float y1, float x2, float y2) {
        float A = px - x1;
        float B = py - y1;
        float C = x2 - x1;
        float D = y2 - y1;

        float dot = A * C + B * D;
        float len_sq = C * C + D * D;
        float param = -1;

        if (len_sq != 0) {
            param = dot / len_sq;
        }

        float xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        float dx = px - xx;
        float dy = py - yy;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public void eliminarCota(int indice) {
        if (indice >= 0 && indice < startPoints.size()) {
            startPoints.remove(indice);
            endPoints.remove(indice);
            textosCotas.remove(indice);
            invalidate();
            Log.d(TAG, "Cota eliminada en índice: " + indice);
        }
    }
}