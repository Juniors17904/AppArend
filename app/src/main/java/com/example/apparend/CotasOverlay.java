//package com.example.apparend;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.PointF;
//import android.graphics.RectF;
//import android.graphics.drawable.Drawable;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.ImageView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CotasOverlay extends View {
//    private static final String TAG = "arenado Cotas Overlay";
//    private Paint paint = new Paint();
//    private PointF start, end;
//    private boolean drawingEnabled = false;
//    private List<PointF> startPoints = new ArrayList<>();
//    private List<PointF> endPoints = new ArrayList<>();
//
//
//
//    public CotasOverlay(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        paint.setColor(Color.RED);
//        paint.setStrokeWidth(5f);
//        paint.setTextSize(40f);
//    }
//
//    public void setDrawingEnabled(boolean enabled) {
//        drawingEnabled = enabled;
//        invalidate();
//    }
//
//
//    //control de toques
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (!drawingEnabled) return false;
//
//        // Buscar el ImageView de la imagen
//        ImageView imageView = getRootView().findViewById(R.id.imgVisor);
//        if (imageView == null || imageView.getDrawable() == null) return false;
//
//        // Calcular el área real de la imagen
//        RectF imageRect = getImageBounds(imageView);
//
//        // Log para depuración
//        Log.d(TAG, "Touch en: x=" + event.getX() + ", y=" + event.getY());
//        Log.d(TAG, "Área imagen: " + imageRect.toString());
//
//        // Ignorar toques fuera de la imagen (zona negra)
//        if (!imageRect.contains(event.getX(), event.getY())) {
//            Log.d(TAG, "Ignorado: toque fuera de la imagen");
//            return false;
//        }
//
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            start = new PointF(event.getX(), event.getY());
//            Log.d(TAG, "Inicio cota en: " + start.toString());
//        } else if (event.getAction() == MotionEvent.ACTION_UP) {
//            end = new PointF(event.getX(), event.getY());
//            Log.d(TAG, "Fin cota en: " + end.toString());
//
//            // GUARDAR LA COTA EN LAS LISTAS (NUEVO)
//            startPoints.add(start);
//            endPoints.add(end);
//
//            invalidate();
//        }
//        return true;
//    }
//
//
//    //dibujar lineas rojas
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        // DIBUJAR TODAS LAS COTAS ALMACENADAS (NUEVO)
//        for (int i = 0; i < startPoints.size(); i++) {
//            PointF s = startPoints.get(i);
//            PointF e = endPoints.get(i);
//
//            canvas.drawLine(s.x, s.y, e.x, e.y, paint);
//
//            // Texto en el medio de cada cota
//            float midX = (s.x + e.x) / 2;
//            float midY = (s.y + e.y) / 2;
//            canvas.drawText("25 cm", midX, midY, paint);
//
//            Log.d(TAG, "Dibujada cota " + i + ": " + s.toString() + " a " + e.toString());
//        }
//
//        // MANTENER TU CÓDIGO ORIGINAL (para la cota actual en progreso)
//        if (start != null && end != null) {
//            canvas.drawLine(start.x, start.y, end.x, end.y, paint);
//
//            // Texto en el medio
//            float midX = (start.x + end.x) / 2;
//            float midY = (start.y + end.y) / 2;
//            canvas.drawText("25 cm", midX, midY, paint);
//
//            Log.d(TAG, "Dibujada línea de " + start.toString() + " a " + end.toString());
//        }
//    }
//
//    // Método auxiliar: calcula el área real de la imagen dentro del ImageView
//    private RectF getImageBounds(ImageView imageView) {
//        RectF rect = new RectF();
//        Drawable drawable = imageView.getDrawable();
//        if (drawable == null) return rect;
//
//        Matrix matrix = imageView.getImageMatrix();
//        RectF drawableRect = new RectF(
//                0, 0,
//                drawable.getIntrinsicWidth(),
//                drawable.getIntrinsicHeight()
//        );
//        matrix.mapRect(rect, drawableRect);
//        return rect;
//    }
//
//
//
//
//
//
//    public void clearAllCotas() {
//        startPoints.clear();
//        endPoints.clear();
//        start = null;    // ← LIMPIAR COTA ACTUAL
//        end = null;      // ← LIMPIAR COTA ACTUAL
//        invalidate();    // ← FORZAR REDIBUJADO
//    }
//
//    public boolean hasCotas() {
//        return !startPoints.isEmpty();
//    }
//}
//




package com.example.apparend;

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

import java.util.ArrayList;
import java.util.List;

public class CotasOverlay extends View {
    private static final String TAG = "arenado Cotas Overlay";
    private Paint paint = new Paint();
    private PointF start, end;
    private boolean drawingEnabled = false;
    private List<PointF> startPoints = new ArrayList<>();
    private List<PointF> endPoints = new ArrayList<>();

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

    //control de toques
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (modoMedicionPrecisa) {
            // LÓGICA DE MEDICIÓN PRECISA
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    puntoInicialMedicion = new PointF(event.getX(), event.getY());
                    puntoActualTemporal = new PointF(event.getX(), event.getY());
                    Log.d(TAG, "Medición precisa - Punto inicial: " + puntoInicialMedicion.toString());
                    break;

                case MotionEvent.ACTION_MOVE:
                    puntoActualTemporal = new PointF(event.getX(), event.getY());
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    if (puntoInicialMedicion != null) {
                        PointF puntoFinal = new PointF(event.getX(), event.getY());
                        startPoints.add(puntoInicialMedicion);
                        endPoints.add(puntoFinal);
                        Log.d(TAG, "Medición precisa guardada: " + puntoInicialMedicion.toString() + " a " + puntoFinal.toString());
                        puntoInicialMedicion = null;
                        puntoActualTemporal = null;
                        invalidate();
                    }
                    break;
            }
            return true;
        }

        if (!drawingEnabled) return false;

        // LÓGICA ORIGINAL DE COTAS NORMALES
        ImageView imageView = getRootView().findViewById(R.id.imgVisor);
        if (imageView == null || imageView.getDrawable() == null) return false;

        RectF imageRect = getImageBounds(imageView);
        Log.d(TAG, "Touch en: x=" + event.getX() + ", y=" + event.getY());

        if (!imageRect.contains(event.getX(), event.getY())) {
            Log.d(TAG, "Ignorado: toque fuera de la imagen");
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            start = new PointF(event.getX(), event.getY());
            Log.d(TAG, "Inicio cota en: " + start.toString());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            end = new PointF(event.getX(), event.getY());
            Log.d(TAG, "Fin cota en: " + end.toString());
            startPoints.add(start);
            endPoints.add(end);
            invalidate();
        }
        return true;
    }

    //dibujar lineas rojas
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // DIBUJAR LÍNEA TEMPORAL EN MODO MEDICIÓN PRECISA
        if (modoMedicionPrecisa && puntoInicialMedicion != null && puntoActualTemporal != null) {
            canvas.drawLine(puntoInicialMedicion.x, puntoInicialMedicion.y,
                    puntoActualTemporal.x, puntoActualTemporal.y, paint);
        }

        // DIBUJAR TODAS LAS COTAS ALMACENADAS
        for (int i = 0; i < startPoints.size(); i++) {
            PointF s = startPoints.get(i);
            PointF e = endPoints.get(i);

            canvas.drawLine(s.x, s.y, e.x, e.y, paint);

            float midX = (s.x + e.x) / 2;
            float midY = (s.y + e.y) / 2;
            canvas.drawText("25 cm", midX, midY, paint);

            Log.d(TAG, "Dibujada cota " + i + ": " + s.toString() + " a " + e.toString());
        }

        // COTA ACTUAL EN PROGRESO
        if (start != null && end != null) {
            canvas.drawLine(start.x, start.y, end.x, end.y, paint);
            float midX = (start.x + end.x) / 2;
            float midY = (start.y + end.y) / 2;
            canvas.drawText("25 cm", midX, midY, paint);
            Log.d(TAG, "Dibujada línea de " + start.toString() + " a " + end.toString());
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
}