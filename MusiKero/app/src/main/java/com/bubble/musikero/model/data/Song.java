package com.bubble.musikero.model.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bubble.musikero.R;

import java.util.Locale;

/**
 * Created by Miguel on 05/09/2017.
 */
public class Song extends PlayItem {

    public static final int ITEMTYPE = 0;

    private long   m_id;
    private String m_display_name;
    private long   m_duration_mills;
    private String m_path;

    private String m_folder_name;
    private String m_folder_path;

    // Constructor
    public Song(long id, String display, String path, long duration){
        super(ITEMTYPE);
        m_id = id;
        m_display_name = display;
        m_path = path;
        m_duration_mills = duration;

        // acquiring the folder name and path
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

    /**
     * Static method that get me the Song class view.
     *
     * @param parent object that reaches to me the context to inflate the view.
     */
    public static View getPlayItemView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.itemsong_layout, parent, false);
    }

    @Override
    public void setViewHolder(View view) {
        ((TextView) view.findViewById(R.id.txv_song_name)).setText(m_display_name);
        ((TextView) view.findViewById(R.id.txv_song_duration)).setText(getPlaybackDurationTime());
    }

    public String getPath() {
        return m_path;
    }

    /**
     * @return Uri of the song reference.
     */
    public Uri getUri() {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, m_id);
    }

    @Override
    public String getPlaybackDurationTime() {
        long s = (m_duration_mills / 1000) % 60;
        long m = (m_duration_mills / (1000 * 60)) % 60;
        // http://developando.com/blog/java-formatear-cadenas-string-format
        return String.format(Locale.US, "%02d:%02d", m, s);
    }

    @Override
    public Long getPlaybackDurationMills() {
        return m_duration_mills;
    }

    public String getFolderName() {
        return m_folder_name;
    }

    public String getFolderPath() {
        return m_folder_path;
    }

    @Override
    public Integer getContentCount() {
        return null;
    }

    @Override
    public int getItemType() {
        return item_type;
    }

}
