package com.example.petfriendsapp.components;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Handler;

import com.example.petfriendsapp.R;

public class LoadingDialog extends AlertDialog {
    private static final int MINIMUM_DISPLAY_TIME = 15000; // Duración mínima de visualización en milisegundos
    private final Handler handler;
    private final Runnable dismissRunnable;

    public LoadingDialog(Context context) {
        super(context);
        handler = new Handler();
        dismissRunnable = this::dismiss;
        initDialog(context);
    }

    private void initDialog(Context context) {
        // Inflar el layout custom_dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_dialog, null);

        // Configurar el diálogo
        setView(view);
        setCancelable(false); // No se puede cancelar tocando fuera del diálogo

        // Iniciar temporizador para asegurar un tiempo mínimo de visualización
        handler.postDelayed(dismissRunnable, MINIMUM_DISPLAY_TIME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void show() {
        super.show();
        // Si el diálogo ya está visible, restablecer el temporizador
        handler.removeCallbacks(dismissRunnable);
        handler.postDelayed(dismissRunnable, MINIMUM_DISPLAY_TIME);
    }

    @Override
    public void dismiss() {
        // Cancelar el temporizador antes de cerrar el diálogo
        handler.removeCallbacks(dismissRunnable);
        super.dismiss();
    }
}
