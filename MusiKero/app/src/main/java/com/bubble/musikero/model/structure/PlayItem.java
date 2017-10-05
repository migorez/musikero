package com.bubble.musikero.model.structure;

/**
 * Created by Miguel on 31/07/2017.
 */
public abstract class PlayItem { // can refs file, directory, uri

    // atributo que definira la clase heredera
    //protected static String ITEMTYPE;

    // abstract class cannot instanciate. Protected constructor so that the class that extends
    // from this is able to generate a public constructor.
    protected PlayItem() {
    }

    public abstract String getDisplayName();

    public abstract String getLengLife();

    public abstract String getNature();

}


    //public abstract List actionListItemClick(int position);

    /*private Song song;
    private Folder folder;
    private boolean im_folder = false;
    private PlayList playlist;

    *//**
     * Constructor utilizado para archivos reproducibles como tal*//*
    public PlayItem(long id, String display, String ruta, long duracion) {
        song = new Song(id, display, ruta, duracion);
        im_folder = false;
    }

    *//**
     * Constructor empleado para representar carpetas en el mismo widget*//*
    public PlayItem(String name_folder, String path) {
        folder = new Folder(name_folder, path);
        im_folder = true;
    }

    public PlayItem(long id_playlist, String name_playlist) {
        playlist = new PlayList(id_playlist, name_playlist);
    }

    public Song getSong() {
        return song != null ? song : null;
    }

    public Folder getFolder() {
        return folder != null ? folder : null;
    }

    public boolean imFolder() {
        return im_folder;
    }

    public PlayList getPlaylist() {
        return playlist != null ? playlist : null;
    }

    *//**
     * Sub-clase de Itemlist que lo establecera como item de archivo o cancion reproducible*//*
    public class Song {
        // Modificadores de acceso a constructores y campos.
        // refs = http://www.aprenderaprogramar.com/index.php?option=com_content&view=article&id=665:public-private-y-protected-javatipos-de-modificadores-de-acceso-visibilidad-en-clases-subclases-cu00693b&catid=68&Itemid=188
        // en este caso se usara private porque estos atributos son inamovibles en cada objeto.
        private long m_id;
        private String m_display_name;
        private String m_path;
        private long m_duration;
        private String m_folder_name;
        private String m_folder_path;

        *//**
         * Constructor*//*
        public Song(long id, String display, String ruta, long duracion){
            m_id = id;
            m_display_name = display;
            m_path = ruta;
            m_duration = duracion;

            int index_last_slash = m_path.lastIndexOf("/");
            m_folder_path = m_path.substring(0, index_last_slash); // desde el primer (incluido) hasta antes del segundo valor (sin sin incluirlo)
            index_last_slash = m_folder_path.lastIndexOf("/");
            m_folder_name = m_folder_path.substring(index_last_slash + 1);
        }

        public long getId() {
            return m_id;
        }

        public String getDisplayName() {
            return m_display_name;
        }

        public String getRuta() {
            return m_path;
        }

        public String getDuracion() {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("mm:ss");
            return sdf.format(new Date(m_duration));
        }

        public String getNameFolder() {
            return m_folder_name;
        }

        public String getFolderPath() {
            return m_folder_path;
        }

        public Uri obtenerUriArchivo() {
            return ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, m_id);
        }
    }

    *//**
     * Sub-clase de Itemlist que lo establecera como item de carpeta*//*
    public class Folder {

        private String m_name;
        private String m_path;

        public Folder(String nameFolder, String path) {
            m_name = nameFolder;
            m_path = path;
        }

        public String getName() {
            return m_name;
        }

        public String getPath() {
            return m_path;
        }

    }

    public class PlayList {

        private long m_id;
        private String m_name;

        public PlayList(long id, String name) {
            m_id = id;
            m_name = name;
        }

        public long getId() {
            return m_id;
        }

        public String getName() {
            return m_name;
        }

    }*/


