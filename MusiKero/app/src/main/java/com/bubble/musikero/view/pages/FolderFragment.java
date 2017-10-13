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
import android.widget.Toast;

import com.bubble.musikero.R;
import com.bubble.musikero.controlador.player.MusicService;
import com.bubble.musikero.model.PlayItemLoader;
import com.bubble.musikero.model.data.Folder;
import com.bubble.musikero.model.data.PlayItem;
import com.bubble.musikero.model.data.Song;
import com.bubble.musikero.model.widgets.PlayItemRecyclerAdapter;
import com.bubble.musikero.model.widgets.PlayItemViewHolder;
import com.bubble.musikero.view.MainActivity;

import java.util.List;

public class FolderFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<PlayItem>>,
        PlayItemViewHolder.OnPlayItemViewHolderClickListener,
        MainActivity.OnActivityInteractionListener {

    // ATTRIBUTES

    //private RecyclerView m_recycler_view;
    private PlayItemRecyclerAdapter m_play_item_recycler_adapter;

    private PlayItemLoader m_playItemLoader;

    // CONSTRUCTION

    public FolderFragment() {
        // Required empty public constructor for method newInstance
    }

    /**
     * Static void make that every component which request this fragment as it´s component
     */
    public static FolderFragment newInstance() {
        return new FolderFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        // the inflate layout for the View
        View folderFragmentView = inflater.inflate(R.layout.folder_fragment, container, false);

        // config form recyclerview
        RecyclerView m_recycler_view = (RecyclerView) folderFragmentView.findViewById(R.id.rv_folder_fragment);
        m_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

        // recyclerview adapter
        m_play_item_recycler_adapter = new PlayItemRecyclerAdapter(this);
        m_recycler_view.setAdapter(m_play_item_recycler_adapter);

        // return the view already set
        return folderFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        // at first time create the loader instance whit the id provided.
        m_playItemLoader = (PlayItemLoader) getLoaderManager().restartLoader // loader instance flag, bundle data, load reacts listener
                (PlayItemLoader.ARG_ONLY_INSTANCE_LOADER, null, this);
        m_playItemLoader.reloadData(Folder.ITEMTYPE, null);
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

    // LOADER INTERFACES

    // load recyclerview data asynchronously
    @Override
    public Loader<List<PlayItem>> onCreateLoader(int id, Bundle args) {
        // El metodo restartloader del LoaderManager manda a ejecutar este metodo para que le otorgue
        // o le resuelva el objeto Loader que cargara los datos en un hilo asincrono. Al mismo tiempo
        // ejecuta su carga de datos que automaticamente ejecutan los restantes callbacks.
        // 1 - create instance loader (restartLoader, initLoader once time)
        // 2 - this onCreateLoader is combine
        // 3 - if the activity or fragment are ready the Loader start and trigger the callbacks less
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

    // RECYCLER ITEMS CLICK LISTENERS

    @Override
    public void onPlayItemClick(PlayItem play_item) {
        if (play_item.getItemType() == Folder.ITEMTYPE) {
            m_playItemLoader.reloadData(Folder.ITEMTYPE, ((Folder) play_item).getPath());
        } else if(play_item.getItemType() == Song.ITEMTYPE) {
            getActivity().startService(new Intent(
                    MusicService.ACTION_PLAY,
                    ((Song) play_item).getUri(),
                    getContext(),
                    MusicService.class
            ));
        }
    }

    @Override
    public void onPlayItemLongClick(PlayItem play_item) {
        if (play_item.getItemType() == Folder.ITEMTYPE) {
            Bundle bundle = new Bundle();
            bundle.putString( // bundle with the folder path for its songs
                    MusicService.EXTRA_KEY_FOLDER_PATH_TO_PLAY,
                    ((Folder) play_item).getPath()
            );
            Intent playFolderIntent = new Intent(
                    MusicService.ACTION_PLAY,
                    null, // data null
                    getContext(),
                    MusicService.class);
            playFolderIntent.putExtras(bundle);
            getActivity().startService(playFolderIntent);
        }
        Toast.makeText(getContext(), "LongClick en " + play_item.getDisplayName(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onKeyBackPressed() {
        Toast.makeText(getContext(), "Folder Back pressed",Toast.LENGTH_SHORT).show();
        m_playItemLoader.reloadData(Folder.ITEMTYPE, null);
    }

}
