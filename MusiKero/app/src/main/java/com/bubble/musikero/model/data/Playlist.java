package com.bubble.musikero.model.data;

import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bubble.musikero.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Miguel on 10/09/2017.
 * Playlist
 *
 */
public class Playlist extends PlayItem {

    public static final int m_ITEMTYPE = 2;

    public static final List<Playlist> DEFAULT_PLAYLISTS = Collections.unmodifiableList(
            new ArrayList<Playlist>() {
                {
                    add(new Playlist(0, "selección"));
                    add(new Playlist(1, "todo"));
                    add(new Playlist(2, "cola"));
                }
            }
    );

    private long m_id;
    private String m_name;
    private long m_content_duration;

    /**
     * */
    public Playlist(long id, String name) {
        super(m_ITEMTYPE);
        m_id = id;
        m_name = name;
    }

    // PARCELABLE IMPLEMENTATION

    public Playlist(Parcel in) {
        super(m_ITEMTYPE);
        m_itemType = in.readInt();
        m_id = in.readLong();
        m_name = in.readString();
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

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

    /**
     * Static method that get me the Song class view.
     *
     * @param parent object that reaches to me the context to inflate the view.
     */
    public static View getPlayItemView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.itemplaylist_layout, parent, false);
    }

    @Override
    public void setViewHolder(View view) {
        ((TextView) view.findViewById(R.id.txv_playlist_name)).setText(m_name);
        ((TextView) view.findViewById(R.id.txv_playlist_content_count)).setText(
                String.format(
                        view.getResources().getString(R.string.text_files_count),
                        String.valueOf(0)
                )
        );
        ((TextView) view.findViewById(R.id.txv_playlist_duration)).setText(
                String.valueOf(m_content_duration));
    }

    @Override
    public String getDisplayName() {
        return m_name;
    }

    @Override
    public String getPlaybackDurationTime() {
        // refs => https://stackoverflow.com/questions/9027317/how-to-convert-milliseconds-to-hhmmss-format
        long s = (m_content_duration / 1000) % 60;
        long m = (m_content_duration / (1000 * 60)) % 60;
        long h = (m_content_duration / (1000 * 60 * 60)) % 24;
        if (h > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", h, m, s);
        }
        return String.format(Locale.US, "%02d:%02d", m, s);
    }

    @Override
    public Long getPlaybackDurationMills() {
        return m_content_duration;
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
