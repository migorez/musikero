package com.bubble.musikero.model.data;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Miguel on 31/07/2017.
 * Abstract class of which its heirs will shares a lot of its methods and attributes.
 */

public abstract class PlayItem {

    // Items types:
    // 0 = Song
    // 1 = Folder
    // 2 = Playlist

    final int item_type;

    // abstract class cannot instanciate. Protected constructor so that the class that extends
    // from this is able to generate a public constructor.
    protected PlayItem(int item_type) {
        this.item_type = item_type;
    }

    /**
     * @param parent the context that is needed to inflate the view.
     */
    public abstract void setViewHolder(View view);

    public abstract int getItemType();

    public abstract String getDisplayName();

    public abstract String getPlaybackDurationTime();

    public abstract Long getPlaybackDurationMills();

    public abstract Integer getContentCount();

}
