package com.bubble.musikero.model.structure;

import java.util.Locale;

/**
 * Created by Miguel on 02/08/2017.
 */
public class Folder extends PlayItem {

    public static final String ITEMTYPE = "Folder";

    private String m_name;
    private String m_path;
    private int    m_content_count;
    private long   m_content_duration;

    public Folder(String name, String path) {
        m_name             = name;
        m_path             = path;
        m_content_count    = 0;
        m_content_duration = 0;
    }

    /**
     * Set the number of elements (reproducible files) contained in this folder. */
    public void addMember(PlayItem playitem) {
        m_content_count++;
        m_content_duration = m_content_duration + playitem.getPlaybackDurationMills();
    }

    @Override
    public Integer getContentCount() {
        return m_content_count;
    }

    public String getName() {
        return m_name;
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
    public String getItemType() {
        return ITEMTYPE;
    }

    public String getPath() {
        return m_path;
    }

    @Override
    public String toString() {
        return ITEMTYPE;
    }
}
