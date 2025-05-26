package com.cadnative.firevisioniptv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.app.DetailsSupportFragmentBackgroundController;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnActionClickedListener;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Collections;
import java.util.List;

/*
 * VideoDetailsFragment extiende DetailsSupportFragment,
 * utilizado para mostrar detalles de un video (película),
 * incluyendo información y opciones de acción (ver tráiler, comprar, rentar).
 */
public class VideoDetailsFragment extends DetailsSupportFragment {
    private static final String TAG = "VideoDetailsFragment";

    // Constantes para los IDs de acciones disponibles en el detalle
    private static final int ACTION_WATCH_TRAILER = 1;
    private static final int ACTION_RENT = 2;
    private static final int ACTION_BUY = 3;

    // Dimensiones para la miniatura del detalle en dp
    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private static final int NUM_COLS = 10;  // Número de columnas para lista relacionada (comentada)

    // Objeto Movie seleccionado cuyos detalles se mostrarán
    private Movie mSelectedMovie;

    // Adaptador para contener filas con diferentes tipos de datos
    private ArrayObjectAdapter mAdapter;

    // Selector para asociar cada tipo de fila con su Presenter correspondiente
    private ClassPresenterSelector mPresenterSelector;

    // Controlador para el fondo con efectos visuales en la pantalla de detalles
    private DetailsSupportFragmentBackgroundController mDetailsBackground;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        // Inicializa el controlador para el fondo del fragmento detalles
        mDetailsBackground = new DetailsSupportFragmentBackgroundController(this);

        // Obtiene la película seleccionada pasada desde el Intent
        mSelectedMovie =
                (Movie) getActivity().getIntent().getSerializableExtra(DetailsActivity.MOVIE);

        // Si hay película seleccionada, inicializa los componentes para mostrar detalles
        if (mSelectedMovie != null) {
            mPresenterSelector = new ClassPresenterSelector();
            mAdapter = new ArrayObjectAdapter(mPresenterSelector);

            setupDetailsOverviewRow();         // Crea fila con detalles y acciones
            setupDetailsOverviewRowPresenter(); // Configura la apariencia y comportamiento
            setupRelatedMovieListRow();          // (Comentado) Configura lista de películas relacionadas

            setAdapter(mAdapter);               // Asocia el adaptador al fragmento

            initializeBackground(mSelectedMovie); // Carga y muestra el fondo con efecto parallax

            setOnItemViewClickedListener(new ItemViewClickedListener()); // Listener para clics en ítems
        } else {
            // Si no hay película, redirige al MainActivity (pantalla principal)
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Inicializa el fondo del fragmento con la imagen de fondo de la película,
     * usando Glide para cargar la imagen y efecto parallax.
     */
    private void initializeBackground(Movie data) {
        mDetailsBackground.enableParallax();

        Glide.with(getActivity())
                .asBitmap()
                .centerCrop()
                .error(R.drawable.default_background)  // Imagen por defecto si falla la carga
                .load(data.getBackgroundImageUrl())    // URL de la imagen de fondo
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap,
                                                @Nullable Transition<? super Bitmap> transition) {
                        // Cuando la imagen está lista, se establece como fondo
                        mDetailsBackground.setCoverBitmap(bitmap);
                        // Notifica al adaptador que los datos cambiaron para refrescar UI
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });
    }

    /**
     * Configura la fila que muestra los detalles principales de la película
     * (imagen, título, descripción y acciones disponibles)
     */
    private void setupDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + mSelectedMovie.toString());

        // Crea una fila para mostrar detalles usando el objeto Movie seleccionado
        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);

        // Establece una imagen por defecto mientras carga la imagen real
        row.setImageDrawable(
                ContextCompat.getDrawable(getContext(), R.drawable.default_background));

        // Convierte las dimensiones de la miniatura de dp a píxeles según densidad pantalla
        int width = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);

        // Carga la imagen de la tarjeta (card) de la película y la asigna al row cuando esté lista
        Glide.with(getActivity())
                .load(mSelectedMovie.getCardImageUrl())
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<Drawable>(width, height) {
                    @Override
                    public void onResourceReady(@NonNull Drawable drawable,
                                                @Nullable Transition<? super Drawable> transition) {
                        Log.d(TAG, "details overview card image url ready: " + drawable);
                        row.setImageDrawable(drawable);
                        // Actualiza la UI notificando cambios en el adaptador
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });

        // Crea un adaptador para las acciones (botones) que se mostrarán en la fila detalle
        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();

        // Añade acciones: Ver tráiler, Rentar y Comprar, con etiquetas y descripciones
        actionAdapter.add(
                new Action(
                        ACTION_WATCH_TRAILER,
                        getResources().getString(R.string.watch_trailer_1),
                        getResources().getString(R.string.watch_trailer_2)));
        actionAdapter.add(
                new Action(
                        ACTION_RENT,
                        getResources().getString(R.string.rent_1),
                        getResources().getString(R.string.rent_2)));
        actionAdapter.add(
                new Action(
                        ACTION_BUY,
                        getResources().getString(R.string.buy_1),
                        getResources().getString(R.string.buy_2)));

        // Asocia las acciones creadas a la fila de detalles
        row.setActionsAdapter(actionAdapter);

        // Agrega la fila al adaptador principal
        mAdapter.add(row);
    }

    /**
     * Configura el presentador para la fila de detalles,
     * incluyendo el color de fondo, transición compartida y el manejo de acciones.
     */
    private void setupDetailsOverviewRowPresenter() {
        // Crea un presentador para mostrar la fila detalle a pantalla completa
        FullWidthDetailsOverviewRowPresenter detailsPresenter =
                new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());

        // Establece el color de fondo cuando la fila está seleccionada
        detailsPresenter.setBackgroundColor(
                ContextCompat.getColor(getContext(), R.color.selected_background));

        // Configura la transición compartida para animar el cambio entre pantallas
        FullWidthDetailsOverviewSharedElementHelper sharedElementHelper =
                new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(
                getActivity(), DetailsActivity.SHARED_ELEMENT_NAME);
        detailsPresenter.setListener(sharedElementHelper);
        detailsPresenter.setParticipatingEntranceTransition(true);

        // Define el comportamiento cuando se hace clic en una acción (botón)
        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if (action.getId() == ACTION_WATCH_TRAILER) {
                    // Al pulsar "Ver tráiler", se lanza PlaybackActivity para reproducir video
                    Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                    intent.putExtra(DetailsActivity.MOVIE, mSelectedMovie);
                    startActivity(intent);
                } else {
                    // Para otras acciones, muestra un mensaje Toast con el texto de la acción
                    Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Añade el presentador para filas DetailsOverviewRow al selector
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    /**
     * Configura una fila con películas relacionadas para mostrar al final de la pantalla de detalles.
     * Actualmente está comentado y no activo.
     */
    private void setupRelatedMovieListRow() {
        // Código comentado que configuraría una lista de películas relacionadas con tarjetas
        // con una fila adicional en la pantalla de detalles.
        // Por ahora está desactivado.

        /*
        String subcategories[] = {getString(R.string.related_movies)};
        List<Movie> list = MovieList.getList();

        Collections.shuffle(list);
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (int j = 0; j < NUM_COLS; j++) {
            listRowAdapter.add(list.get(j % list.size()));
        }

        HeaderItem header = new HeaderItem(0, subcategories[0]);
        mAdapter.add(new ListRow(header, listRowAdapter));

        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());

        setOnItemViewClickedListener(new ItemViewClickedListener());
        */
    }

    /**
     * Convierte un valor en dp (density-independent pixels) a píxeles,
     * según la densidad de pantalla del dispositivo.
     */
    public static int convertDpToPixel(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    /**
     * Listener para clicks en los items de la lista.
     * Actualmente maneja la navegación cuando se clickea una película.
     */
    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                Log.d(TAG, "ItemViewClickedListener - onItemClicked: " + movie.getTitle());

                // Se lanza DetailsActivity para mostrar detalles de la película clickeada,
                // con animación de transición compartida
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE, movie);

                Bundle bundle =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        getActivity(),
                                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                                        DetailsActivity.SHARED_ELEMENT_NAME)
                                .toBundle();
                getActivity().startActivity(intent, bundle);
            }
        }
    }
}
