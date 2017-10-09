package com.bubble.musikero.view.pages;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bubble.musikero.R;
import com.bubble.musikero.controlador.Reproduccion.MusicPlayerService;
import com.bubble.musikero.model.PlayItemLoader;
import com.bubble.musikero.model.PlayItemProvider;
import com.bubble.musikero.model.structure.PlayItem;
import com.bubble.musikero.model.structure.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miguel on 05/09/2017.
 */
public class SongFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<PlayItem>> {

    // ATTRIBUTES

    private static final int INSTANCE_LOADER_MANAGER = 0;

    // implement RecyclerView.Adapter
    private SongRecyclerAdapter m_recycler_adapter;

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

    // CONFIG AND INIT

    // config view components
    private View initViewComponents(View m_view) {
        // config form recyclerview
        // instancia del widget de la UI para mostrar el listado de objetos tipo Song
        // https://developer.android.com/guide/topics/ui/layout/recyclerview.html
        final RecyclerView m_recycler_view = (RecyclerView) m_view.findViewById(R.id.rv_song_fragment);
        m_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        // adapter and data of recyclerview
        m_recycler_adapter = new SongRecyclerAdapter();
        m_recycler_view.setAdapter(m_recycler_adapter);
        return m_view;
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
        return inflater.inflate(R.layout.song_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewComponents(getView());
        loadRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
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

    // metodo versatil para la recarga de la lista segun los parametros requeridos
    private void loadRecyclerView() {
        Bundle bundle_args = new Bundle();
        bundle_args.putString(PlayItemLoader.ARG_TYPELIST_LOAD, Song.ITEMTYPE);
        getLoaderManager().restartLoader // loader instance flag, bundle data, load reacts listener
                (INSTANCE_LOADER_MANAGER, bundle_args, SongFragment.this);
    }

    // load recyclerview data asyncronously
    @Override
    public Loader<List<PlayItem>> onCreateLoader(int id, Bundle args) {
        // create a new asyncloader that recover the data
        return new PlayItemLoader(SongFragment.this.getContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<PlayItem>> loader, List<PlayItem> data) {
        m_recycler_adapter.setItemList(data);
    }

    @Override
    public void onLoaderReset(Loader<List<PlayItem>> loader) {
        m_recycler_adapter.setItemList(null);
    }

    // INNER CLASS

    // implement RecyclerView.Adapter for manage the list of this fragment
    private static class SongRecyclerAdapter extends RecyclerView.Adapter<SongItemViewHolder> {

        // list of items to show
        private List<PlayItem> m_list;

        // Implements RecyclerView.Adapter
        // 1
        @Override
        public int getItemCount() {
            return m_list != null ? m_list.size() : 0;
        }

        // 2
        @Override
        public SongItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SongItemViewHolder( // view
                    LayoutInflater.from(parent.getContext()). // context
                            inflate(R.layout.itemsong_layout, parent, false) // xml layout
            );
        }

        // 3
        @Override
        public void onBindViewHolder(SongItemViewHolder holder, int position) {
            holder.onBindView(m_list.get(position));
        }

        // Implement Own Methods
        void setItemList(List<PlayItem> list) {
            m_list = null;
            if (list != null) {
                m_list = new ArrayList<>();
                m_list.addAll(list);
            }
            SongRecyclerAdapter.this.notifyDataSetChanged(); // ctrl + Q for info
        }
    }

    /***/
    private static class SongItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txv_song_name, txv_song_duration;

        private Song m_song_item;

        SongItemViewHolder(View itemView) {
            super(itemView);
            txv_song_name     = (TextView) itemView.findViewById(R.id.txv_song_name);
            txv_song_duration = (TextView) itemView.findViewById(R.id.txv_song_duration);
            itemView.setOnClickListener(SongItemViewHolder.this);
        }

        void onBindView(PlayItem item) {
            m_song_item = (Song) item; // Song extends from PlayItem
            txv_song_name.setText(item.getDisplayName());
            txv_song_duration.setText(item.getPlaybackDurationTime());
        }

        @Override
        public void onClick(View view) {
            view.getContext().startService(
                    new Intent(
                            MusicPlayerService.ACTION_PLAY,
                            m_song_item.getUri(),
                            view.getContext(),
                            MusicPlayerService.class
                    )
            );
            Toast.makeText(view.getContext(), "Reproduciendo: " + m_song_item.getDisplayName(),
                    Toast.LENGTH_LONG).show();
        }
    }
}