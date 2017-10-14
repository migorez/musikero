package com.bubble.musikero.model.widgets;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;

import com.bubble.musikero.model.PlayItemLoader;
import com.bubble.musikero.model.data.PlayItem;

import java.util.List;

/**
 *
 */

public abstract class PlayItemFragment extends Fragment implements
        Loader.OnLoadCompleteListener<List<PlayItem>>, Loader.OnLoadCanceledListener<List<PlayItem>>,
        PlayItemViewHolder.OnPlayItemViewHolderClickListener {

    protected String                    m_tabTitle;
    protected PlayItemLoader            m_playItemLoader;
    protected PlayItemFragmentCallbacks m_playItemCallbacks;
    protected PlayItemRecyclerAdapter   m_playItemListAdapter;

    // CONSTRUCTOR

    protected PlayItemFragment(String tabTitle) {
        m_tabTitle = tabTitle;
    }

    // Fragment lifecycle

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlayItemFragmentCallbacks) {
            m_playItemCallbacks = (PlayItemFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PlayItemFragmentCallbacks");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_playItemLoader = new PlayItemLoader(getContext());
        m_playItemLoader.registerListener(0, this);
        m_playItemListAdapter = new PlayItemRecyclerAdapter(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        m_playItemCallbacks = null;
    }

    // Own Methods

    public abstract String getTabTitle();

    /**
     *
     */
    public interface PlayItemFragmentCallbacks {
        void onPlayItemPlaySelected(PlayItem playItem);
    }

}
