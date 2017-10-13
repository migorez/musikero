package com.bubble.musikero.view;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.bubble.musikero.R;
import com.bubble.musikero.controlador.player.MusicService;
import com.bubble.musikero.view.pages.FolderFragment;
import com.bubble.musikero.view.pages.PlaylistFragment;
import com.bubble.musikero.view.pages.SongFragment;

public class MainActivity extends AppCompatActivity {

    // bind service
    private MusicService m_musicService;
    private Intent       m_musicServiceIntent;
    private boolean      bindedService;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private PlayFragmentsAdapter play_fragments_adapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;

    // OFFERED INTERFACE
    private OnActivityInteractionListener m_interaction_listener;
    public interface OnActivityInteractionListener {
        void onKeyBackPressed();
    }

    // INNER CLASS

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     * Clase interna privada pues no necesita ser utilzada en ninguna otra parte
     *
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
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    SongFragment songFragment = SongFragment.newInstance();
                    //m_interaction_listener = songFragment;
                    return songFragment;
                case 1:
                    FolderFragment folderFragment = FolderFragment.newInstance();
                    m_interaction_listener = folderFragment;
                    return folderFragment;
                case 2:
                    PlaylistFragment playlistFragment = PlaylistFragment.newInstance();
                    //m_interaction_listener = playlistFragment;
                    return playlistFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.label_tab_songs);
                case 1:
                    return getResources().getString(R.string.label_tab_folders);
                case 2:
                    return getResources().getString(R.string.label_tab_playlists);
                /*case 3:
                    return getResources().getString(R.string.label_tab_player);*/
            }
            return null;
        }

        // SLIDING PANE
        // refs -> https://www.numetriclabz.com/implementation-of-sliding-up-panel-using-androidslidinguppanel-in-android-tutorial/
        // https://www.google.com.co/search?q=sliding+up+panel+android+example&oq=sliding+up+&gs_l=psy-ab.3.2.0j0i22i30k1l3.32149.32655.0.34932.3.3.0.0.0.0.186.533.0j3.3.0....0...1.1.64.psy-ab..0.3.532....0.FMCSb4YClAc
        // http://www.devexchanges.info/2015/05/making-sliding-up-panel-like-google.html
    }

    // Activity Life

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        play_fragments_adapter = new PlayFragmentsAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.play_pages_pager);
        viewPager.setAdapter(play_fragments_adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // METHODS AND IMPLEMENTED INTERFACES

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Se implementa el metodo de la actividad que responde a la accion sobre los controles
        // fisicos del dispositivo. Si es accionada la tecla de retroceso se enviara el evento
        // para ser repondido por el fragmento que implemente la interfaz.
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (m_interaction_listener != null) {
                m_interaction_listener.onKeyBackPressed();
                return true;
            }
        }
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
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

}
