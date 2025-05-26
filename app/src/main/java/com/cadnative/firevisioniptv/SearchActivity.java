package com.cadnative.firevisioniptv;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.SearchSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.cadnative.firevisioniptv.DetailsActivity;
import com.cadnative.firevisioniptv.Movie;
import com.cadnative.firevisioniptv.PlaybackActivity;
import com.cadnative.firevisioniptv.R;
import com.cadnative.firevisioniptv.SearchResultProvider;

/**
 * Actividad que contiene un fragmento de búsqueda (SearchSupportFragment)
 * para permitir la búsqueda de contenido y manejar la selección de resultados.
 */
public class SearchActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establecer el layout de la actividad con el fragmento de búsqueda
        setContentView(R.layout.activity_search);

        // Obtener la instancia del fragmento de búsqueda desde el layout
        SearchSupportFragment searchFragment = (SearchSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.search_fragment);

        // Definir qué hacer cuando un ítem en los resultados es clickeado
        searchFragment.setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                      RowPresenter.ViewHolder rowViewHolder, Row row) {
                // Comprobar si el ítem seleccionado es un objeto Movie
                if (item instanceof Movie) {
                    Movie movie = (Movie) item;

                    // Crear un Intent para abrir PlaybackActivity y reproducir el canal seleccionado
                    Intent intent = new Intent(SearchActivity.this, PlaybackActivity.class);

                    // Pasar el objeto Movie seleccionado a la nueva actividad con clave DetailsActivity.MOVIE
                    intent.putExtra(DetailsActivity.MOVIE, movie);

                    // Iniciar la actividad PlaybackActivity
                    startActivity(intent);
                }
            }
        });

        // Establecer el proveedor de resultados para el fragmento de búsqueda,
        // que se encarga de filtrar y mostrar los resultados de búsqueda
        searchFragment.setSearchResultProvider(new SearchResultProvider());
    }
}
