package com.bubble.musikero.model.structure;

/**
 * Created by Miguel on 10/09/2017.
 */
public class Playlist extends PlayItem {

    public static final String ITEMTYPE = "Playlist";

    private long m_id;
    private String m_name;
    private long m_duration;

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
    public String getLengLife() {
        return null;
    }

    @Override
    public String getNature() {
        return ITEMTYPE;
    }

    @Override
    public String toString() {
        return ITEMTYPE;
    }
}
