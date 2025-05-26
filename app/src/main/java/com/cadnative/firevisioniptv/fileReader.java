package com.cadnative.firevisioniptv;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/*
 * Clase para leer un archivo de lista de reproducción M3U desde los assets de la app,
 * y extraer la información de los canales (Channel) en una lista.
 */
public class fileReader {

    // Tag para mensajes de log
    private static final String TAG = "FileReader";

    // Constantes para detectar etiquetas y formatos dentro del archivo M3U
    private static final String EXT_INF_SP = "#EXTINF:"; // Línea que contiene metadatos del canal
    private static final String KOD_IP_DROP_TYPE = "#KODIPROP:inputstream.adaptive.license_type="; // Tipo de DRM
    private static final String KOD_IP_DROP_KEY = "#KODIPROP:inputstream.adaptive.license_key=";   // Clave DRM
    private static final String TVG_NAME = "tvg-name=";    // Nombre del canal (atributo)
    private static final String TVG_ID = "tvg-id=";        // ID del canal (atributo)
    private static final String TVG_LOGO = "tvg-logo=";    // Logo del canal (atributo)
    private static final String GROUP_TITLE = "group-title="; // Grupo al que pertenece el canal
    private static final String COMMA = ",";               // Separador para dividir datos
    private static final String HTTP = "http://";          // URL HTTP
    private static final String HTTPS = "https://";        // URL HTTPS

    // Lista para almacenar los objetos Channel extraídos del archivo
    private final List<Channel> channelList;

    // Constructor inicializa la lista vacía
    public fileReader() {
        this.channelList = new ArrayList<>();
    }

    /**
     * Método que lee el archivo "playlist.m3u" desde los assets y construye una lista de canales.
     * @param assetManager para acceder a los archivos dentro del paquete assets
     * @return lista de canales extraídos del archivo, o null si no se encontraron canales
     */
    public List<Channel> readFile(AssetManager assetManager) {

        // Abrimos el archivo con BufferedReader para leer línea a línea
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open("playlist.m3u")))) {
            String currentLine;     // Línea actual leída del archivo
            Channel channel = null; // Variable temporal para ir creando cada canal

            // Recorremos cada línea hasta llegar al final del archivo
            while ((currentLine = bufferedReader.readLine()) != null) {
                try {
                    // Ajustamos el formato para separar mejor las comas y quitar comillas
                    currentLine = currentLine.replace(",", " ,");
                    currentLine = currentLine.replace("\"", "");

                    // Si la línea comienza con la etiqueta de metadatos de canal #EXTINF:
                    if (currentLine.startsWith(EXT_INF_SP)) {
                        channel = new Channel(); // Creamos un nuevo canal
                        // Separamos la línea en dos partes por la primera coma
                        String[] parts = currentLine.split(",");
                        if (parts.length > 1) {
                            // La parte anterior a la coma contiene los atributos, separados por espacio
                            String[] attributes = parts[0].split(" ");
                            // Recorremos cada atributo para buscar y asignar datos al canal
                            for (String attr : attributes) {
                                if (attr.startsWith(TVG_ID)) {
                                    String[] attributesSplit= attr.split("=");
                                    if (attributesSplit.length > 1) {
                                        channel.setChannelId(attributesSplit[1]);
                                    }
                                } else if (attr.startsWith(TVG_NAME)) {
                                    String[] attributesSplit= attr.split("=");
                                    if (attributesSplit.length > 1) {
                                        channel.setChannelName(attributesSplit[1]);
                                    }
                                } else if (attr.startsWith(TVG_LOGO)) {
                                    String[] attributesSplit= attr.split("=");
                                    if (attributesSplit.length > 1) {
                                        channel.setChannelImg(attributesSplit[1]);
                                    }
                                } else if (attr.startsWith(GROUP_TITLE)) {
                                    String[] attributesSplit= attr.split("=");
                                    if (attributesSplit.length > 1) {
                                        channel.setChannelGroup(attributesSplit[1]);
                                    }
                                }
                            }
                            // El nombre real del canal suele estar después de la coma, se actualiza para seguridad
                            channel.setChannelName(parts[1].trim());
                        }
                    }
                    // Si la línea comienza con el tipo de licencia DRM, se asigna a channel
                    else if (currentLine.startsWith(KOD_IP_DROP_TYPE)) {
                        if (channel != null) {
                            channel.setChannelDrmType(currentLine.split(KOD_IP_DROP_TYPE)[1].trim());
                        }
                    }
                    // Si la línea comienza con la clave DRM, se asigna a channel
                    else if (currentLine.startsWith(KOD_IP_DROP_KEY)) {
                        if (channel != null) {
                            channel.setChannelDrmKey(currentLine.split(KOD_IP_DROP_KEY)[1].trim());
                        }
                    }
                    // Si la línea es una URL (http o https), se asigna al canal y se agrega a la lista
                    else if (currentLine.startsWith(HTTP) || currentLine.startsWith(HTTPS)) {
                        if (channel != null) {
                            channel.setChannelUrl(currentLine);
                            channelList.add(channel);
                        }
                    }
                } catch (Exception e) {
                    // En caso de error al procesar una línea, se registra en log sin detener el proceso
                    Log.e(TAG, "Error reading file: " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            // Error al abrir o leer el archivo
            Log.e(TAG, "Error reading file: " + e.getMessage(), e);
        }

        // Si se encontraron canales, se retorna la lista; si no, retorna null y registra error
        if (!channelList.isEmpty()) {
            return channelList;
        } else {
            Log.e(TAG, "Error: No channels found in the file");
            return null;
        }
    }
}
