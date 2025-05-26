package com.cadnative.firevisioniptv;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/*
 * Clase principal (Activity) que extiende FragmentActivity.
 * Se encarga de inicializar Firebase y cargar el fragmento principal de la app.
 */
public class MainActivity extends FragmentActivity {

    // Etiqueta para logs
    private static final String TAG = "MainActivity";

    /**
     * Método llamado al crear la actividad.
     * Aquí se configura la vista principal y se inicializa Firebase.
     * También se agrega el fragmento MainFragment al contenedor de la UI.
     *
     * @param savedInstanceState Bundle que contiene el estado previo de la actividad
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establece el layout XML que define la UI de esta actividad
        setContentView(R.layout.activity_main);

        // Inicializa Firebase para esta aplicación, debe hacerse antes de usar Firebase
        FirebaseApp.initializeApp(this);

        // Si no hay estado previo guardado (es la primera vez que se crea la actividad)
        if (savedInstanceState == null) {
            // Reemplaza el contenedor (R.id.main_browse_fragment) con el fragmento MainFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_browse_fragment, new MainFragment())
                    .commitNow();  // commitNow asegura que el cambio se aplique inmediatamente
        }
    }
}
