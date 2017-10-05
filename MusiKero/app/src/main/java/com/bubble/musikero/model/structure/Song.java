package com.bubble.musikero.model.structure;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.Date;

/**
 * Created by Miguel on 05/09/2017.
 */
public class Song extends PlayItem {

    public static final String ITEMTYPE = "Song";

    private static final java.text.SimpleDateFormat SIMPLE_DATE_FORMAT =
            new java.text.SimpleDateFormat("mm:ss");

    private long m_id;
    private String m_display_name;
    private long m_life;
    private String m_path;

    private String m_folder_name;
    private String m_folder_path;

    // Constructor
    public Song(long id, String display, String path, long life){
        m_id = id;
        m_display_name = display;
        m_path = path;
        m_life = life;

        int index_last_slash = m_path.lastIndexOf("/");
        m_folder_path = m_path.substring(0, index_last_slash); // desde el primer (incluido) hasta antes del segundo valor (sin sin incluirlo)
        index_last_slash = m_folder_path.lastIndexOf("/");
        m_folder_name = m_folder_path.substring(index_last_slash + 1);
    }

    public long getId() {
        return m_id;
    }

    @Override
    public String getDisplayName() {
        return m_display_name;
    }

    @Override
    public String getLengLife() {
        return SIMPLE_DATE_FORMAT.format(new Date(m_life));
    }

    @Override
    public String getNature() {
        return ITEMTYPE;
    }

    public String getPath() {
        return m_path;
    }

    public String getFolderName() {
        return m_folder_name;
    }

    public String getFolderPath() {
        return m_folder_path;
    }

    public Uri getUri() {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, m_id);
    }

    @Override
    public String toString() {
        return ITEMTYPE;
    }
}
