package com.bubble.musikero.model.structure;

import java.util.Locale;

/**
 * Created by Miguel on 10/09/2017.
 */
public class Playlist extends PlayItem {

    public static final String ITEMTYPE = "Playlist";

    private long m_id;
    private String m_name;
    private long m_content_duration;

    /**
     * */
    public Playlist(long id, String name) {
        m_id = id;
        m_name = name;
    }

    public long getId() {
        return m_id;
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
    public String getItemType() {
        return ITEMTYPE;
    }

    @Override
    public String toString() {
        return ITEMTYPE;
    }
}
