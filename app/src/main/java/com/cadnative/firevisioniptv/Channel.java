package com.cadnative.firevisioniptv;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

/**
 * Clase que representa un canal de IPTV.
 * Extiende RealmObject para persistencia en la base de datos Realm
 * e implementa Parcelable para poder enviar objetos Channel entre Activities o Fragments.
 */
public class Channel extends RealmObject implements Parcelable {

    // Atributos principales del canal
    private String channelName;     // Nombre del canal
    private String channelId;       // ID único del canal
    private String channelUrl;      // URL del stream del canal
    private String channelImg;      // URL o path de la imagen del canal
    private String channelGroup;    // Grupo o categoría del canal (Ej: Deportes, Noticias)
    private String channelDrmKey;   // Clave DRM para reproducción (si aplica)
    private String channelDrmType;  // Tipo de DRM (Ej: Widevine, AES)

    /**
     * Constructor vacío requerido por Realm para poder instanciar objetos.
     */
    public Channel() {
    }

    // Métodos getters y setters para acceder y modificar cada atributo del canal

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public void setChannelUrl(String channelUrl) {
        this.channelUrl = channelUrl;
    }

    public String getChannelImg() {
        return channelImg;
    }

    public void setChannelImg(String channelImg) {
        this.channelImg = channelImg;
    }

    public String getChannelGroup() {
        return channelGroup;
    }

    public void setChannelGroup(String channelGroup) {
        this.channelGroup = channelGroup;
    }

    public String getChannelDrmKey() {
        return channelDrmKey;
    }

    public void setChannelDrmKey(String channelDrmKey) {
        this.channelDrmKey = channelDrmKey;
    }

    public String getChannelDrmType() {
        return channelDrmType;
    }

    public void setChannelDrmType(String channelDrmType) {
        this.channelDrmType = channelDrmType;
    }

    /**
     * Devuelve una representación legible del objeto Channel.
     */
    @Override
    public String toString() {
        return "Channel{" +
                "channelName='" + channelName + '\'' +
                ", channelUrl='" + channelUrl + '\'' +
                ", channelImg='" + channelImg + '\'' +
                ", channelGroup='" + channelGroup + '\'' +
                ", channelDrmKey='" + channelDrmKey + '\'' +
                ", channelDrmType='" + channelDrmType + '\'' +
                '}';
    }

    /**
     * Constructor que permite reconstruir un objeto Channel desde un Parcel.
     * Esto se usa cuando se recibe el objeto desde otra Activity o Fragment.
     *
     * @param in Parcel que contiene los datos del objeto.
     */
    public Channel(Parcel in) {
        String[] data = new String[6];
        in.readStringArray(data); // Leer datos en el mismo orden que se escribieron
        this.channelName = data[0];
        this.channelUrl = data[1];
        this.channelImg = data[2];
        this.channelGroup = data[3];
        this.channelDrmKey = data[4];
        this.channelDrmType = data[5];
    }

    /**
     * Método requerido por Parcelable, describe el contenido. Normalmente se deja en 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Método que escribe los datos del objeto al Parcel, en orden específico.
     * Este método se llama automáticamente al enviar el objeto entre componentes Android.
     *
     * @param parcel Parcel de destino.
     * @param i      No utilizado (pero obligatorio por la interfaz).
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{
                this.channelName,
                this.channelUrl,
                this.channelImg,
                this.channelGroup,
                this.channelDrmKey,
                this.channelDrmType
        });
    }

    /**
     * Creador requerido por Parcelable. Se encarga de reconstruir objetos desde un Parcel.
     */
    public static final Parcelable.Creator<Channel> CREATOR = new Parcelable.Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel in) {
            return new Channel(in); // Llama al constructor con Parcel
        }

        @Override
        public Channel[] newArray(int i) {
            return new Channel[i];
        }
    };
}
