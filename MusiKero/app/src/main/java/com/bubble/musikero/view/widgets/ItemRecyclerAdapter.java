package com.bubble.musikero.view.widgets;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bubble.musikero.R;
import com.bubble.musikero.model.structure.PlayItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miguel on 09/09/2017.
 * This class will provide the configuration of the lists in the app. This will be react to the methods
 * of the class PlayItem those must be implement by the class that extends of PlayItem, that is:
 * Song, Folder n Playlist
 */
public class ItemRecyclerAdapter
        extends RecyclerView.Adapter<ItemRecyclerAdapter.ItemViewHolder> {

    // refs => https://www.raywenderlich.com/126528/android-recyclerview-tutorial

    // fields

    /**
     * listener usado para que sea implementado por el fragmento que utiliza este adaptador en su
     * RecyclerView para que al clicar un elemento de la lista, el fragmento responda en su instancia
     * con sus recursos.*/
    private static ItemRecyclerClickListener m_item_recycler_click_listener;

    // collection of items
    private List<PlayItem> m_list;

    /** construct
    /* the implement RecyclerView.Adapter request a list of PlayItem for build its views. */
    public ItemRecyclerAdapter(List<PlayItem> list, ItemRecyclerClickListener item_recycler_click_listener) {
        m_list = list;
        m_item_recycler_click_listener = item_recycler_click_listener;
    }

    /**
     * Void setItems sets m_list in a new ArrayList whit the List<PlayItem> passed replacing all
     * the previous items
     * */
    public void setItems(List<PlayItem> item_list) {
        m_list = new ArrayList<>();
        if (item_list != null) {
            m_list.addAll(item_list);
        }
        notifyDataSetChanged();
    }

    // the compiler execute this code before deploy views onCreateViewHolder and onBindViewHolder
    // for know how many must do. Al instanciar el adaptador con esta lista nula, logicamente no se
    // desplegara nada.
    @Override
    public int getItemCount() {
        return m_list != null ? m_list.size() : 0;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // return a new ItemViewHolder to which set a view inflated references a xml file in app resources
        return new ItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate
                        (R.layout.itemsong_layout, parent, false) // xml file
        );
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.onBindView(m_list.get(position));
    }

    // ---

    // INNER CLASS
    // ITEM HOLDER Represent and hold the interpreter of the PlayItem class in the UI
    static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //public static final String ARG_ID = "m_id";

        private PlayItem m_item;

        // wdgts
        //private FrameLayout m_image_layout;
        private TextView txv_display_item, txv_lenglife_item;

        // construct
        public ItemViewHolder(View itemView) {
            super(itemView);
//            txv_display_item  = (TextView) itemView.findViewById(R.id.txv_display_item);
//            txv_lenglife_item = (TextView) itemView.findViewById(R.id.txv_lenglife_item);
            itemView.setOnClickListener(ItemViewHolder.this);
        }

        // config on bind to view (just on starting)
        // PlayItem could be: Song, Folder or Playlist
        public void onBindView(PlayItem item/*, int position*/) {
            m_item = item;
            txv_display_item.setText(item.getDisplayName());
            if (item.getLengLife() != null) {
                txv_lenglife_item.setText(item.getLengLife());
            }
        }

        // listener onclik ui
        @Override
        public void onClick(View itemView) {
            m_item_recycler_click_listener.onItemRecyclerClick(m_item);
        }
    }

    // INTERFACES TO COMUNICATE

    /**
     * Created by Miguel Gonzalez on 10/09/2017.
     * Interface that allow us to bring access to the event of touch or click on each item for
     * implements the process consequent
     */
    public interface ItemRecyclerClickListener {
        void onItemRecyclerClick(PlayItem item);
    }

}
