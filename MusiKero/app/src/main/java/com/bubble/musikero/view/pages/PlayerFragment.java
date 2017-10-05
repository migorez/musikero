package com.bubble.musikero.view.pages;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bubble.musikero.R;
import com.bubble.musikero.controlador.Reproduccion.MusicPlayerService;
import com.bubble.musikero.model.structure.PlayItem;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {

    /*public static final PlayItem[] DEFAULT_PLAYLISTS = new PlayItem[]{
            new PlayItem(0, "Selección actual"),
            new PlayItem(1, "Reproducir tod"),
            new PlayItem(2, "Reproducir cola")
    };*/

    private OnFragmentInteractionListener mListener;

    private VinculadorServicio m_vinculador_servicio;
    private MusicPlayerService m_music_service_instance;
    private boolean m_servicio_vinculado;

    private MediaPlayer service_media_player;

    // widgets
    private Spinner spinner_play_list;
    private ImageButton btn_play_pause;
    private ImageButton btn_next;
    private ImageButton btn_prev;
    private ImageButton btn_stop;
    private SeekBar seekbar_player;
    private TextView lbl_title_present, txv_display_song;
    private TextView txv_time_playing, txv_duration_playing;
    // --

    private SpinnerAdapter m_spinner_playlist_adapter;

    // this void only can be used on onActivityCreated or later
    private void initComponents() {
        // fuente personalizada
        // http://trucosandroidstudio.blogspot.com.co/2015/03/como-cambiar-la-fuente-de-nuestra-app.html
        // http://trucosandroidstudio.blogspot.com.co/2015/03/como-crear-las-carpetas-raw-y-assets.html
        lbl_title_present = (TextView) getView().findViewById(R.id.w_lbl_title_present);
        txv_display_song  = (TextView) getView().findViewById(R.id.w_txv_display_song);
        /*Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/SIXTY.TTF"); // se refiere a la carpeta assets del proyecto
        lbl_title_present.setTypeface(typeface);
        txv_display_song.setTypeface(typeface);*/
        // player controls
        btn_play_pause       = (ImageButton) getView().findViewById(R.id.w_btn_play_pause);
        btn_next             = (ImageButton) getView().findViewById(R.id.w_btn_next);
        btn_prev             = (ImageButton) getView().findViewById(R.id.w_btn_prev);
        btn_stop             = (ImageButton) getView().findViewById(R.id.w_btn_stop);
        seekbar_player       = (SeekBar)     getView().findViewById(R.id.w_seekbar_player);
        txv_time_playing     = (TextView)    getView().findViewById(R.id.w_txv_time_playing);
        txv_duration_playing = (TextView)    getView().findViewById(R.id.w_txv_duration_playing);
        // registro callbacks
        btn_play_pause.setOnClickListener(PlayerFragment.this);
        btn_next.setOnClickListener(PlayerFragment.this);
        btn_prev.setOnClickListener(PlayerFragment.this);
        btn_stop.setOnClickListener(PlayerFragment.this);
        seekbar_player.setOnSeekBarChangeListener(PlayerFragment.this);
        //
        spinner_play_list = (Spinner)  getView().findViewById(R.id.w_spin_play_list);
        /*m_spinner_playlist_adapter = new SpinnerAdapter(getContext());
        spinner_play_list.setAdapter(m_spinner_playlist_adapter);
        spinner_play_list.post(new Runnable() {
            @Override
            public void run() {
                m_spinner_playlist_adapter.setItems(m_proveedor_contenido.getPlayLists());
            }
        });*/
    }

    static class SpinnerAdapter extends ArrayAdapter<PlayItem> {

        private LayoutInflater m_layout_inflater;

        private SpinnerAdapter(Context context) {
            super(context, R.layout.spinner_itemplaylist);
            m_layout_inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return setView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return setView(position, convertView, parent);
        }

        /***/
        private View setView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = m_layout_inflater.inflate(R.layout.spinner_itemplaylist, parent, false);
            } else {
                view = convertView;
            }
            PlayItem itemlist = getItem(position);
            /*((TextView)view.findViewById(R.id.w_spintxv_playlist_name)).
                    setText(itemlist.getPlaylist().getName());*/
            return view;
        }

        private void setItems(List<PlayItem> playlist_items) {
            clear();
            /*add(DEFAULT_PLAYLISTS[0]);
            add(DEFAULT_PLAYLISTS[1]);
            add(DEFAULT_PLAYLISTS[2]);*/
            if (playlist_items != null) {
                addAll(playlist_items);
            }
        }
    }

    // Construccion e Instanciamiento
    public PlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerFragment newInstance(/*String param1, int param2*/) {
         return new PlayerFragment();
    }
    // --

    // Ciclo de Vida del Fragmento
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            Log.d("PlayerFragment", context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        m_vinculador_servicio = new VinculadorServicio();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponents();
        // connect seekbar to the service´s mediaplayer
        // refs => https://stackoverflow.com/questions/6255965/android-media-player-seekbar
        // how bind a service => https://stackoverflow.com/questions/40024139/which-context-to-call-startservice
        getActivity().bindService(
                new Intent(getContext(), MusicPlayerService.class),
                m_vinculador_servicio,
                0 // flag of bind. if 1 start the services if this not exist, 0 dont do anything.
        );
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (m_servicio_vinculado) {
            service_media_player = m_music_service_instance.getPlayer();
            seekbar_player.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            seekbar_player.setProgress(service_media_player.getCurrentPosition());
                        }
                    },
                    1 * 1000 // min/millis
            );
            /*int i;
            for (i = 0; i < m_spinner_playlist_adapter.getCount(); i++) {
                if (m_music_service_instance.getActualPlaylistId() ==
                        m_spinner_playlist_adapter.getItem(i).getPlaylist().getId()) {
                    break;
                }
            }
            spinner_play_list.setSelection(i);*/
        }
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
        mListener = null;
    }

    // --

    // Interfaces del Fragmento
    // Propias
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /*// TODO: Rename method, update argument and hook method into UI event
    ejemplo de implementacion de una interfaz
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    // Implementadas
    // Control al ejecutar u oprimir sobre los componentes en la pantalla
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.w_btn_play_pause:
                /*Intent intent = new Intent(
                        MusicPlayerService.ACTION_PLAY,
                        null,
                        getContext(),
                        MusicPlayerService.class
                );
                int pos_item_sel =
                        spinner_play_list.getSelectedItemPosition() == AdapterView.INVALID_POSITION ?
                        0 : spinner_play_list.getSelectedItemPosition();
                intent.putExtra(MusicPlayerService.ARG_PLAYLIST_ID,
                        m_spinner_playlist_adapter.getItem(pos_item_sel).getPlaylist().getId()
                );
                getActivity().startService(intent);*/
                break;
            case R.id.w_btn_next:
                getActivity().startService(new Intent(MusicPlayerService.ACTION_NEXT));
                break;
            case R.id.w_btn_prev:
                getActivity().startService(new Intent(MusicPlayerService.ACTION_PREV));
                break;
            case R.id.w_btn_stop:
                getActivity().startService(new Intent(MusicPlayerService.ACTION_STOP));
                break;
            default:
        }
    }

    // callbacks del seekbar control
    // https://examples.javacodegeeks.com/android/core/widget/seekbar/android-seekbar-example/
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekbar_player.setProgress(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    // --

    // Clases Internas
    /**
     * Clase encargada de obtener la instancia del servicio para acceder a sus metodos
     * y variables publicas.*/
    private class VinculadorServicio implements ServiceConnection {

        public VinculadorServicio() {}

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.Conector conector = (MusicPlayerService.Conector) service;
            m_music_service_instance = conector.getMusicService();
            m_servicio_vinculado = m_music_service_instance != null ? true : false;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            m_servicio_vinculado = false;
        }
    }

}
