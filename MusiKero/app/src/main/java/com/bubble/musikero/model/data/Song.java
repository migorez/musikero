package com.bubble.musikero.model.data;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bubble.musikero.R;

import java.util.Locale;

/**
 * Created by Miguel on 05/09/2017.
 * Song
 *
 */

public class Song extends PlayItem {

    public static final int m_ITEMTYPE = 0;

    private long   m_id;
    private String m_display_name;
    private long   m_duration_mills;
    private String m_path;

    private String m_folder_name;
    private String m_folder_path;

    // Constructors

    public Song(long id, String display, String path, long duration){
        super(m_ITEMTYPE);
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

    // PARCELABLE IMPLEMENTATION

    private Song(Parcel in) {
        super(m_ITEMTYPE);
        m_itemType = in.readInt();
        m_id = in.readLong();
        m_display_name = in.readString();
        m_path = in.readString();
        m_duration_mills = in.readLong();
        m_folder_name = in.readString();
        m_folder_path = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(m_itemType);
        dest.writeLong(m_id);
        dest.writeString(m_display_name);
        dest.writeString(m_path);
        dest.writeLong(m_duration_mills);
        dest.writeString(m_folder_name);
        dest.writeString(m_folder_path);
    }

    // GETTERS AND SETTERS

    @Override
    public void setListPosition(int listPosition) {
        m_listPosition = listPosition;
    }

    @Override
    public int getListPosition() {
        return m_listPosition;
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
        return m_itemType;
    }

}
