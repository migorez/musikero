package com.bubble.musikero.model.data;

import android.os.Parcelable;
import android.view.View;

/**
 * Created by Miguel on 31/07/2017.
 * Abstract class of which its heirs will shares a lot of its methods and attributes.
 */

public abstract class PlayItem implements Parcelable {

    // Items types:
    // 0 = Song
    // 1 = Folder
    // 2 = Playlist

    int m_itemType;
    int m_listPosition;

    // abstract class cannot instanciate. Protected constructor so that the class that extends
    // from this is able to generate a public constructor.
    protected PlayItem(int itemType) {
        this.m_itemType = itemType;
    }

    public abstract void setListPosition(int listPosition);

    public abstract int getListPosition();

    /**
     * @param view the context that is needed to inflate the view.
     */
    public abstract void setViewHolder(View view);

    public abstract int getItemType();

    public abstract String getDisplayName();

    public abstract String getPlaybackDurationTime();

    public abstract Long getPlaybackDurationMills();

    public abstract Integer getContentCount();

}
