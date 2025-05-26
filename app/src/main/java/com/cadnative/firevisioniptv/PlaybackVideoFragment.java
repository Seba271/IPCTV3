package com.cadnative.firevisioniptv;

import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.media.MediaPlayerAdapter;
import androidx.leanback.widget.PlaybackControlsRow;

import java.util.List;

/**
 * Fragmento que extiende VideoSupportFragment para reproducir videos (canales)
 * y controlar la navegación entre canales con controles personalizados.
 */
public class PlaybackVideoFragment extends VideoSupportFragment {

    // Glue para controlar la reproducción y la interfaz del reproductor
    private ChannelPlaybackTransportControlGlue<MediaPlayerAdapter> mTransportControlGlue;

    // Lista de canales disponibles (representados como objetos Movie)
    private List<Movie> mChannels;

    // Índice del canal actualmente reproducido en la lista
    private int mCurrentChannelIndex;

    /**
     * Método llamado al crear el fragmento.
     * Inicializa el reproductor, obtiene el canal actual y configura la pantalla.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener el objeto Movie enviado desde la actividad (canal seleccionado)
        final Movie movie = (Movie) getActivity().getIntent().getSerializableExtra(DetailsActivity.MOVIE);

        // Obtener la lista completa de canales y buscar el índice del canal actual
        mChannels = MovieList.list;
        mCurrentChannelIndex = mChannels.indexOf(movie);

        // Crear el host para los controles de video (Leanback)
        VideoSupportFragmentGlueHost glueHost = new VideoSupportFragmentGlueHost(PlaybackVideoFragment.this);

        // Crear el adaptador de reproducción multimedia
        MediaPlayerAdapter playerAdapter = new MediaPlayerAdapter(getContext());

        // Desactivar la repetición automática
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE);

        // Crear el "glue" que conecta el adaptador de video con la UI y control de transporte
        mTransportControlGlue = new ChannelPlaybackTransportControlGlue<>(getContext(), playerAdapter, this);

        // Asociar el host a este glue
        mTransportControlGlue.setHost(glueHost);

        // Actualizar la UI y el reproductor con la información del canal actual
        updateChannelInfo(movie);

        // Mantener la pantalla encendida mientras se reproduce video
        Window window = getActivity().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        window.setAttributes(params);
    }

    /**
     * Actualiza la información del canal actual en la UI y configura la fuente de video.
     * @param movie objeto Movie que representa el canal a reproducir
     */
    private void updateChannelInfo(Movie movie) {
        mTransportControlGlue.setTitle(movie.getTitle());          // Título del canal
        mTransportControlGlue.setSubtitle(movie.getDescription());  // Descripción o subtítulo
        mTransportControlGlue.getPlayerAdapter().setDataSource(Uri.parse(movie.getVideoUrl())); // Fuente del video
        mTransportControlGlue.playWhenPrepared();                   // Reproducir automáticamente cuando esté listo
    }

    /**
     * Cambia al siguiente canal en la lista (circular).
     */
    public void nextChannel() {
        mCurrentChannelIndex = (mCurrentChannelIndex + 1) % mChannels.size();
        updateChannelInfo(mChannels.get(mCurrentChannelIndex));
    }

    /**
     * Cambia al canal anterior en la lista (circular).
     */
    public void previousChannel() {
        mCurrentChannelIndex = (mCurrentChannelIndex - 1 + mChannels.size()) % mChannels.size();
        updateChannelInfo(mChannels.get(mCurrentChannelIndex));
    }

    /**
     * Maneja eventos de tecla para cambiar canales con botones físicos o controles remotos.
     * @param keyCode código de la tecla presionada
     * @param event evento de tecla
     * @return true si el evento fue manejado, false si no
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_CHANNEL_UP:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                nextChannel();    // Cambiar al canal siguiente
                return true;
            case KeyEvent.KEYCODE_CHANNEL_DOWN:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                previousChannel(); // Cambiar al canal anterior
                return true;
            default:
                return false;     // No manejado aquí
        }
    }

    /**
     * Cuando el fragmento se pausa, se pausa la reproducción y se permite que la pantalla se apague.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mTransportControlGlue != null) {
            mTransportControlGlue.pause();  // Pausar reproducción
        }

        // Permitir que la pantalla se apague (quitar flag KEEP_SCREEN_ON)
        Window window = getActivity().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        window.setAttributes(params);
    }

    /**
     * Cuando el fragmento se destruye, libera referencias y permite que la pantalla se apague.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTransportControlGlue != null) {
            mTransportControlGlue = null;  // Liberar referencia para ayudar GC
        }

        // Permitir que la pantalla se apague (quitar flag KEEP_SCREEN_ON)
        Window window = getActivity().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        window.setAttributes(params);
    }
}
