package com.cadnative.firevisioniptv;

// Importación de clases necesarias
import static java.security.AccessController.getContext;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.leanback.widget.ImageCardView; // Vista especializada para tarjetas en Android TV
import androidx.leanback.widget.Presenter;     // Clase base para crear y vincular vistas
import androidx.core.content.ContextCompat;

import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide; // Librería para cargar imágenes desde URLs

/*
 * CardPresenter genera vistas tipo tarjeta y las vincula con datos (Movie).
 * Es común en aplicaciones Android TV para mostrar contenidos visuales.
 */
public class CardPresenter extends Presenter {
    private static final String TAG = "CardPresenter";

    // Dimensiones de las tarjetas
    private static final int CARD_WIDTH = 313;
    private static final int CARD_HEIGHT = 176;

    // Colores de fondo para la tarjeta seleccionada y no seleccionada
    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;

    // Imagen por defecto para las tarjetas sin imagen
    private Drawable mDefaultCardImage;

    // Cambia el color de fondo dependiendo si la tarjeta está seleccionada
    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        view.setBackgroundColor(color);
        view.setInfoAreaBackgroundColor(color); // cambia también el fondo de la sección de texto
    }

    // Se llama cuando el sistema necesita crear una nueva tarjeta (ViewHolder)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Log.d(TAG, "onCreateViewHolder");

        // Obtiene colores desde los recursos
        sDefaultBackgroundColor = ContextCompat.getColor(parent.getContext(), R.color.default_background);
        sSelectedBackgroundColor = ContextCompat.getColor(parent.getContext(), R.color.selected_background);

        // Imagen por defecto si no se carga la del Movie
        mDefaultCardImage = ContextCompat.getDrawable(parent.getContext(), R.drawable.movie);

        // Crea la tarjeta personalizada (ImageCardView) y sobreescribe su comportamiento al ser seleccionada
        ImageCardView cardView = new ImageCardView(parent.getContext()) {
            @Override
            public void setSelected(boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };

        // Permite que sea enfocada (útil en interfaces de TV)
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);

        // Le da un fondo inicial
        updateCardBackgroundColor(cardView, false);

        return new ViewHolder(cardView); // Devuelve el ViewHolder con esta tarjeta
    }

    // Vincula un objeto Movie con su vista (tarjeta)
    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Movie movie = (Movie) item; // Se castea el objeto recibido
        ImageCardView cardView = (ImageCardView) viewHolder.view;

        Log.d(TAG, "onBindViewHolder");

        if (movie.getCardImageUrl() != null) {
            // Configura el texto e imagen de la tarjeta
            cardView.setTitleText(movie.getTitle());
            cardView.setContentText(movie.getStudio());
            cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);

            // Espaciado dentro de la imagen
            cardView.getMainImageView().setPadding(20, 20, 20, 20);

            // Fondo en forma de gradiente (de arriba hacia abajo)
            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[] {
                            ContextCompat.getColor(viewHolder.view.getContext(), R.color.card_background_startColor),
                            ContextCompat.getColor(viewHolder.view.getContext(), R.color.card_background_endColor)
                    }
            );
            cardView.getMainImageView().setBackground(gradientDrawable);

            // Escalado de la imagen
            cardView.getMainImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);

            // Usa Glide para cargar la imagen desde una URL
            Glide.with(viewHolder.view.getContext())
                    .load(movie.getCardImageUrl())     // URL de la imagen
                    .fitCenter()                        // Ajusta al centro sin recortar
                    .error(mDefaultCardImage)          // Muestra imagen por defecto si hay error
                    .into(cardView.getMainImageView()); // Aplica la imagen a la tarjeta
        }
    }

    // Se llama cuando la vista ya no está en uso (por ejemplo, al desplazarse fuera de pantalla)
    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        Log.d(TAG, "onUnbindViewHolder");
        ImageCardView cardView = (ImageCardView) viewHolder.view;

        // Libera recursos de imágenes para ahorrar memoria
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
