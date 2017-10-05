package com.bubble.musikero.model;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.bubble.musikero.model.structure.Folder;
import com.bubble.musikero.model.structure.PlayItem;
import com.bubble.musikero.model.structure.Playlist;
import com.bubble.musikero.model.structure.Song;

import java.util.List;

/**
 * Created by Miguel on 30/09/2017.
 */

public class PlayItemLoader extends AsyncTaskLoader<List<PlayItem>> {

    public static final String ARG_TYPELIST_LOAD = "type_list_load";

    private Bundle m_args;

    public PlayItemLoader(Context context, Bundle args) {
        super(context);
        m_args = args;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<PlayItem> loadInBackground() {
        if (m_args.getString(PlayItemLoader.this.ARG_TYPELIST_LOAD).equals(Song.ITEMTYPE)){
            return PlayItemProvider.getDeviceSongs(getContext());
        } else if (m_args.getString(PlayItemLoader.this.ARG_TYPELIST_LOAD).equals(Folder.ITEMTYPE)) {
            return PlayItemProvider.getPlayFolders(getContext());
        } else if (m_args.getString(PlayItemLoader.this.ARG_TYPELIST_LOAD).equals(Playlist.ITEMTYPE)){
            return PlayItemProvider.getPlaylists(getContext());
        }
        return null;
    }
}
