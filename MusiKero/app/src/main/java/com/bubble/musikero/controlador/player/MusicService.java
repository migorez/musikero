package com.bubble.musikero.controlador.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.widget.MediaController;
import android.widget.Toast;

import com.bubble.musikero.R;
import com.bubble.musikero.model.PlayItemProvider;
import com.bubble.musikero.model.data.Folder;
import com.bubble.musikero.model.data.PlayItem;
import com.bubble.musikero.model.data.Playlist;
import com.bubble.musikero.model.data.Song;
import com.bubble.musikero.view.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicService extends Service implements
        MediaController.MediaPlayerControl,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener,
        Loader.OnLoadCompleteListener<List<Song>>, Loader.OnLoadCanceledListener<List<Song>> {

    // ATTRIBUTES

    private static final String TAG = "MusicService";

    // action constants
    public static final String BIND_MUSIC_SERVICE = "com.bubble.musikero.action.BIND_MUSIC_SERVICE";

    public static final String ACTION_PLAY = "com.bubble.musikero.action.PLAY";
    public static final String ACTION_STOP = "com.bubble.musikero.action.STOP";
    public static final String ACTION_PLAY_PAUSE = "com.bubble.musikero.action.PLAY_PAUSE";
    public static final String ACTION_NEXT = "com.bubble.musikero.action.NEXT";
    public static final String ACTION_PREV = "com.bubble.musikero.action.PREV";
    public static final String ACTION_ADDTAIL = "com.bubble.musikero.action.ADDTAIL";
    public static final String ACTION_PLAYALL = "com.bubble.musikero.action.PLAYALL";

    // params and args
    public static final String PLAYITEM_FOR_PLAYBACK_ARG = "playitem_to_playback";
    private static final int NO_PLAYBACK_LIST_ARG = -1;
    private static final float AUDIO_FOCUS_DUCK_VOLUME = 0.1f;
    private static final int NOTIFICATION_ID = 1;

    private static final int m_PLAYER_SESSION_ID_IS_RESET = 0;

    // fields
    private final IBinder m_musicServiceBinder = new MusicServiceBinder();

    private MediaPlayer m_mediaPlayer;
    private AudioManager m_audioManager;
    private long m_playbackListId;
    private PlaybackList m_playbackList;
    private PlayBackListLoader m_playBackListLoader;
    private AudioFocusState m_audioFocusState;
    private PlaybackState m_playbackState;

    private Notification m_notification;

    // INNER CLASS

    /**
     *
     */
    private enum AudioFocusState {
        Focused,
        NoFocusCanDuck,
        NoFocusNoDuck
    }

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
    private final class PlaybackList extends ArrayList<Song> {

        private Song actualSong;

        private boolean continuePlayback;
        private boolean randomPlayback;

        private List<Integer> sortOrderList;
        private int sortOrderListIndex;
        private Random randomizer;

        PlaybackList() {
            sortOrderList = new ArrayList<>();
            sortOrderListIndex = 0;
            randomizer = new Random();
        }

        /**
         *
         */
        /*void setFirstSortOrder(int firstPlaybackIndex) {
            sortOrderList = new ArrayList<>();
            sortOrderList.add(firstPlaybackIndex);
            sortOrderListIndex = 0;
        }*/
        @Override
        public boolean add(Song song) {
            if (sortOrderList == null)
                sortOrderList = new ArrayList<>();
            sortOrderList.add(song.getListPosition());
            actualSong = song;
            return super.add(song);
        }

        /**
         * El metodo va guardando la posicion del Song en la lista, en el array sortOrderList,
         * asi mismo guardando la posicion en que han sonado las canciones para poder retroceder o
         * avanzar para repetir recientes. Se guardara un arreglo de 10 canciones reproducidas
         * recientemente.
         */
        Song getRandomSong() {
            int random = randomizer.nextInt(size());
            if (sortOrderListIndex == 9) { // si contando el 0 ya tengo diez registros
                int[] aux = new int[9];
                for (int i = 0; i < sortOrderList.size() - 1; i++) {
                    aux[i] = sortOrderList.get((i + 1));
                }
                sortOrderList.clear();
                for (int anAux : aux) {
                    sortOrderList.add(anAux);
                }
                sortOrderList.add(random);
                sortOrderListIndex = (sortOrderList.size() - 1);
            } else if (sortOrderListIndex >= 0 && sortOrderListIndex < 9) {
                if (sortOrderListIndex < (sortOrderList.size() - 1)) {
                    sortOrderListIndex++;
                } else {
                    if (sortOrderList.size() < 10) {
                        sortOrderList.add(random);
                        sortOrderListIndex = (sortOrderList.size() - 1);
                    }
                }
            } else if (sortOrderListIndex < 0) {
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
                sortOrderListIndex = 0;
            }
            int songPosition = sortOrderList.get(sortOrderListIndex);
            actualSong = get(songPosition);
            return actualSong;
        }

        /**
         *
         * */
        Song getPrev() {
            if (sortOrderListIndex > 0) {
                sortOrderListIndex--;
                int songPosition = sortOrderList.get(sortOrderListIndex);
                actualSong = get(songPosition);
                return actualSong;
            } else {
                sortOrderListIndex = -1;
                return null;
            }
        }

        Song getActualSong() {
            return actualSong;
        }

    }

    /**
     * Loader playback playlist.
     */
    private class PlayBackListLoader extends AsyncTaskLoader<List<Song>> {

        private String m_folderPath;
        private Long m_playlistId;

        PlayBackListLoader(Context context) {
            super(context);
            this.m_folderPath = null;
            this.m_playlistId = null;
        }

        /**
         *
         */
        void loadPlaybackList(String folderPath, Long playlistId) {
            this.m_folderPath = folderPath;
            this.m_playlistId = playlistId;
            forceLoad();
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<Song> loadInBackground() {
            List<PlayItem> playItemSongs = null;
            if (m_folderPath != null) {
                playItemSongs = PlayItemProvider.getFolderSongs(getContext(), m_folderPath);
            } else if (m_playlistId != null) {
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

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public final class MusicServiceBinder extends Binder {

        MusicServiceBinder() {
        }

        public MusicService getMusicService() {
            return MusicService.this;
        }

    }

    // CONSTRUCT

    public MusicService() {
    }

    // LIFECYCLE

    @Override
    public void onCreate() {
        super.onCreate();
        m_audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        m_playBackListLoader = new PlayBackListLoader(getApplicationContext());
        m_playBackListLoader.registerListener(0, this);
        m_audioFocusState = AudioFocusState.NoFocusNoDuck;
        m_playbackState = PlaybackState.Stopped;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PlayItem playItemForPlayback = intent.getParcelableExtra(PLAYITEM_FOR_PLAYBACK_ARG);
        switch (intent.getAction()) {
            case ACTION_PLAY:
                actionPlay(playItemForPlayback);
                break;
            case ACTION_STOP:
                actionStop(true);
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
    public IBinder onBind(Intent intent) {
        return m_musicServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // no hacemos nada al desenlazar pues el servicio deberia seguir si tiene mas trabajo
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        relaxResources(true);
        leaveAudioFocus();
    }

    // IMPLEMENTED METHODS

    // MEDIAPLAYER CONTROLLER CALLBACKS
    @Override
    public void start() {
        actionPlayPause();
    }

    @Override
    public void pause() {
        actionPause();
    }

    @Override
    public int getDuration() {
        if (m_mediaPlayer != null)
            return m_mediaPlayer.getDuration();
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (m_mediaPlayer != null)
            return m_mediaPlayer.getCurrentPosition();
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (m_mediaPlayer != null)
            m_mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return m_mediaPlayer != null && m_mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    // LOADER SONGS LIST
    @Override
    public void onLoadComplete(Loader<List<Song>> loader, List<Song> data) {
        // al ejecutar el Loader este gatillara este metodo al retornar doInBacnkground. Si ha obtenido
        // una lista de playback se empezara a reproducir.
        if (data != null) {
            m_playbackList = new PlaybackList(); // may be null if are not storage for example
            m_playbackList.addAll(data);
        }
        actionNext(null); // next uri of playback_list if not is null
    }

    @Override
    public void onLoadCanceled(Loader<List<Song>> loader) {
        m_playbackList = null;
    }

    // AUDIO FOCUS MANAGE
    // No se ejecuta al pedirlo o dejarlo, solo al cambiar al ya tenerlo.
    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                m_audioFocusState = AudioFocusState.Focused;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                m_audioFocusState = AudioFocusState.NoFocusCanDuck;
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                m_audioFocusState = AudioFocusState.NoFocusNoDuck;
                break;
            default:
                break;
        }
        // no pasa por aqui cuando recien se pide el foco, antes de instanciar el MediaPlayer
        playbackStart();
    }

    // MEDIAPLAYER PLAYBACK CALLBACKS
    @Override
    public void onPrepared(MediaPlayer mp) {
        playbackStart();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        actionNext(null);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(getApplicationContext(), "Error en la reproducción\n" +
                "what = " + what + "; extra = " + extra, Toast.LENGTH_LONG).show();
        //actionNext(null);
        return false;
    }

    // ACTIONS COMMANDS

    /**
     * Encapsulated method action PLAY of service. Respond to the the Intent action pausing or playing
     * the playback.
     */
    private void actionPlay(PlayItem playItemForPlayback) {
        // al recien pedir el foco no se ejecuta el listener, solo se registra para que responda al
        // dispositivo.
        if (obtainAudioFocus()) {
            if (playItemForPlayback != null) {
                switch (playItemForPlayback.getItemType()) {
                    case Song.m_ITEMTYPE:
                        Song song = ((Song) playItemForPlayback);
                        if (m_playbackList == null) {
                            m_playbackList = new PlaybackList();
                        }
                        m_playbackList.add(song);
                        actionNext(song);
                        break;
                    case Folder.m_ITEMTYPE:
                        Folder folder = ((Folder) playItemForPlayback);
                        m_playBackListLoader.loadPlaybackList(folder.getPath(), null);
                        break;
                    case Playlist.m_ITEMTYPE:
                        Playlist playlist = ((Playlist) playItemForPlayback);
                        m_playBackListLoader.loadPlaybackList(null, playlist.getId());
                        break;
                    default:
                        break;
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
        if (m_playbackState == PlaybackState.Stopped) {
            actionPlay(null);
        } else if (m_playbackState == PlaybackState.Paused) {
            Song song = m_playbackList.getActualSong();
            setupAsForeground(song);
            playbackStart();
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
            relaxResources(false);
            m_playbackState = PlaybackState.Paused;
            // no abandonar el foco de audio. Probablemente se pierda solo.
        }
    }

    /**
     * At change the playlist in the player controller, the Loader load the new PlaybackList, and in
     * this method is accessed the next item.
     */
    public void actionNext(Song song) {
        try {
            m_playbackState = PlaybackState.Stopped;
            relaxResources(true);
            initMediaPlayer();
            if (song != null) {
                m_mediaPlayer.setDataSource(getApplicationContext(), song.getUri());
            } else if (m_playbackList != null && m_playbackList.size() > 0) {
                song = m_playbackList.getRandomSong();
                m_mediaPlayer.setDataSource(getApplicationContext(), song.getUri());
            } else {
                Toast.makeText(getApplicationContext(), "MusiKero\nNo hay más que reproducir.",
                        Toast.LENGTH_LONG).show();
                actionStop(false); // detengo todos los componentes menos el servicio en si.
                return;
            }
            setupAsForeground(song);
            m_mediaPlayer.prepare();
            //m_mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * */
    public void actionPrev() {
        if (m_playbackState == PlaybackState.Playing || m_playbackState == PlaybackState.Paused) {
            if (m_playbackList != null) {
                actionNext(m_playbackList.getPrev());
            }
        } else {
            actionNext(null);
        }
    }

    /**
     *
     */
    private void actionStop(boolean totalStop) {
        relaxResources(true); // cierro todos los componentes (cotificacion, player...)
        leaveAudioFocus();
        if (totalStop)
            stopSelf();
    }

    // OWN METHODS

    /**
     * Creates or reset the mediaplayer.
     */
    private void initMediaPlayer() {
        if (m_mediaPlayer == null) {
            m_mediaPlayer = new MediaPlayer();
            m_mediaPlayer.setOnPreparedListener(this);
            m_mediaPlayer.setOnCompletionListener(this);
            m_mediaPlayer.setOnErrorListener(this);
            m_mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        } else {
            m_mediaPlayer.reset();
        }
        m_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    /**
     * Try gain the audio focus for begin playback
     */
    private boolean obtainAudioFocus() {
        if (m_audioFocusState == AudioFocusState.Focused || m_audioManager.requestAudioFocus(
                this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) ==
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            m_audioFocusState = AudioFocusState.Focused;
            return true;
        }
        return false;
    }

    /**
     * Abandon the audio focus.
     */
    private boolean leaveAudioFocus() {
        return m_audioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    /**
     * Versatile method for start the mediaplayer.
     */
    public void playbackStart() {
        if (m_audioFocusState == AudioFocusState.NoFocusNoDuck) {
            if (m_mediaPlayer.isPlaying()) {
                m_mediaPlayer.pause();
                m_playbackState = PlaybackState.Paused;
            }
        } else {
            if (m_audioFocusState == AudioFocusState.NoFocusCanDuck) {
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
     * Configura para que se ejecute en primer plano y el sistema respete su proceso sobre otros
     * servicios que al correr en segundo plano son desechados antes al haber problemas de memoria.
     * Configura una notificacion que indica al usuario que el servicio esta activo.
     */
    private void setupAsForeground(Song songPlaying) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setContentIntent(pendingIntent)
                .setTicker(songPlaying.getDisplayName())
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(songPlaying.getDisplayName())
                .setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .setVibrate(new long[]{600, 200, 800, 300, 1200});
        m_notification = notificationBuilder.build();
        startForeground(NOTIFICATION_ID, m_notification);
    }

}
