package com.bubble.musikero.view;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.bubble.musikero.R;
import com.bubble.musikero.view.widgets.MediaMusicController;
import com.bubble.musikero.controlador.player.MusicService;
import com.bubble.musikero.model.data.PlayItem;
import com.bubble.musikero.view.pages.FolderFragment;
import com.bubble.musikero.view.pages.PlaylistFragment;
import com.bubble.musikero.view.pages.SongFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        PlayItemFragment.PlayItemFragmentCallbacks, View.OnTouchListener {

    // ATTRIBUTES AND CONSTANTS

    private static final List<PlayItemFragment> PLAYITEM_PAGES = Collections.unmodifiableList(
            new ArrayList<PlayItemFragment>() {
                {
                    add(SongFragment.newInstance());
                    add(FolderFragment.newInstance());
                    add(PlaylistFragment.newInstance());
                }
            }
    );

    private final MusicServiceConnetion m_musicServiceConnection = new MusicServiceConnetion();

    private MusicService m_musicService;

    private MediaMusicController m_mediaController;

    // INNER CLASS

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     * Clase interna privada pues no necesita ser utilzada en ninguna otra parte
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private class PlayFragmentsAdapter extends FragmentPagerAdapter {

        /**
         * Esta requiere de una instancia del FragmentManager que es la clase de la API de Android
         * que permite la interaccion con fragmentos, en este caso con los que utilizaremos en
         * nuestra aplicacion. A esta se puede acceder desde la instancia de una actividad, puesto
         * que justamente el fragmento es para añadirse sobre una actividad.
         * Un fragmento no puede ser usado aparte de una actividad
         * refs = https://developer.android.com/reference/android/app/Fragment.html
         * https://developer.android.com/guide/components/fragments.html
         */
        PlayFragmentsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PLAYITEM_PAGES.size();
        }

        @Override
        public Fragment getItem(int position) {
            return PLAYITEM_PAGES.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return PLAYITEM_PAGES.get(position).getTabTitle();
        }

    }

    /**
     * android ServiceConnection that look for get the instance of the MusicService.
     */
    private final class MusicServiceConnetion implements ServiceConnection {

        MusicServiceConnetion() {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicServiceBinder musicBinder = (MusicService.MusicServiceBinder) service;
            m_musicService = musicBinder.getMusicService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            m_musicService = null;
        }

    }

    // CONSTRUCT

    public MainActivity() {
    }

    // ACTIVITY LIFE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        findViewById(R.id.main_activity_container).setOnTouchListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        PlayFragmentsAdapter playFragmentsAdapter = new PlayFragmentsAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        // The ViewPager that will host the section contents.
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_activity_pager);
        viewPager.setAdapter(playFragmentsAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_activity_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // bind music service
        // le paso el contexto de la actividad para que se liberen los recursos cuando esta se cierre
        // intent refs => https://developer.android.com/guide/components/intents-filters.html?hl=es-419
        Intent musicService = new Intent();
        musicService.setClass(this, MusicService.class);
        musicService.setAction(MusicService.BIND_MUSIC_SERVICE);
        bindService(musicService, m_musicServiceConnection, BIND_AUTO_CREATE);
        initMediaController();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(m_musicServiceConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_mediaController = null;
    }

    // OWN AND IMPLEMENTED METHODS

    private void initMediaController() {
        if (m_musicService != null) {
            if (m_mediaController == null) {
                m_mediaController = new MediaMusicController(this);
                m_mediaController.setPlayerControlsListener(m_musicService);
                m_mediaController.setAnchorView(findViewById(R.id.main_activity_container));
            }
            m_mediaController.setEnabled(true);
            m_mediaController.show(/*13000*/); // default 3000 rested of my wished value
        }
    }

    // PLAYITEM FRAGMENTS CALLBACKS
    @Override
    public void onSelectPlayItemForPlayback(PlayItem playItem) {
        Intent musicServiceIntent = new Intent(this, MusicService.class);
        musicServiceIntent.setAction(MusicService.ACTION_PLAY);
        musicServiceIntent.putExtra(MusicService.PLAYITEM_FOR_PLAYBACK_ARG, playItem);
        startService(musicServiceIntent);
        bindService(musicServiceIntent, m_musicServiceConnection, BIND_AUTO_CREATE);
        initMediaController();
    }

    // touch screen listener
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (m_mediaController != null) {
            switch (v.getId()) {
                case R.id.main_activity_pager:
                    m_mediaController.mHide();
                    break;
                case R.id.main_activity_toolbar:
                    m_mediaController.show();
                    break;
                default:
                    break;
            }
        }
        Toast.makeText(this, "Haz dado un toque", Toast.LENGTH_SHORT).show();
        return true;
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //Toast.makeText(this, "Haz dado un toque", Toast.LENGTH_SHORT).show();

        if (m_mediaController != null && !m_mediaController.isShowing())
            m_mediaController.show(13000);
        return super.dispatchTouchEvent(ev);
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Se implementa el metodo de la actividad que responde a la accion sobre los controles
        // fisicos del dispositivo. Si es accionada la tecla de retroceso se enviara el evento
        // para ser repondido por el fragmento que implemente la interfaz.
        /*if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (m_interaction_listener != null) {
                m_interaction_listener.onKeyBackPressed();
                return true;
            }
        }*/
        // En cualquier caso se respondera normalmente a la accion sobre los controles que no nos importan.
        return super.onKeyDown(keyCode, event);
    }

    // MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the MainActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_main_stop_playback) {
            stopService(new Intent(this, MusicService.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
