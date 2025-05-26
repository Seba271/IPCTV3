package com.cadnative.firevisioniptv;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.fragment.app.FragmentActivity;

/**
 * Activity que carga el fragmento PlaybackVideoFragment
 * y maneja eventos de teclas para la navegación entre canales.
 */
public class PlaybackActivity extends FragmentActivity {

    // Referencia al fragmento que reproduce el video
    private PlaybackVideoFragment mPlaybackVideoFragment;

    /**
     * Método llamado cuando se crea la actividad.
     * Aquí se crea o recupera el fragmento PlaybackVideoFragment para mostrarlo.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Si es la primera creación, instanciamos el fragmento nuevo
            mPlaybackVideoFragment = new PlaybackVideoFragment();

            // Reemplazamos el contenido de la actividad con el fragmento
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, mPlaybackVideoFragment)
                    .commit();
        } else {
            // Si la actividad se recrea (ejemplo: rotación), recuperamos el fragmento existente
            mPlaybackVideoFragment = (PlaybackVideoFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }

    /**
     * Método que captura eventos de pulsación de teclas.
     * Se usa para manejar teclas específicas para navegar entre canales.
     *
     * @param keyCode código de la tecla presionada
     * @param event información del evento de tecla
     * @return true si el evento fue manejado, false si no
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mPlaybackVideoFragment != null) {
            // Interceptamos algunas teclas específicas para controlar la navegación entre canales
            switch (keyCode) {
                // Para teclas que avanzan al siguiente canal
                case KeyEvent.KEYCODE_CHANNEL_UP:
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    mPlaybackVideoFragment.nextChannel();  // Cambia al canal siguiente
                    return true;

                // Para teclas que retroceden al canal anterior
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    mPlaybackVideoFragment.previousChannel();  // Cambia al canal anterior
                    return true;
            }
        }

        // Si no manejamos la tecla, se pasa al comportamiento por defecto
        return super.onKeyDown(keyCode, event);
    }
}
