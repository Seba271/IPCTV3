package com.cadnative.firevisioniptv;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.leanback.app.ErrorSupportFragment;

/*
 * Esta clase demuestra cómo extender ErrorSupportFragment para mostrar
 * una interfaz de error amigable en aplicaciones Android TV usando Leanback.
 */
public class ErrorFragment extends ErrorSupportFragment {
    // Etiqueta para los mensajes de log
    private static final String TAG = "ErrorFragment";
    // Define si el fondo del fragmento debe ser translúcido
    private static final boolean TRANSLUCENT = true;

    /**
     * Método llamado cuando el fragmento es creado por primera vez.
     * Se usa para configurar el título del fragmento y preparar recursos.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Registra en logcat la creación del fragmento para depuración
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        // Establece el título del fragmento con el nombre de la app desde strings.xml
        setTitle(getResources().getString(R.string.app_name));
    }

    /**
     * Método para configurar el contenido visual del error mostrado.
     * Aquí se define la imagen, mensaje, fondo, texto del botón y la acción del botón.
     */
    void setErrorContent() {
        // Establece un ícono de nube triste para mostrar el error
        setImageDrawable(ContextCompat.getDrawable(getContext(),
                androidx.leanback.R.drawable.lb_ic_sad_cloud));
        // Establece el mensaje de error que se mostrará, obtenido de strings.xml
        setMessage(getResources().getString(R.string.error_fragment_message));
        // Configura si el fondo debe ser translúcido o no
        setDefaultBackground(TRANSLUCENT);

        // Establece el texto del botón para cerrar el mensaje de error
        setButtonText(getResources().getString(R.string.dismiss_error));
        // Define el comportamiento al pulsar el botón: cerrar este fragmento
        setButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        // Elimina este fragmento de la pila de fragmentos, cerrando el error
                        getFragmentManager().beginTransaction()
                                .remove(ErrorFragment.this)
                                .commit();
                    }
                });
    }
}
