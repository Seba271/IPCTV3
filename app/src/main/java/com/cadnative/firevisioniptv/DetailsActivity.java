// Paquete principal de la aplicación
package com.cadnative.firevisioniptv;

// Importaciones necesarias para trabajar con actividades y fragmentos
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

/**
 * Actividad que actúa como contenedor para mostrar los detalles de un canal o contenido.
 * Esta actividad carga un fragmento basado en Leanback (interfaz para Android TV).
 */
public class DetailsActivity extends FragmentActivity {

    // Constante usada para transiciones compartidas (por ejemplo, animaciones entre actividades)
    public static final String SHARED_ELEMENT_NAME = "hero";

    // Clave para pasar un objeto tipo "Movie" a esta actividad mediante un Intent
    public static final String MOVIE = "Movie";

    /**
     * Método que se llama cuando se crea por primera vez la actividad.
     * Aquí se configura el layout y se carga el fragmento correspondiente.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Llama al método de la clase base

        // Asigna el layout XML a esta actividad. Este layout contiene un contenedor para el fragmento de detalles.
        setContentView(R.layout.activity_details);

        // Si la actividad se está creando por primera vez (no se está restaurando desde un estado guardado)
        if (savedInstanceState == null) {
            // Reemplaza el contenedor con ID 'details_fragment' por una instancia de VideoDetailsFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_fragment, new VideoDetailsFragment())
                    .commitNow(); // Ejecuta inmediatamente la transacción
        }
    }
}
