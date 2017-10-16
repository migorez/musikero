package com.bubble.musikero.model.widgets;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bubble.musikero.model.data.Folder;
import com.bubble.musikero.model.data.PlayItem;
import com.bubble.musikero.model.data.Playlist;
import com.bubble.musikero.model.data.Song;

import java.util.List;

/**
 * Created by Miguel on 09/09/2017.
 * This class will provide the configuration of the lists in the app. This will be react to the methods
 * of the class PlayItem those must be implement by the class that extends of PlayItem, that is:
 * Song, Folder n Playlist
 */
public class PlayItemRecyclerAdapter extends RecyclerView.Adapter<PlayItemViewHolder> {

    // List of Items. At beginning the list is null, so that not execute onCreateViewHolder and onBindViewHolder
    private List<PlayItem> m_playItemList;

    // listener que transferira las acciones de click sobre los ViewHolders al fragmento, con la
    // informacion pertinente del PlayItem que alberga para utilizar los recursos del fragmento.
    private PlayItemViewHolder.OnPlayItemViewHolderClickListener m_onPlayItemViewHolderClickListener;

    // Construct

    /**
     * @param onPlayItemRecyclerClick The listener that resolve the actions clicks above the holders.
     */
    PlayItemRecyclerAdapter(
            @NonNull PlayItemViewHolder.OnPlayItemViewHolderClickListener onPlayItemRecyclerClick) {
        this.m_onPlayItemViewHolderClickListener = onPlayItemRecyclerClick;
    }

    // OWN IMPLEMENT METHODS

    /**
     * Void setItems sets m_list in a new ArrayList whit the List<PlayItem> passed replacing all
     * the previous items
     */
    public void setItems(List<PlayItem> dataList) {
        m_playItemList = dataList;
        notifyDataSetChanged(); // info ctrl + Q, execute the three main adapter methods.
    }

    // IMPLEMENT ADAPTER ABSTRACT METHODS

    // execution order
    // 1
    @Override
    public int getItemCount() {
        // The compiler execute this code before deploy views onCreateViewHolder and onBindViewHolder
        // for know how many must do. Al instanciar el adaptador con esta lista nula, logicamente no se
        // desplegara nada.
        // List's members count, if it's null, return 0 for not deploy ViewHolders.
        // this method is the first in be executed. If return 0, the 2 remaining methods will not be executed.
        return m_playItemList != null ? m_playItemList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        // quiza mezcle listas de diferentes PlayItem
        m_playItemList.get(position).setListPosition(position);
        return m_playItemList.get(position).getItemType();
    }

    // 2
    @Override
    public PlayItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View playItemView;
        switch (viewType) {
            case Song.m_ITEMTYPE:
                playItemView = Song.getPlayItemView(parent);
                break;
            case Folder.m_ITEMTYPE:
                playItemView = Folder.getPlayItemView(parent);
                break;
            case Playlist.m_ITEMTYPE:
                playItemView = Playlist.getPlayItemView(parent);
                break;
            default:
                playItemView = null;
                break;
        }
        return new PlayItemViewHolder(playItemView);
    }

    // 3
    @Override
    public void onBindViewHolder(PlayItemViewHolder playItemHolder, int position) {
        playItemHolder.onBindViewHolder(m_playItemList.get(position), m_onPlayItemViewHolderClickListener);
    }

    // 4
    @Override
    public void onViewRecycled(PlayItemViewHolder playItemHolder) {
        // este metodo reutiliza una vista ya creada.
        playItemHolder.onBindViewHolder(null, null);
        super.onViewRecycled(playItemHolder);
    }

}
