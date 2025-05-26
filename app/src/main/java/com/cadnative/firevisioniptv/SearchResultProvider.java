package com.cadnative.firevisioniptv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.leanback.app.SearchSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import java.util.ArrayList;
import java.util.List;

import com.cadnative.firevisioniptv.CardPresenter;
import com.cadnative.firevisioniptv.DetailsActivity;
import com.cadnative.firevisioniptv.PlaybackActivity;
import com.cadnative.firevisioniptv.Movie;
import com.cadnative.firevisioniptv.MovieList;
import com.cadnative.firevisioniptv.FirevisionApplication;

/**
 * Clase que implementa SearchResultProvider para manejar los resultados de búsqueda
 * dentro del fragmento SearchSupportFragment de Leanback.
 */
public class SearchResultProvider implements SearchSupportFragment.SearchResultProvider {

    // Adaptador que contiene las filas de resultados a mostrar en la búsqueda
    private ArrayObjectAdapter mRowsAdapter;

    // Lista completa de películas/canales cargada para filtrar búsquedas
    private List<Movie> mAllMovies;

    /**
     * Constructor, inicializa el adaptador de filas y carga la lista completa de películas
     */
    SearchResultProvider() {
        // Crea un adaptador para filas con un presentador para listas (ListRowPresenter)
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        // Carga la lista completa de películas usando el AssetManager de la app
        mAllMovies = MovieList.setupMovies(FirevisionApplication.getAppContext().getAssets());
    }

    /**
     * Devuelve el adaptador que contiene los resultados que serán mostrados en el fragmento de búsqueda
     */
    @Override
    public ObjectAdapter getResultsAdapter() {
        return mRowsAdapter;
    }

    /**
     * Se llama cuando cambia el texto de búsqueda.
     * Aquí se actualizan los resultados filtrados según el texto ingresado.
     * @param newQuery texto actual del cuadro de búsqueda
     * @return true indica que se manejó el evento
     */
    @Override
    public boolean onQueryTextChange(String newQuery) {
        loadQueryResults(newQuery);
        return true;
    }

    /**
     * Se llama cuando se confirma la búsqueda (por ejemplo, se presiona Enter).
     * Se actualizan los resultados con el texto enviado.
     * @param query texto confirmado para búsqueda
     * @return true indica que se manejó el evento
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        loadQueryResults(query);
        return true;
    }

    /**
     * Método interno para filtrar la lista completa de películas con base en la consulta.
     * Luego actualiza el adaptador para mostrar los resultados en la interfaz.
     * @param query texto a buscar en los títulos de las películas
     */
    private void loadQueryResults(String query) {
        // Limpiar los resultados previos
        mRowsAdapter.clear();

        // Lista temporal para almacenar las coincidencias con la consulta
        List<Movie> results = new ArrayList<>();

        // Filtrar películas que contengan el texto de búsqueda (sin importar mayúsculas/minúsculas)
        for (Movie movie : mAllMovies) {
            if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                results.add(movie);
            }
        }

        // Crear un encabezado para la lista de resultados
        HeaderItem header = new HeaderItem("Search Results");

        // Adaptador para las tarjetas que representan cada película en los resultados
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());

        // Agregar todas las películas encontradas al adaptador de tarjetas
        listRowAdapter.addAll(0, results);

        // Agregar una fila al adaptador principal con el encabezado y las tarjetas de resultados
        mRowsAdapter.add(new ListRow(header, listRowAdapter));
    }
}
