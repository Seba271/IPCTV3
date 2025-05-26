package com.cadnative.firevisioniptv;

import android.content.res.AssetManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
 * Clase utilitaria para manejar la lista de objetos Movie.
 * Proporciona métodos para cargar y preparar una lista de películas (o canales de video)
 * a partir de datos obtenidos, en este caso, desde un archivo local y Firebase.
 */
public final class MovieList {

    // Lista estática que almacena todos los objetos Movie generados
    static List<Movie> list;

    // Contador estático para asignar IDs únicos a cada Movie creado
    private static long count = 0;

    /*
     * Método público que devuelve la lista completa de películas.
     * Recibe un AssetManager para poder acceder a archivos locales necesarios.
     */
    public static List<Movie> getAllMovies(AssetManager assetManager) {
        return setupMovies(assetManager);
    }

    /*
     * Método que configura y crea la lista de películas.
     * Lee canales desde un archivo usando fileReader y les asigna imágenes de fondo
     * aleatorias de un arreglo predefinido.
     */
    public static List<Movie> setupMovies(AssetManager assetManager) {
        // Referencia a la base de datos Firebase para el nodo "channels"
        DatabaseReference channelsRef = FirebaseDatabase.getInstance().getReference("channels");

        // Inicializa la lista de películas vacía
        list = new ArrayList<>();

        // Arreglo con URLs de imágenes de fondo que se asignarán aleatoriamente a los videos
        String bgImageUrl[] = {
                "https://firebasestorage.googleapis.com/v0/b/firevisioniptv.appspot.com/o/bk1.jpg?alt=media&token=bc5dafeb-33a8-48d4-b283-6ff22bf3a7e5",
                "https://firebasestorage.googleapis.com/v0/b/firevisioniptv.appspot.com/o/bk3.png?alt=media&token=bd37b51c-7e9b-4500-9b62-11c98603e9b3",
                "https://firebasestorage.googleapis.com/v0/b/firevisioniptv.appspot.com/o/bk4.png?alt=media&token=30e5bfc3-f7b3-4d69-bdfd-71fa9ff9b789",
        };

        // Lee la lista de canales desde un archivo local usando fileReader
        List<Channel> listChannel = new fileReader().readFile(assetManager);

        // Itera sobre cada canal para crear un objeto Movie correspondiente
        for (int index = 0; index < listChannel.size(); ++index) {
            // Obtiene el nombre del canal
            String name= listChannel.get(index).getChannelName();

            // Si el nombre está vacío, usa el ID del canal como nombre
            if(name.isEmpty()){
                name= listChannel.get(index).getChannelId();
            }

            // Obtiene la imagen en miniatura del canal
            String cardImage = listChannel.get(index).getChannelImg();

            // Crea un nuevo Movie con los datos del canal y una imagen de fondo aleatoria
            list.add(
                    buildMovieInfo(
                            name,                                           // título
                            name,                                           // descripción (se usa igual que título)
                            listChannel.get(index).getChannelId(),         // estudio (se usa ID como studio)
                            listChannel.get(index).getChannelUrl(),        // URL del video
                            listChannel.get(index).getChannelGroup(),      // grupo o categoría
                            cardImage,                                      // imagen miniatura
                            bgImageUrl[new Random().nextInt(bgImageUrl.length)]  // imagen fondo aleatoria
                    )
            );
        }

        // Devuelve la lista completa de objetos Movie creada
        return list;
    }

    /*
     * Método privado auxiliar para construir un objeto Movie con todos sus datos.
     * Recibe parámetros detallados para llenar cada campo del objeto Movie.
     */
    private static Movie buildMovieInfo(
            String title,
            String description,
            String studio,
            String videoUrl,
            String group,
            String cardImageUrl,
            String backgroundImageUrl) {
        Movie movie = new Movie();

        // Asigna un ID único incremental
        movie.setId(count++);

        // Asigna los valores recibidos a los campos del objeto Movie
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setGroup(group);
        movie.setStudio(studio);
        movie.setCardImageUrl(cardImageUrl);
        movie.setBackgroundImageUrl(backgroundImageUrl);
        movie.setVideoUrl(videoUrl);

        // Retorna el objeto Movie creado
        return movie;
    }
}
