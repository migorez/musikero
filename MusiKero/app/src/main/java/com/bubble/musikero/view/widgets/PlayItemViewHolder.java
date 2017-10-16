package com.bubble.musikero.view.widgets;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bubble.musikero.model.data.PlayItem;

/**
 * Created by Miguel on 10/10/2017.
 * ViewHolder. Es reutilizado por el adaptador de recyclerview, es decir, se crean unos cuantos que,
 * a medida que la lista se desplaza, se reutilizan los que ya han salido del margen de la pantalla,
 * para asi optimizar la memoria utilizada por la aplicacion.
 */
public class PlayItemViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener, View.OnLongClickListener {

    // fields
    private PlayItem m_play_item;

    /**
     * Envia las acciones de click repondidas por cada item de lista representado en esta clase*/
    private OnPlayItemViewHolderClickListener m_onPlayItemClick;
    public interface OnPlayItemViewHolderClickListener {
        void onPlayItemClick(PlayItem play_item);
        void onPlayItemLongClick(PlayItem play_item);
    }

    // Default Constructor
    public PlayItemViewHolder(View itemView) {
        super(itemView);
    }

    /***/
    public void onBindViewHolder(PlayItem play_item, OnPlayItemViewHolderClickListener onPlayItemClick) {
        if (play_item != null) {
            // le paso el View al objeto PlayItem para que lo setee con sus datos
            play_item.setViewHolder(this.itemView);
            this.itemView.setOnClickListener(this);
            this.itemView.setOnLongClickListener(this);
        } else {
            this.itemView.setOnClickListener(null);
            this.itemView.setOnLongClickListener(null);
        }
        // nulos o no asi se setearan los internos
        m_play_item = play_item;
        m_onPlayItemClick = onPlayItemClick;
    }

    public PlayItem getPlayItem() {
        return m_play_item;
    }

    @Override
    public void onClick(View v) {
        if (m_onPlayItemClick != null && m_play_item != null) {
            m_onPlayItemClick.onPlayItemClick(m_play_item);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (m_onPlayItemClick != null && m_play_item != null) {
            m_onPlayItemClick.onPlayItemLongClick(m_play_item);
        }
        return true;
    }

}
