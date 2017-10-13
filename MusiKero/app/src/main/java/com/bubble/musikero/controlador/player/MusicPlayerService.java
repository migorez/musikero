package com.bubble.musikero.controlador.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.widget.Toast;

import com.bubble.musikero.model.PlayItemProvider;
import com.bubble.musikero.model.data.PlayItem;
import com.bubble.musikero.model.data.Song;
import com.bubble.musikero.view.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicPlayerService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener,
        Loader.OnLoadCompleteListener<List<Song>>, Loader.OnLoadCanceledListener<List<Song>> {

    // ATTRIBUTES

    private static final String TAG = "MusicPlayerService";

    // actions constants
    public static final String ACTION_PLAY       = "com.bubble.musikero.action.PLAY";
    public static final String ACTION_STOP       = "com.bubble.musikero.action.STOP";
    public static final String ACTION_PLAY_PAUSE = "com.bubble.musikero.action.PLAY_PAUSE";
    public static final String ACTION_NEXT       = "com.bubble.musikero.action.NEXT";
    public static final String ACTION_PREV       = "com.bubble.musikero.action.PREV";
    public static final String ACTION_ADDTAIL    = "com.bubble.musikero.action.ADDTAIL";
    public static final String ACTION_PLAYALL    = "com.bubble.musikero.action.PLAYALL";

    // params
    public static final String EXTRA_KEY_FOLDER_PATH_TO_PLAY = "_folder_path_to_play";
    public static final String EXTRA_KEY_PLAYLIST_ID_TO_PLAY = "_playlist_id_to_play";
    private static final int NO_PLAYBACK_LIST_ARG = -1;

    private static final float AUDIO_FOCUS_DUCK_VOLUME = 0.1f;

    private static final int NOTIFICATION_ID = 1;

    // fields
    /**
     * Essential list for playback
     */
    private long m_playBackListId;
    private PlaybackList m_playback_list;
    private PlayBackListLoader m_playBackListLoader;

    private AudioManager m_audioManager;

    private MediaPlayer m_mediaPlayer;

    private Notification m_notification;
    private NotificationManagerCompat m_notificationManagerCompat;

    // classes

    private AudioFocus m_audioFocus;
    /**
     *
     */
    private enum AudioFocus {
        Focused,
        NoFocusCanDuck,
        NoFocusNoDuck
    }

    private PlaybackState m_playbackState;
    /**
     *
     */
    private enum PlaybackState {
        Playing,
        Paused,
        Stopped
    }

    /**
     *
     */
    private class PlaybackList extends ArrayList<Song> {

        private List<Integer> sortOrderList;
        private int sortIndex;
        private Random randomizer;

        PlaybackList(List<Song> uris) {
            super(uris);
            sortOrderList = new ArrayList<>();
            sortIndex = 0;
            randomizer = new Random();
        }

        /**
         *
         * */
        /*void setFirstSortOrder(int ) {
            sortOrderList = new ArrayList<>();
            sortOrderList.add(ind_pos_cero);
            //sort[0] = ind_pos_cero;
            sortIndex = 0;
        }*/

        /**
         * El metodo va guardando la posicion del Song en la lista, en el array sortOrderList,
         * asi mismo guardando la posicion en que han sonado las canciones para poder retroceder o
         * avanzar para repetir recientes. Se guardara un arreglo de 10 canciones reproducidas
         * recientemente.
         */
        Uri getRandomUri() {
            int random = randomizer.nextInt(size());
            if (sortIndex == 9) { // si contando el 0 ya tengo diez registros
                int[] aux = new int[9];
                for (int i = 0; i < sortOrderList.size() - 1; i++) {
                    aux[i] = sortOrderList.get((i + 1));
                }
                sortOrderList.clear();
                for (int anAux : aux) {
                    sortOrderList.add(anAux);
                }
                sortOrderList.add(random);
                sortIndex = (sortOrderList.size() - 1);
            } else if (sortIndex >= 0 && sortIndex < 9) {
                if (sortIndex < (sortOrderList.size() - 1)) {
                    sortIndex++;
                } else {
                    if (sortOrderList.size() < 10) {
                        sortOrderList.add(random);
                        sortIndex = (sortOrderList.size() - 1);
                    }
                }
            } else if (sortIndex < 0) {
                //setPosCero(azar);
                if (sortOrderList.size() == 10) {
                    int[] aux = new int[9];
                    for (int i = sortOrderList.size() - 2; i >= 0; i--) { // menos dos porque size = diez menos dos = indice 8, el nueve lo descarto; corro la pila hacia adelante metiendo uno al principio y sacando el ultimo
                        aux[i] = sortOrderList.get(i);
                    }
                    sortOrderList.clear();
                    sortOrderList.add(random); // seteo el indice 0
                    for (int anAux : aux) {
                        sortOrderList.add(anAux); // aqui ya empieza desde el 1
                    }
                } else {
                    int[] aux = new int[sortOrderList.size()];
                    for (int i = sortOrderList.size() - 1; i >= 0; i--) {
                        aux[i] = sortOrderList.get(i);
                    }
                    sortOrderList.clear();
                    sortOrderList.add(random);
                    for (int anAux : aux) {
                        sortOrderList.add(anAux);
                    }
                }
                sortIndex = 0;
            }
            int songPosition = sortOrderList.get(sortIndex);
            return get(songPosition).getUri();
        }

        /**
         *
         * */
        Song getPrev() {
            if (sortIndex > 0) {
                sortIndex--;
                int songPosition = sortOrderList.get(sortIndex);
                return get(songPosition);
            } else {
                sortIndex = -1;
                return null;
            }
        }

    }

    /**
     * Loader playback playlist.
     */
    private class PlayBackListLoader extends AsyncTaskLoader<List<Song>> {

        private String m_folderPath;
        private long m_playlistId;

        PlayBackListLoader(Context context) {
            super(context);
            this.m_folderPath = null;
            this.m_playlistId = -1;
        }

        /**
         *
         */
        void loadPlayBackList(String folderPath, long playlistId) {
            this.m_folderPath = folderPath;
            this.m_playlistId = playlistId;
            forceLoad();
        }

        /**
         *
         */
        /*private List<Song> getSongsUris(List<Song> songs) {
            if (songs != null) {
                List<Uri> uris = new ArrayList<>();
                for (Song song : songs) {
                    uris.add(song.getUri());
                }
                return uris;
            }
            return null;
        }*/

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<Song> loadInBackground() {
            List<PlayItem> playItemSongs = null;
            if (m_folderPath != null) {
                playItemSongs = PlayItemProvider.getFolderSongs(getContext(), m_folderPath);
            } else if (m_playlistId != NO_PLAYBACK_LIST_ARG) {
                playItemSongs = PlayItemProvider.getPlaylistSongs(getContext(), m_playlistId);
            }
            if (playItemSongs != null) {
                List<Song> songs = new ArrayList<>();
                for (PlayItem playItemSong : playItemSongs) {
                    songs.add((Song) playItemSong);
                }
                return songs;
            }
            return null;
        }

    }

    // CONSTRUCT

    public MusicPlayerService() {
    }

    // LIFECYCLE

    @Override
    public void onCreate() {
        super.onCreate();
        m_audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        m_playBackListLoader = new PlayBackListLoader(getApplicationContext());
        m_playBackListLoader.registerListener(0, this);
        m_audioFocus = AudioFocus.NoFocusNoDuck;
        m_playbackState = PlaybackState.Stopped;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // may null, so prove before
        Uri uri = intent.getData();
        Bundle extras = intent.getExtras();
        // action decision
        switch (intent.getAction()) {
            case ACTION_PLAY:
                actionPlay(extras, uri);
                break;
            case ACTION_STOP:
                actionStop();
                break;
            case ACTION_PLAY_PAUSE:
                actionPlayPause();
                break;
            case ACTION_NEXT:
                actionNext(null);
                break;
            case ACTION_PREV:
                actionPrev();
                break;
            case ACTION_PLAYALL:
                break;
            default:
                break;
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        relaxResources(true);
        leaveAudioFocus();
    }

    // CREATED METHODS

    /**
     *
     */
    private void initMediaPlayer() {
        if (m_mediaPlayer == null) {
            m_mediaPlayer = new MediaPlayer();
            m_mediaPlayer.setOnPreparedListener(this);
            m_mediaPlayer.setOnCompletionListener(this);
            m_mediaPlayer.setOnErrorListener(this);
            m_mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        } else
            m_mediaPlayer.reset();
    }

    /**
     *
     */
    private void startMediaPlayer() {
        if (m_audioFocus == AudioFocus.NoFocusNoDuck) {
            if (m_mediaPlayer.isPlaying()) {
                m_mediaPlayer.pause();
                m_playbackState = PlaybackState.Paused;
            }
        } else {
            if (m_audioFocus == AudioFocus.NoFocusCanDuck) {
                // setVolume recibe dos valores float para un audio estereo, izq y der
                // we'll be relatively quiet
                m_mediaPlayer.setVolume(AUDIO_FOCUS_DUCK_VOLUME, AUDIO_FOCUS_DUCK_VOLUME);
            } else {
                m_mediaPlayer.setVolume(1.0f, 1.0f); // we can be loud / podemos ser ruidosos
            }
            if (!m_mediaPlayer.isPlaying()) {
                m_mediaPlayer.start();
                m_playbackState = PlaybackState.Playing;
            }
        }
    }

    /**
     * Encapsulated method action PLAY of service. Respond to the the Intent action pausing or playing
     * the playback.
     */
    private void actionPlay(Bundle bundle, Uri uri) {
        // al recien pedir el foco no se ejecuta el listener, solo se registra para que responda al
        // dispositivo.
        if (getAudioFocus()) {
            // from Song item or PlayerFragment the bundle will be null
            if (uri != null) { // just Song give me an uri
                actionNext(uri);
            } else if (bundle != null) {
                // Folder, Playlist and Player send a reference for fetch a list of Uris for playback
                m_playBackListLoader.loadPlayBackList(
                        bundle.getString(EXTRA_KEY_FOLDER_PATH_TO_PLAY, null),
                        bundle.getLong(EXTRA_KEY_PLAYLIST_ID_TO_PLAY, NO_PLAYBACK_LIST_ARG)
                );
            } else {
                if (m_playbackState == PlaybackState.Paused) {
                    setupAsForeground("Despausa");
                    startMediaPlayer();
                    m_playbackState = PlaybackState.Playing;
                } else if (m_playbackState == PlaybackState.Stopped) {
                    actionNext(null);
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Otra aplicación acapara el audio del dispositivo.",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     *
     */
    private void actionPlayPause() {
        if (m_playbackState == PlaybackState.Paused || m_playbackState == PlaybackState.Stopped) {
            actionPlay(null, null);
        } else {
            actionPause();
        }
    }

    /**
     *
     */
    private void actionPause() {
        if (m_playbackState == PlaybackState.Playing) {
            m_mediaPlayer.pause();
            m_playbackState = PlaybackState.Paused;
            relaxResources(false);
            // no abandonar el foco de audio. Probablemente se pierda solo.
        }
    }

    /**
     * At change the playlist in the PlayerFragment, the Loader load the new PlaybackList, and in
     * this method is accessed the next item.
     */
    private void actionNext(Uri uri) {
        try {
            m_playbackState = PlaybackState.Stopped;
            initMediaPlayer();
            relaxResources(false);
            m_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (uri != null) {
                m_mediaPlayer.setDataSource(getApplicationContext(), uri);
            } else if (m_playback_list != null && m_playback_list.size() > 0) {
                m_mediaPlayer.setDataSource(getApplicationContext(), m_playback_list.getRandomUri());
            } else {
                Toast.makeText(getApplicationContext(), "\tMusiKero\nNo más que reproducir.",
                        Toast.LENGTH_LONG).show();
                actionStop();
                return;
            }
            setupAsForeground("Siguiente Canción...");
            m_mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * */
    private void actionPrev() {
        actionNext(null);
    }

    /**
     *
     */
    private void actionStop() {
        relaxResources(true);
        leaveAudioFocus();
        stopSelf();
    }

    /**
     * Hace que el servicio deje de estar en primer plano, esto se hace al pausar o detener el
     * reproductor, para que en caso de baja memoria el servicio se pueda matar. Se configura
     * como servicio en primer plano para que el sistema respete su proceso, a diferencia de un
     * servicio normal que por defecto corren en background.
     *
     * @param freePlayer boolean that indicates if free also the MediaPlayer
     */
    private void relaxResources(boolean freePlayer) {
        stopForeground(true);
        if (freePlayer && m_mediaPlayer != null) {
            m_mediaPlayer.reset();
            m_mediaPlayer.release();
            m_mediaPlayer = null;
        }
    }

    /**
     *
     */
    private boolean getAudioFocus() {
        if (m_audioFocus != AudioFocus.Focused) {
            if (m_audioManager.requestAudioFocus
                    (this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) ==
                    AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                m_audioFocus = AudioFocus.Focused;
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     *
     */
    private boolean leaveAudioFocus() {
        return m_audioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    /**
     * Configura para que se ejecute en primer plano y el sistema respete su proceso sobre otros
     * servicios que al correr en segundo plano son desechados antes al haber problemas de memoria.
     * Configura una notificacion que indica al usuario que el servicio esta activo.
     */
    private void setupAsForeground(String tickerText) {
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setContentIntent(pendingIntent)
                .setTicker(tickerText)
                .setContentTitle("Musikero")
                .setContentText("Reproduciendo...")
                .setWhen(System.currentTimeMillis())
                .setVibrate(new long[]{900, 400, 1100, 700, 1400, 500, 2200});
        notificationBuilder.mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        m_notification = notificationBuilder.build();
        startForeground(NOTIFICATION_ID, m_notification);
    }

    /**
     * Set the continuous playback list to play in background
     */
    public void setPlayBackListId(long playbackListId) {
        this.m_playBackListId = playbackListId;
    }

    // IMPLEMENTS METHODS

    // Audio focus managing
    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                m_audioFocus = AudioFocus.Focused;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                m_audioFocus = AudioFocus.NoFocusCanDuck;
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                m_audioFocus = AudioFocus.NoFocusNoDuck;
                break;
            default:
                break;
        }
        // no pasa por aqui cuando recien se pide el foco, antes de instanciar el MediaPlayer
        startMediaPlayer();
    }

    // MediaPlayer callbacks
    @Override
    public void onCompletion(MediaPlayer mp) {
        actionNext(null);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        startMediaPlayer();
    }

    // Loader callbacks
    @Override
    public void onLoadComplete(Loader<List<Song>> loader, List<Song> data) {
        // al ejecutar el Loader este gatillara este metodo al retornar doInBacnkground. Si ha obtenido
        // una lista de playback se empezara a reproducir.
        if (data != null) {
            m_playback_list = new PlaybackList(data); // may be null if are not storage for example
        }
        actionNext(null); // next uri of playback_list if not is null
    }

    @Override
    public void onLoadCanceled(Loader<List<Song>> loader) {
        m_playback_list = null;
    }

    // BINDER METHODS

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
