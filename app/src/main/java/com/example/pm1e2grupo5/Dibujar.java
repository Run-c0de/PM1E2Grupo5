package com.example.pm1e2grupo5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;

public class Dibujar extends View {
    private Path drawPath; // El camino de dibujo
    public static Paint drawPaint; // La pintura utilizada para dibujar
    private Paint canvasPaint; // Pintura para el fondo del lienzo
    private int paintColor = 0xFF000000; // Color predeterminado del pincel (negro)
    private Canvas drawCanvas; // El lienzo de dibujo
    private Bitmap canvasBitmap; // La imagen del lienzo
    public boolean borrar = true; // Bandera para indicar si se está borrando o dibujando

    // Constructor
    public Dibujar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing(); // Configuración inicial del dibujo
    }

    // Método para configurar el dibujo
    private void setupDrawing() {
        drawPath = new Path(); // Inicializa el camino de dibujo
        drawPaint = new Paint(); // Inicializa la pintura de dibujo
        drawPaint.setColor(paintColor); // Establece el color de la pintura
        drawPaint.setAntiAlias(true); // Activa el suavizado de bordes
        drawPaint.setStrokeWidth(5); // Establece el ancho del trazo
        drawPaint.setStyle(Paint.Style.STROKE); // Establece el estilo de trazo
        drawPaint.setStrokeJoin(Paint.Join.ROUND); // Establece el tipo de unión de trazo
        drawPaint.setStrokeCap(Paint.Cap.ROUND); // Establece el tipo de extremo de trazo
        canvasPaint = new Paint(Paint.DITHER_FLAG); // Pintura del fondo del lienzo
        borrar = true; // Inicialmente se establece en borrar
    }

    // Método llamado cuando cambia el tamaño del View
    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Crea un nuevo bitmap para el lienzo con el nuevo tamaño
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        // Crea un nuevo lienzo de dibujo asociado al nuevo bitmap
        drawCanvas = new Canvas(canvasBitmap);
    }

    // Método llamado cuando se dibuja en el View
    @Override
    public void onDraw(Canvas canvas) {
        // Dibuja el bitmap del lienzo en el View
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        // Dibuja el camino de dibujo sobre el bitmap del lienzo
        canvas.drawPath(drawPath, drawPaint);
    }

    // Método llamado cuando ocurre un evento táctil en el View
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX(); // Posición X del evento táctil
        float touchY = event.getY(); // Posición Y del evento táctil

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // Cuando se toca la pantalla
                drawPath.moveTo(touchX, touchY); // Mueve el punto de inicio del camino
                break;
            case MotionEvent.ACTION_MOVE: // Cuando se mueve el dedo por la pantalla
                drawPath.lineTo(touchX, touchY); // Agrega una línea al camino
                break;
            case MotionEvent.ACTION_UP: // Cuando se levanta el dedo de la pantalla
                drawPath.lineTo(touchX, touchY); // Agrega una línea al camino
                drawCanvas.drawPath(drawPath, drawPaint); // Dibuja el camino en el lienzo
                drawPath.reset(); // Resetea el camino para el próximo dibujo
                break;
            default:
                return false;
        }
        invalidate(); // Invalida el View para que se redibuje
        borrar = false; // Se establece en falso ya que se está dibujando
        return true; // Se maneja el evento táctil
    }

    // Método para crear un nuevo dibujo (borrar el lienzo)
    public void limpiarFirma() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR); // Borra el lienzo
        invalidate(); // Invalida el View para que se redibuje
        borrar = true; // Se establece en verdadero ya que se está borrando
    }
    public String convertirFirmaABase64() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
