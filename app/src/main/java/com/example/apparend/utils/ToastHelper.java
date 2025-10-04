package com.example.apparend.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastHelper {

    public static void showShortToast(Context context, String message, int durationMs) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();

        // Cancelar después del tiempo personalizado
        new Handler().postDelayed(toast::cancel, durationMs);
    }
}



// En tu actividad:
//ToastHelper.showShortToast(this, "Modo cotas activado", 1000);