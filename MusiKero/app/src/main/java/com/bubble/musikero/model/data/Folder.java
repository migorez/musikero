package com.bubble.musikero.model.data;

import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bubble.musikero.R;

import java.util.Locale;

/**
 * Created by Miguel on 02/08/2017.
 * Class that manage the folders content
 *
 */
public class Folder extends PlayItem {

    public static final int m_ITEMTYPE = 1;

    private String m_name;
    private String m_path;
    private int    m_content_count;
    private long   m_content_duration;

    public Folder(String name, String path) {
        super(m_ITEMTYPE);
        m_name             = name;
        m_path             = path;
        m_content_count    = 0;
        m_content_duration = 0;
    }

    // PARCELABLE IMPLEMENTATION

    private Folder(Parcel in) {
        super(m_ITEMTYPE);
        m_itemType = in.readInt();
        m_name = in.readString();
        m_path = in.readString();
        m_content_count = in.readInt();
        m_content_duration = in.readLong();
    }

    public static final Creator<Folder> CREATOR = new Creator<Folder>() {
        @Override
        public Folder createFromParcel(Parcel in) {
            return new Folder(in);
        }

        @Override
        public Folder[] newArray(int size) {
            return new Folder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(m_itemType);
        dest.writeString(m_name);
        dest.writeString(m_path);
        dest.writeInt(m_content_count);
        dest.writeLong(m_content_duration);
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

    /**
     * Retorna el nombre de la carpeta que representa como primer argumento.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Retorna la ruta de la carpeta en el almacenamiento del dispositivo.
     */
    public String getPath() {
        return m_path;
    }

    @Override
    public String getDisplayName() {
        return m_name;
    }

    /**
     * Static method that get me the Song class view.
     *
     * @param parent object that reaches to me the context to inflate the view.
     */
    public static View getPlayItemView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.itemfolder_layout, parent, false);
    }

    @Override
    public void setViewHolder(View view) {
        ((TextView) view.findViewById(R.id.txv_folder_name)).setText(m_name);
        ((TextView) view.findViewById(R.id.txv_folder_content_count)).
                setText(String.format(
                        view.getContext().getResources().getString(R.string.text_files_count),
                        String.valueOf(m_content_count)
                ));
        ((TextView) view.findViewById(R.id.txv_folder_duration)).setText(getPlaybackDurationTime());
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
    public int getItemType() {
        return m_itemType;
    }

}
