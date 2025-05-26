// Paquete de la app
package com.cadnative.firevisioniptv;

// Importación de clases necesarias para la reproducción y control en Android TV (Leanback)
import android.content.Context;
import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.PlaybackControlsRow;

/**
 * Esta clase extiende PlaybackTransportControlGlue y permite agregar
 * acciones personalizadas para controlar la reproducción de canales IPTV,
 * como saltar al canal siguiente o al canal anterior.
 *
 * @param <T> Tipo de PlayerAdapter (por ejemplo, ExoPlayerAdapter)
 */
public class ChannelPlaybackTransportControlGlue<T extends androidx.leanback.media.PlayerAdapter>
        extends PlaybackTransportControlGlue<T> {

    // Acción personalizada para saltar al siguiente canal
    private Action mSkipNextAction;

    // Acción personalizada para volver al canal anterior
    private Action mSkipPreviousAction;

    // Referencia al fragmento que contiene la lógica de cambio de canal
    private PlaybackVideoFragment mFragment;

    /**
     * Constructor que inicializa los botones personalizados y la referencia al fragmento.
     *
     * @param context   Contexto de la app
     * @param impl      Implementación del PlayerAdapter (por ejemplo, ExoPlayerAdapter)
     * @param fragment  Fragmento donde se implementan los métodos nextChannel y previousChannel
     */
    public ChannelPlaybackTransportControlGlue(Context context, T impl, PlaybackVideoFragment fragment) {
        super(context, impl); // Llama al constructor de la clase padre

        // Inicializa las acciones personalizadas para siguiente y anterior
        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(context);
        mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(context);

        // Guarda la referencia al fragmento para poder llamar a sus métodos
        mFragment = fragment;
    }

    /**
     * Método sobrescrito para agregar las acciones personalizadas
     * a la barra de control de reproducción.
     *
     * @param primaryActionsAdapter Adaptador de acciones principales
     */
    @Override
    protected void onCreatePrimaryActions(ArrayObjectAdapter primaryActionsAdapter) {
        super.onCreatePrimaryActions(primaryActionsAdapter); // Agrega controles predeterminados (play, pause, etc.)
        primaryActionsAdapter.add(mSkipPreviousAction); // Agrega el botón de canal anterior
        primaryActionsAdapter.add(mSkipNextAction);     // Agrega el botón de canal siguiente
    }

    /**
     * Método que llama a la función de cambiar al siguiente canal en el fragmento.
     */
    public void next() {
        mFragment.nextChannel();
    }

    /**
     * Método que llama a la función de volver al canal anterior en el fragmento.
     */
    public void previous() {
        mFragment.previousChannel();
    }

    /**
     * Maneja los clics en los botones de la barra de reproducción.
     * Si se hace clic en "siguiente" o "anterior", llama a los métodos correspondientes.
     * Si no, ejecuta la acción predeterminada.
     *
     * @param action La acción que se hizo clic
     */
    @Override
    public void onActionClicked(Action action) {
        if (action == mSkipNextAction) {
            next(); // Acción personalizada: siguiente canal
        } else if (action == mSkipPreviousAction) {
            previous(); // Acción personalizada: canal anterior
        } else {
            super.onActionClicked(action); // Cualquier otra acción (play, pause, etc.)
        }
    }
}
