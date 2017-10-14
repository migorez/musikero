package com.bubble.musikero.view.pages;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bubble.musikero.R;
import com.bubble.musikero.model.data.PlayItem;
import com.bubble.musikero.model.data.Song;
import com.bubble.musikero.model.widgets.PlayItemFragment;

import java.util.List;

/**
 * Created by Miguel on 05/09/2017.
 */
public class SongFragment extends PlayItemFragment {

    // CONSTRUCTION

    public SongFragment() {
        super("Songs");
        // Required empty public constructor for method newInstance
    }

    /**
     * Static void make that every component which request this fragment as it´s component
     */
    public static SongFragment newInstance() {
        return new SongFragment();
    }

    // FRAGMENT LIFECYCLE

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View songFragmentView = inflater.inflate(R.layout.song_fragment, container, false);
        // config form recyclerview
        // instancia del widget de la UI para mostrar el listado de objetos tipo Song
        // https://developer.android.com/guide/topics/ui/layout/recyclerview.html
        RecyclerView m_recycler_view = (RecyclerView) songFragmentView.findViewById(R.id.rv_song_fragment);
        m_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        // adapter and data of recyclerview
        m_recycler_view.setAdapter(m_playItemListAdapter);
        return songFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        m_playItemLoader.reloadData(Song.ITEMTYPE, null);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // END LIFECYCLE

    // IMPLEMENTS METHODS AND INTERFACES

    @Override
    public String getTabTitle() {
        return m_tabTitle;
    }

    // load recyclerview data asyncronously
    @Override
    public void onLoadCanceled(Loader<List<PlayItem>> loader) {
        m_playItemListAdapter.setItems(null);
    }

    @Override
    public void onLoadComplete(Loader<List<PlayItem>> loader, List<PlayItem> data) {
        if (data != null)
            m_playItemListAdapter.setItems(data);
    }

    @Override
    public void onPlayItemClick(PlayItem playItem) {
        if (m_playItemCallbacks != null) {
            m_playItemCallbacks.onPlayItemPlaySelected(playItem);
        }
    }

    @Override
    public void onPlayItemLongClick(PlayItem playItem) {
        if (m_playItemCallbacks != null) {
            m_playItemCallbacks.onPlayItemPlaySelected(playItem);
        }
    }

}
