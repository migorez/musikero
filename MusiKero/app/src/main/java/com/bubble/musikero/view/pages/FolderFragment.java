package com.bubble.musikero.view.pages;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bubble.musikero.R;
import com.bubble.musikero.model.PlayItemLoader;
import com.bubble.musikero.model.structure.Folder;
import com.bubble.musikero.model.structure.PlayItem;

import java.util.ArrayList;
import java.util.List;

public class FolderFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<PlayItem>> {

    // ATTRIBUTES

    private static final int INSTANCE_LOADER_MANAGER = 0;

    // instancia del widget de la UI para mostrar el listado de objetos tipo Folder
    // https://developer.android.com/guide/topics/ui/layout/recyclerview.html
    //private RecyclerView m_recycler_view;

    // implement RecyclerView.Adapter
    private FolderRecyclerAdapter m_recycler_adapter;

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

    // CONFIG AND INIT

    // config view components
    private void initViewComponents(View view) {
        // config form recyclerview
        final RecyclerView m_recycler_view = (RecyclerView) view.findViewById(R.id.rv_folder_fragment);
        m_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        // adapter and data of recyclerview
        m_recycler_adapter = new FolderRecyclerAdapter();
        m_recycler_view.setAdapter(m_recycler_adapter);
        registerForContextMenu(m_recycler_view);
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
        return inflater.inflate(R.layout.folder_fragment, container, false);
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

    // metodo versatil para la recarga de la lista
    private void loadRecyclerView() {
        Bundle bundle_args = new Bundle();
        bundle_args.putString(PlayItemLoader.ARG_TYPELIST_LOAD, Folder.ITEMTYPE);
        getLoaderManager().restartLoader // loader instance flag, bundle data, load reacts listener
                (INSTANCE_LOADER_MANAGER, bundle_args, FolderFragment.this);
    }

    // LoaderManager Interfaces
    // load recyclerview data asyncronously
    @Override
    public Loader<List<PlayItem>> onCreateLoader(int id, Bundle args) {
        // create a new asyncloader that recover the data
        return new PlayItemLoader(FolderFragment.this.getContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<PlayItem>> loader, List<PlayItem> data) {
        m_recycler_adapter.setItemList(data);
    }

    @Override
    public void onLoaderReset(Loader<List<PlayItem>> loader) {
        m_recycler_adapter.setItemList(null);
    }

    /*// MENU CONTROL

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.op_ctx_menu_play_folder:
                Toast.makeText(
                        getContext(),
                        "Has seleccionado reproducir la carpeta... (pronto)",
                        Toast.LENGTH_LONG
                ).show();
                return true;
        }
        return super.onContextItemSelected(item);
    }*/

    // INNER CLASS

    // RECYCLERVIEW ITEM ADAPTER

    // implement RecyclerView.Adapter for manage the list of this fragment
    private static class FolderRecyclerAdapter extends RecyclerView.Adapter<FolderItemViewHolder> {

        // ATTRIBUTES

        // list of items to show
        private List<PlayItem> m_list;

        // CONSTRUCT

        FolderRecyclerAdapter() {

        }

        // Implements RecyclerView.Adapter
        // 1
        @Override
        public int getItemCount() {
            return m_list != null ? m_list.size() : 0;
        }

        // 2
        @Override
        public FolderItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FolderItemViewHolder( // view object
                    LayoutInflater.from(parent.getContext()). // inflate layout from context
                            inflate(R.layout.itemfolder_layout, parent, false) // xml layout to inflate
            );
        }

        // 3
        @Override
        public void onBindViewHolder(FolderItemViewHolder holder, int position) {
            holder.onBindView(m_list.get(position)); // here comes the inflated view object (xml layout)
        }

        @Override
        public void onViewRecycled(FolderItemViewHolder holder) {
            holder.onBindView(null); // here i pass it null to bindView for release resources
            super.onViewRecycled(holder);
        }

        // Implement Own Methods
        void setItemList(List<PlayItem> list) {
            m_list = null;
            if (list != null) {
                m_list = new ArrayList<>();
                m_list.addAll(list);
            }
            FolderRecyclerAdapter.this.notifyDataSetChanged(); // ctrl + Q for info
        }
    }

    // VIEW HOLDER CLASS

    /***/
    private static class FolderItemViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {

        // ATTRIBUTES

        private TextView txv_folder_name, txv_folder_content_count, txv_folder_duration;

        private Folder m_folder_item;

        // CONSTRUCTION

        FolderItemViewHolder(View itemView) {
            super(itemView);
            // set widgets
            txv_folder_name          = (TextView) itemView.findViewById(R.id.txv_folder_name);
            txv_folder_content_count = (TextView) itemView.findViewById(R.id.txv_folder_content_count);
            txv_folder_duration      = (TextView) itemView.findViewById(R.id.txv_folder_duration);
        }

        /**
         * ViewHolder's implemented class method onBindView receives a PlayItem object, which if
         * isn't null set the view else release the view. */
        void onBindView(PlayItem item) {
            if (item != null) {
                m_folder_item = (Folder) item; // Folder extends from PlayItem
                // draw on widgets
                txv_folder_name.setText(item.getDisplayName());
                txv_folder_content_count.setText(
                        String.format(
                                itemView.getContext().getResources().
                                        getString(R.string.text_files_count),
                                String.valueOf(item.getContentCount())
                        )
                );
                txv_folder_duration.setText(item.getPlaybackDurationTime());
                // register listeners
                itemView.setOnClickListener(FolderItemViewHolder.this);
                itemView.setOnLongClickListener(FolderItemViewHolder.this);
            } else {
                itemView.setOnClickListener(null);
                itemView.setOnLongClickListener(null);
            }
        }

        // ACTIONS

        @Override
        public void onClick(View v) {
            // In this FolderFragment the onClick method of each holder item, will me allow to deploy the
            // content of the folder referenced in the holder.

        }

        @Override
        public boolean onLongClick(View v) {
            // action that deploy a emergent menu offering the options of this fragment-list.
            PopupMenu popmenu = new PopupMenu(v.getContext(), v);
            popmenu.inflate(R.menu.ctx_menu_rv_folder_frag);
            popmenu.setOnMenuItemClickListener(FolderItemViewHolder.this);
            popmenu.show();
            return false;
        }

        // MENU

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            String mnj;
            switch (item.getItemId()) {
                case R.id.op_ctx_menu_play_folder:
                    mnj = "Has seleccionado reproducir la carpeta " + m_folder_item.getDisplayName() + ".";
                    break;
                case R.id.op_ctx_menu_folder_add_tail:
                    mnj = "Agregar a la cola " + m_folder_item.getPlaybackDurationTime()
                            + " de reproducción.";
                    break;
                case R.id.op_ctx_menu_folder_add_to_playlist:
                    mnj = "Agregar la carpeta " + m_folder_item.getDisplayName() + " a la lista...";
                    break;
                default:
                    mnj = "";
            }
            Toast.makeText(
                    itemView.getContext(),
                    mnj,
                    Toast.LENGTH_LONG
            ).show();
            return false;
        }
    }
}
