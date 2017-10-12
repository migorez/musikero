package com.bubble.musikero.model;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.bubble.musikero.model.data.Folder;
import com.bubble.musikero.model.data.PlayItem;
import com.bubble.musikero.model.data.Playlist;
import com.bubble.musikero.model.data.Song;

import java.util.List;

/**
 * Created by Miguel on 30/09/2017.
 */
public class PlayItemLoader extends AsyncTaskLoader<List<PlayItem>> {

    public static final int ARG_ONLY_INSTANCE_LOADER = 0;

    private int m_typeListLoad;
    private String folderPathLoad;

    /**
     * Este objeto es creado y anadido a la instancia del LoaderManager del fragmento que lo usa,
     * que al primer momento (al LoaderManager.restartLoader) tambien lo ejecuta.
     */
    public PlayItemLoader(Context context) {
        super(context);
    }

    public void reloadData(int typeListLoad, String folder_path) {
        this.m_typeListLoad = typeListLoad;
        this.folderPathLoad = folder_path;
        forceLoad();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<PlayItem> loadInBackground() {
        switch (m_typeListLoad) {
            case Song.ITEMTYPE:
                return PlayItemProvider.getDeviceSongs(getContext());
            case Folder.ITEMTYPE:
                if (folderPathLoad != null) {
                    return PlayItemProvider.getFolderContent(getContext(), folderPathLoad);
                }
                return PlayItemProvider.getPlayFolders(getContext());
            case Playlist.ITEMTYPE:
                return PlayItemProvider.getPlaylists(getContext());
            default:
                return null;
        }
    }

}
