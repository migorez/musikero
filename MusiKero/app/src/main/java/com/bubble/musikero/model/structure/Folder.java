package com.bubble.musikero.model.structure;

/**
 * Created by Miguel on 02/08/2017.
 */
public class Folder extends PlayItem {

    public static final String ITEMTYPE = "Folder";

    private String m_name;
    private String m_path;

    public Folder(String name, String path) {
        //ITEMTYPE = TAG;
        m_name = name;
        m_path = path;
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

    public String getPath() {
        return m_path;
    }

    @Override
    public String toString() {
        return ITEMTYPE;
    }
}
