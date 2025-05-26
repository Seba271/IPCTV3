package com.cadnative.firevisioniptv;

import java.io.Serializable;

/*
 * Clase Movie que representa una entidad de video dentro de la aplicación.
 * Contiene información básica sobre un video como título, descripción,
 * imágenes asociadas y URL para reproducirlo.
 *
 * Implementa Serializable para permitir que los objetos de esta clase
 * puedan ser convertidos en un flujo de bytes, facilitando su paso
 * entre actividades o almacenamiento.
 */
public class Movie implements Serializable {

    // Identificador único para cada video
    static final long serialVersionUID = 727566175075960653L;

    private long id;                 // ID único del video
    private String title;            // Título del video
    private String description;      // Descripción o sinopsis del video
    private String group;            // Grupo o categoría del video (ej. género, canal)

    private String bgImageUrl;       // URL de la imagen de fondo para la interfaz
    private String cardImageUrl;     // URL de la imagen miniatura o tarjeta que se muestra en listas
    private String videoUrl;         // URL del archivo de video para reproducción
    private String studio;           // Nombre del estudio o productora

    // Constructor vacío, necesario para algunas operaciones como serialización o frameworks
    public Movie() {
    }

    // Métodos getter y setter para acceder y modificar los atributos privados

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getBackgroundImageUrl() {
        return bgImageUrl;
    }

    public void setBackgroundImageUrl(String bgImageUrl) {
        this.bgImageUrl = bgImageUrl;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    /*
     * Método sobrescrito toString() para retornar una representación
     * textual del objeto Movie, útil para debugging o logs.
     */
    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", backgroundImageUrl='" + bgImageUrl + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                '}';
    }
}
