package com.bubble.musikero.view.pages;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bubble.musikero.R;
import com.bubble.musikero.model.data.Folder;
import com.bubble.musikero.model.data.PlayItem;
import com.bubble.musikero.model.widgets.PlayItemFragment;

import java.util.List;

public class FolderFragment extends PlayItemFragment {

    // CONSTRUCTION

    public FolderFragment() {
        super("Folders");
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
        RecyclerView recyclerView = (RecyclerView) folderFragmentView.findViewById(R.id.rv_folder_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // recyclerview adapter
        recyclerView.setAdapter(m_playItemListAdapter);
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

    // IMPLEMENTS METHODS AND INTERFACES

    @Override
    public String getTabTitle() {
        return m_tabTitle;
    }

    // load recyclerview data asynchronously
    @Override
    public void onLoadCanceled(Loader<List<PlayItem>> loader) {
        m_playItemListAdapter.setItems(null);
    }

    @Override
    public void onLoadComplete(Loader<List<PlayItem>> loader, List<PlayItem> data) {
        if (data != null)
            m_playItemListAdapter.setItems(data);
    }

    // RECYCLER ITEMS CLICK LISTENERS

    @Override
    public void onPlayItemClick(PlayItem playItem) {
        if (playItem.getItemType() == Folder.ITEMTYPE) {
            m_playItemLoader.reloadData(Folder.ITEMTYPE, ((Folder) playItem).getPath());
        } else {
            if (m_playItemCallbacks != null) {
                m_playItemCallbacks.onPlayItemPlaySelected(playItem);
            }
        }
    }

    @Override
    public void onPlayItemLongClick(PlayItem playItem) {
        if (m_playItemCallbacks != null) {
            m_playItemCallbacks.onPlayItemPlaySelected(playItem);
        }
        Toast.makeText(getContext(), "LongClick en " + playItem.getDisplayName(),
                Toast.LENGTH_SHORT).show();
    }

}
