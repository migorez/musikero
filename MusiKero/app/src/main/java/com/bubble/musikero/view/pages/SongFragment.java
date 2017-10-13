package com.bubble.musikero.view.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bubble.musikero.R;
import com.bubble.musikero.controlador.player.MusicPlayerServiceB;
import com.bubble.musikero.model.PlayItemLoader;
import com.bubble.musikero.model.data.PlayItem;
import com.bubble.musikero.model.data.Song;
import com.bubble.musikero.model.widgets.PlayItemRecyclerAdapter;
import com.bubble.musikero.model.widgets.PlayItemViewHolder;
import com.bubble.musikero.view.MainActivity;

import java.util.List;

/**
 * Created by Miguel on 05/09/2017.
 */
public class SongFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<PlayItem>>,
        PlayItemViewHolder.OnPlayItemViewHolderClickListener,
        MainActivity.OnActivityInteractionListener {

    // ATTRIBUTES

    // implement RecyclerView.Adapter
    private PlayItemRecyclerAdapter m_play_item_recycler_adapter;

    private PlayItemLoader m_playItemLoader;

    // CONSTRUCTION

    public SongFragment() {
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
        m_play_item_recycler_adapter = new PlayItemRecyclerAdapter(this);
        m_recycler_view.setAdapter(m_play_item_recycler_adapter);

        return songFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        m_playItemLoader = (PlayItemLoader) getLoaderManager().restartLoader // loader instance flag, bundle data, load reacts listener
                (PlayItemLoader.ARG_ONLY_INSTANCE_LOADER, null, this);
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

    // load recyclerview data asyncronously
    @Override
    public Loader<List<PlayItem>> onCreateLoader(int id, Bundle args) {
        // create a new asyncloader that recover the data
        return new PlayItemLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<PlayItem>> loader, List<PlayItem> data) {
        m_play_item_recycler_adapter.setItems(data);
    }

    @Override
    public void onLoaderReset(Loader<List<PlayItem>> loader) {
        m_play_item_recycler_adapter.setItems(null);
    }

    @Override
    public void onPlayItemClick(PlayItem play_item) {
        getActivity().startService(new Intent(
                MusicPlayerServiceB.ACTION_PLAY,
                ((Song) play_item).getUri(),
                getContext(),
                MusicPlayerServiceB.class
        ));
    }

    @Override
    public void onPlayItemLongClick(PlayItem play_item) {

    }

    @Override
    public void onKeyBackPressed() {

    }

}
