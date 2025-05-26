package com.cadnative.firevisioniptv;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Esta actividad muestra un ejemplo de cómo utilizar un ErrorFragment.
 * Se utiliza para simular una carga con un spinner y luego mostrar un mensaje de error.
 */
public class BrowseErrorActivity extends FragmentActivity {

    // Tiempo de espera (en milisegundos) antes de mostrar el error
    private static final int TIMER_DELAY = 3000;

    // Tamaño del spinner (barra de progreso circular)
    private static final int SPINNER_WIDTH = 100;
    private static final int SPINNER_HEIGHT = 100;

    // Referencias a los fragments que se mostrarán
    private ErrorFragment mErrorFragment;
    private SpinnerFragment mSpinnerFragment;

    /**
     * Método que se llama cuando se crea la actividad.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establece el layout XML principal
        setContentView(R.layout.activity_main);

        // Si no hay estado guardado (primera vez que se abre), carga el fragmento principal
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_browse_fragment, new MainFragment()) // reemplaza el contenedor con MainFragment
                    .commitNow();
        }

        // Simula un error después de mostrar un spinner de carga
        testError();
    }

    /**
     * Este método agrega un spinner, espera 3 segundos y luego muestra un fragmento de error.
     */
    private void testError() {
        // Crea y agrega el fragmento de error (aún no visible)
        mErrorFragment = new ErrorFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_browse_fragment, mErrorFragment)
                .commit();

        // Crea y agrega el fragmento del spinner (carga en curso)
        mSpinnerFragment = new SpinnerFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_browse_fragment, mSpinnerFragment)
                .commit();

        // Usa un Handler para retrasar la acción durante TIMER_DELAY (3 segundos)
        final Handler handler = new Handler(Looper.myLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Quita el spinner
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(mSpinnerFragment)
                        .commit();

                // Muestra el contenido de error (por ejemplo, mensaje de "no se pudo cargar")
                mErrorFragment.setErrorContent();
            }
        }, TIMER_DELAY);
    }

    /**
     * Clase interna que define un fragmento con un ProgressBar centrado (spinner de carga).
     */
    public static class SpinnerFragment extends Fragment {
        @Override
        public View onCreateView(
                LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            // Crea una barra de progreso circular
            ProgressBar progressBar = new ProgressBar(container.getContext());

            // Si el contenedor es un FrameLayout, centra el spinner
            if (container instanceof FrameLayout) {
                FrameLayout.LayoutParams layoutParams =
                        new FrameLayout.LayoutParams(SPINNER_WIDTH, SPINNER_HEIGHT, Gravity.CENTER);
                progressBar.setLayoutParams(layoutParams);
            }

            // Devuelve la vista del spinner para que se muestre en pantalla
            return progressBar;
        }
    }
}
