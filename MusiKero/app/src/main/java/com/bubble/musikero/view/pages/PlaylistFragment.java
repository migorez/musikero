package com.bubble.musikero.view.pages;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bubble.musikero.R;
import com.bubble.musikero.model.PlayItemLoader;
import com.bubble.musikero.model.structure.PlayItem;
import com.bubble.musikero.model.structure.Playlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miguel on 12/09/2017.
 */
public class PlaylistFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<PlayItem>>  {

    // ATTRIBUTES

    private static final int INSTANCE_LOADER_MANAGER = 0;

    // implement RecyclerView.Adapter
    private PlaylistRecyclerAdapter m_recycler_adapter;

    private static Resources m_resources;

    // CONSTRUCTION

    public PlaylistFragment() {
        // Required empty public constructor for method newInstance
    }

    /**
     * Static void make that every component which request this fragment as it´s component
     */
    public static PlaylistFragment newInstance() {
        return new PlaylistFragment();
    }

    // CONFIG AND INIT

    // config view components
    private void initViewComponents(View view) {
        // config form recyclerview
        final RecyclerView m_recycler_view = (RecyclerView) view.findViewById(R.id.rv_playlist_fragment);
        m_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        // adapter and data of recyclerview
        m_recycler_adapter = new PlaylistRecyclerAdapter();
        m_recycler_view.setAdapter(m_recycler_adapter);
    }

    // FRAGMENT LIFECYCLE

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_resources = getResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.playlist_fragment, container, false);
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
        bundle_args.putString(PlayItemLoader.ARG_TYPELIST_LOAD, Playlist.ITEMTYPE);
        getLoaderManager().restartLoader // loader instance flag, bundle data, load reacts listener
                (INSTANCE_LOADER_MANAGER, bundle_args, PlaylistFragment.this);
    }

    // load recyclerview data asyncronously
    @Override
    public Loader<List<PlayItem>> onCreateLoader(int id, Bundle args) {
        // create a new asyncloader that recover the data
        return new PlayItemLoader(PlaylistFragment.this.getContext(), args);
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
    private static class PlaylistRecyclerAdapter extends RecyclerView.Adapter<PlaylistItemViewHolder> {

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
        public PlaylistItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PlaylistItemViewHolder( // view
                    LayoutInflater.from(parent.getContext()). // context
                            inflate(R.layout.itemplaylist_layout, parent, false) // xml layout
            );
        }

        // 3
        @Override
        public void onBindViewHolder(PlaylistItemViewHolder holder, int position) {
            holder.onBindView(m_list.get(position));
        }

        // Implement Own Methods
        void setItemList(List<PlayItem> list) {
            m_list = null;
            if (list != null) {
                m_list = new ArrayList<>();
                m_list.addAll(list);
            }
            PlaylistRecyclerAdapter.this.notifyDataSetChanged(); // ctrl + Q for info
        }
    }

    /***/
    private static class PlaylistItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView txv_playlist_name, txv_playlist_content_count, txv_playlist_duration;

        private Playlist m_playlist_item;

        PlaylistItemViewHolder(View itemView) {
            super(itemView);
            txv_playlist_name          = (TextView) itemView.findViewById(R.id.txv_playlist_name);
            txv_playlist_content_count = (TextView) itemView.findViewById(R.id.txv_playlist_content_count);
            txv_playlist_duration      = (TextView) itemView.findViewById(R.id.txv_playlist_duration);
            itemView.setOnClickListener(PlaylistItemViewHolder.this);
        }

        void onBindView(PlayItem item) {
            m_playlist_item = (Playlist) item;
            txv_playlist_name.setText(item.getDisplayName());
            txv_playlist_content_count.setText(
                    String.format(
                            m_resources.getString(R.string.text_files_count),
                            String.valueOf(item.getContentCount())
                    )
            );
            txv_playlist_duration.setText(item.getPlaybackDurationTime());
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "Reproduciendo: " + m_playlist_item.getDisplayName(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
