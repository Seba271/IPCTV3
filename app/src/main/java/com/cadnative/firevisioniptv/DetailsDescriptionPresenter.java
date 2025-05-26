// Define el paquete al que pertenece esta clase
package com.cadnative.firevisioniptv;

// Importa la clase base que permite personalizar las descripciones en la interfaz Leanback
import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;


public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Movie movie = (Movie) item;

        if (movie != null) {
            viewHolder.getTitle().setText(movie.getTitle());
            viewHolder.getSubtitle().setText(movie.getStudio());
            viewHolder.getBody().setText(movie.getDescription());
        }
    }
}