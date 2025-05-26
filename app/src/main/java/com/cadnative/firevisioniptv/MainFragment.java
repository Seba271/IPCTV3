package com.cadnative.firevisioniptv;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ListRowView;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.leanback.widget.VerticalGridView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class MainFragment extends BrowseSupportFragment {
    // TAG para logs
    private static final String TAG = "MainFragment";

    // Constantes para la actualización del fondo, tamaño de items y máximo columnas en grid
    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;
    private static final int MAX_NUM_COLS =5;

    // Handler para ejecutar tareas en el hilo UI
    private final Handler mHandler = new Handler(Looper.myLooper());
    private Drawable mDefaultBackground; // Drawable por defecto para fondo
    private DisplayMetrics mMetrics;     // Para obtener tamaño pantalla
    private Timer mBackgroundTimer;      // Temporizador para actualizar fondo
    private String mBackgroundUri;       // Uri de la imagen actual de fondo
    private BackgroundManager mBackgroundManager;  // Manager para el fondo

    private AssetManager assetManager;   // Para manejar recursos dentro de assets

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");

        // Obtener el AssetManager para acceder a archivos dentro de assets
        assetManager = getContext().getAssets();

        super.onActivityCreated(savedInstanceState);

        // Preparar manager de fondo
        prepareBackgroundManager();

        // Configurar elementos UI
        setupUIElements();

        // Cargar las filas con los datos
        loadRows();

        // Configurar listeners para eventos de UI
        setupEventListeners();

        // Opcional: seleccionar primer item después de cargar filas (comentado)
        // mHandler.postDelayed(() -> selectFirstItem(), 500);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancelar el timer del fondo para evitar leaks
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }

    /**
     * Método que carga las filas en el BrowseSupportFragment.
     * Agrupa películas por su grupo, las ordena alfabéticamente y las divide en filas con máximo 5 columnas.
     */
    private void loadRows() {
        // Obtener lista completa de películas desde assets
        List<Movie> list = MovieList.setupMovies(assetManager);
        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter();

        // Usar TreeMap para ordenar grupos alfabéticamente
        Map<String, List<Movie>> groupedMovies = new TreeMap<>();

        // Agrupar películas por su grupo
        for (Movie movie : list) {
            String group = movie.getGroup();
            if (group == null || group.isEmpty()) {
                group = "zzz_other";  // Grupo "otros" para que quede al final
            }
            if (!groupedMovies.containsKey(group)) {
                groupedMovies.put(group, new ArrayList<>());
            }
            groupedMovies.get(group).add(movie);
        }

        // Extraer el grupo "otros" para manejarlo separado
        List<Movie> otherMovies = groupedMovies.remove("zzz_other");

        // Iterar cada grupo para crear filas de hasta MAX_NUM_COLS columnas
        for (Map.Entry<String, List<Movie>> entry : groupedMovies.entrySet()) {
            String group = entry.getKey();
            List<Movie> moviesInGroup = entry.getValue();

            // Número de filas para este grupo
            int numRows = (moviesInGroup.size() + MAX_NUM_COLS - 1) / MAX_NUM_COLS;

            // Crear cada fila
            for (int i = 0; i < numRows; i++) {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);

                // Calcular índices inicio y fin de películas para esta fila
                int startIndex = i * MAX_NUM_COLS;
                int endIndex = Math.min((i + 1) * MAX_NUM_COLS, moviesInGroup.size());

                // Añadir las películas a la fila
                listRowAdapter.addAll(0, moviesInGroup.subList(startIndex, endIndex));

                // Crear texto para el encabezado de la fila
                String headerText = group;
                if (numRows > 1) {
                    int firstMovieIndex = startIndex + 1;  // índice 1-based
                    int lastMovieIndex = endIndex;
                    headerText += " (" + firstMovieIndex + "-" + lastMovieIndex + ")";
                } else {
                    headerText += " (" + moviesInGroup.size() + ")";
                }

                // Crear HeaderItem y agregar la fila al adapter principal
                HeaderItem header = new HeaderItem(0, headerText);
                rowsAdapter.add(new ListRow(header, listRowAdapter));
            }
        }

        // Manejar el grupo "otros" igual que los demás
        if (otherMovies != null && !otherMovies.isEmpty()) {
            int numRows = (otherMovies.size() + MAX_NUM_COLS - 1) / MAX_NUM_COLS;

            for (int i = 0; i < numRows; i++) {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);

                int startIndex = i * MAX_NUM_COLS;
                int endIndex = Math.min((i + 1) * MAX_NUM_COLS, otherMovies.size());

                listRowAdapter.addAll(0, otherMovies.subList(startIndex, endIndex));

                String headerText = "Other";
                if (numRows > 1) {
                    int firstMovieIndex = startIndex + 1;
                    int lastMovieIndex = endIndex;
                    headerText += " (" + firstMovieIndex + "-" + lastMovieIndex + ")";
                } else {
                    headerText += " (" + otherMovies.size() + ")";
                }

                HeaderItem header = new HeaderItem(0, headerText);
                rowsAdapter.add(new ListRow(header, listRowAdapter));
            }
        }

        // Se podría agregar aquí filas extras con otras configuraciones, pero están comentadas

        // Asignar el adapter con todas las filas a la vista
        setAdapter(rowsAdapter);

        // Seleccionar la primera fila y el primer item si existen
        if (rowsAdapter.size() > 0) {
            ListRow firstRow = (ListRow) rowsAdapter.get(0);
            if (firstRow != null && firstRow.getAdapter() != null && firstRow.getAdapter().size() > 0) {
                Object firstItem = firstRow.getAdapter().get(0);
                if (firstItem != null) {
                    setSelectedPosition(0); // Seleccionar la primera fila
                }
            }
        }
    }

    /**
     * Prepara el BackgroundManager para manejar el fondo de pantalla
     */
    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        // Drawable por defecto para fondo si no se carga ninguno
        mDefaultBackground = ContextCompat.getDrawable(getContext(), R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    /**
     * Configura elementos UI como título, colores y estado de headers
     */
    private void setupUIElements() {
        // Obtener versión de la app para mostrar en título
        String appVersion = getAppVersion(requireContext());
        setTitle(getString(R.string.browse_title) + " (v" + appVersion + ")");

        setHeadersState(HEADERS_HIDDEN); // Oculta headers
        setHeadersTransitionOnBackEnabled(true); // Permite transición al presionar atrás

        // Establece color de fondo y del icono de búsqueda
        setBrandColor(ContextCompat.getColor(getContext(), R.color.fastlane_background));
        setSearchAffordanceColor(ContextCompat.getColor(getContext(), R.color.search_opaque));
    }

    /**
     * Obtiene la versión de la aplicación desde el PackageManager
     * @param context Contexto de la app
     * @return Versión como String, o "Unknown" si no se encuentra
     */
    private String getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Si no encuentra versión, devuelve Unknown
            return "Unknown";
        }
    }

    /**
     * Configura los listeners para eventos de búsqueda y selección de ítems
     */
    private void setupEventListeners() {
        // Al hacer clic en el icono de búsqueda, abrir actividad SearchActivity
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        // Listener para cuando se clickea un item
        setOnItemViewClickedListener(new ItemViewClickedListener());

        // Listener para cuando se selecciona un item (cambia el fondo)
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    /**
     * Actualiza el fondo con la imagen del URI dado usando Glide
     * @param uri URL de la imagen de fondo
     */
    private void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;

        // Si el URI es diferente al actual, cargar nueva imagen
        if (uri != null && !uri.equals(mBackgroundUri)) {
            mBackgroundUri = uri;

            Glide.with(getContext())
                    .load(uri)
                    .centerCrop()
                    .error(mDefaultBackground)
                    .into(new SimpleTarget<Drawable>(width, height) {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource,
                                                    @Nullable Transition<? super Drawable> transition) {
                            mBackgroundManager.setDrawable(resource);
                        }
                    });
        }
    }

    /**
     * Listener para eventos de click en items
     */
    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                // Crear intent para detalle y pasar extras con info del item
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE, movie);

                // Crear animación de transición
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();

                getActivity().startActivity(intent, bundle);
            }
        }
    }

    /**
     * Listener para eventos de selección de items, para actualizar el fondo
     */
    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                String bgUri = movie.getBackgroundImageUrl();
                if (bgUri != null) {
                    updateBackground(bgUri);
                }
            }
        }
    }

    // Puedes agregar más métodos o clases internas según necesites, por ejemplo para adaptar los datos o gestionar otras funcionalidades.

}
