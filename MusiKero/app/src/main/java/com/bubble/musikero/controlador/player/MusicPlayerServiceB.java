package com.bubble.musikero.controlador.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerServiceB extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    public static final String ACTION_PLAY = "com.bubble.musikero.action.PLAY";
    public static final String ACTION_STOP = "com.bubble.musikero.action.STOP";
    public static final String ACTION_PAUSE = "com.bubble.musikero.action.PAUSE";
    public static final String ACTION_NEXT = "com.bubble.musikero.action.NEXT";
    public static final String ACTION_PREV = "com.bubble.musikero.action.PREV";
    public static final String ACTION_ADDTAIL = "com.bubble.musikero.action.ADDTAIL";
    public static final String ACTION_PLAYALL = "com.bubble.musikero.action.PLAYALL";

    public static final String ARG_PLAYLIST_ID = "playlist_id";
    public static final String ARG_FOLDER_PLAYLIST_PATH = "folder_playlist_path";

    private static final float FOCO_AUDIO_DUCK_VOLUME = 0.1f;

    private IBinder m_service_conector;

    public enum AudioFocus {
        Focused,
        NoFocusCanDuck,
        NoFocusNoDuck
    }
    private AudioFocus m_audio_focus = AudioFocus.NoFocusNoDuck;

    private enum PlayingState {
        Stopped,
        Paused,
        Playing
    }
    private PlayingState m_playing_state = PlayingState.Stopped;

    private long m_playlist_id;

    private String m_folder_path;

    private List<Uri> m_playlist;

    private AudioManager m_audio_manager;

    private MediaPlayer m_player;

    private void initMediaPlayer() {
        if (m_player == null) {
            m_player = new MediaPlayer();
            m_player.setOnPreparedListener(this);
            m_player.setOnCompletionListener(this);
            m_player.setOnErrorListener(this);
        } else {
            m_player.reset();
        }
        m_player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    }

    // CICLO DE VIDA

    @Override
    public void onCreate() {
        super.onCreate();
        m_audio_manager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        m_service_conector = new Conector();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(ACTION_PLAY)) {
            play(intent.getData(), intent.getLongExtra(this.ARG_PLAYLIST_ID, 0),
                    intent.getStringExtra(this.ARG_FOLDER_PLAYLIST_PATH));
        } else if (action.equals(ACTION_STOP)) {
            stopPlayer();
        } else if (action.equals(ACTION_PAUSE)) {
            pausePlayer();
        } else if (action.equals(ACTION_NEXT)) {
            nextPlay(null);
        } else if (action.equals(ACTION_PREV)) {
            prevPlay();
        } else if (action.equals(ACTION_PLAYALL)) {

        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        leaveAudioFocus();
        liberarRecursos(true);
        m_playing_state = PlayingState.Stopped;
    }

    // IMPLEMENT METHODS AND INTERFACES

    /**
     * Metodo general confirmatorio de la obtencion del foco de la salida de audio del dispositivo*/
    private boolean getAudioFocus() {
        if (m_audio_focus != AudioFocus.Focused) {
            if (m_audio_manager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                m_audio_focus = AudioFocus.Focused;
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Metodo necesario para liberar recursos, en este caso el foco del audio del dispositivo*/
    private boolean leaveAudioFocus() {
        return m_audio_manager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    @Override
    public void onAudioFocusChange(int focusChange) { // este metodo no se lanza al pedir el foco al audio manager,
        // https://developer.android.com/guide/topics/media-apps/volume-and-earphones.html
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN: // no pasa por aqui cuando se lanza por primera vez
                m_audio_focus = AudioFocus.Focused;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                m_audio_focus = AudioFocus.NoFocusCanDuck;
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                m_audio_focus = AudioFocus.NoFocusNoDuck;
                break;
            default:
                break;
        }
        throwPlaying();
    }

    /**
     * Tocar o iniciar/pausar reproduccion*/
    private void throwPlaying() {
        if (m_audio_focus == AudioFocus.NoFocusNoDuck) {
            if (m_player.isPlaying()) {
                m_player.pause();
                m_playing_state = PlayingState.Paused;
            }
        } else {
            if (m_audio_focus == AudioFocus.NoFocusCanDuck) {
                // setVolume recibe dos valores float para un audio estereo, izq y der
                m_player.setVolume(FOCO_AUDIO_DUCK_VOLUME, FOCO_AUDIO_DUCK_VOLUME);  // we'll be relatively quiet
            } else {
                m_player.setVolume(1.0f, 1.0f); // we can be loud / podemos ser ruidosos
            }
            if (!m_player.isPlaying()) {
                m_player.start();
                m_playing_state = PlayingState.Playing;
            }
        }
    }

    private void liberarRecursos(boolean total_clean) {
        // http://www.truiton.com/2014/10/android-foreground-service-example/
        stopForeground(true); // foreground es una configuracion para interactuar con el servicio desde la barra de notificaciones
        if (m_player != null && total_clean){
            m_player.reset();
            m_player.release();
            m_player = null;
        }
    }

    /**
     * Decision de reproduccion*/
    private void play(Uri uri, long playlist_id, String folder_path) {
        if (getAudioFocus()) { // el ganar el foco al pedirlo al AudioManager no ejecuta la interfaz.
            if (m_playing_state == PlayingState.Stopped || uri != null || folder_path != null) {
                m_playlist_id = playlist_id; // por defecto sera 0 indicando la lista "Seleccion actual"
                m_folder_path = folder_path;
                nextPlay(uri);
            } else {
                // si viene de la ventana reproduccion verificara la seleccion de la lista de reproduccion.
                if (playlist_id != m_playlist_id) {
                    m_playlist_id = playlist_id;
                    nextPlay(null);
                    return;
                }
                if (m_playing_state == PlayingState.Paused){
                    throwPlaying();
                } else {
                    m_player.pause();
                    m_playing_state = PlayingState.Paused;
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Otra aplicación acapara el audio del dispositivo.",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Siguiente archivo a reproducir*/
    private void nextPlay(Uri uri) {
        m_playing_state = PlayingState.Stopped;
        liberarRecursos(false);
        initMediaPlayer();
        if (uri != null) {
            addToPlayList(uri);
        } else {
            uri = nextUriPlayBack();
            if (uri == null) {
                Toast.makeText(getApplicationContext(), "No hay mas que reproducir.",
                        Toast.LENGTH_LONG).show();
                stopSelf();
                return;
            }
        }
        try {
            m_player.setDataSource(getApplicationContext(), uri);
            m_player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            m_player.prepareAsync();
        } catch (IOException eh) {
            Toast.makeText(getApplicationContext(), "Error al conseguir el archivo",
                    Toast.LENGTH_LONG).show();
            eh.printStackTrace();
        }
    }

    /**
     * */
    @Nullable
    private Uri nextUriPlayBack() {
        /*if (m_playlist_id == PlayerFragment.DEFAULT_PLAYLISTS[0].getPlaylist().getId()) {
            if (m_folder_path != null) {
                fetchPlayList(m_proveedor_contenido.getSongList(m_folder_path));
            }
        } else if (m_playlist_id == PlayerFragment.DEFAULT_PLAYLISTS[1].getPlaylist().getId()) {
            fetchPlayList(m_proveedor_contenido.getSongList(null));
        }
        // si le especifico que reproduzca lo que hay en cola
        else if (m_playlist_id == PlayerFragment.DEFAULT_PLAYLISTS[2].getPlaylist().getId()) {

        } else {
            fetchPlayList(m_proveedor_contenido.getPlayListContent(m_playlist_id));
        }
        if (m_playlist != null && m_playlist.size() > 0) {
            Random random = new Random();
            return m_playlist.get(random.nextInt(m_playlist.size()));
        }*/
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        throwPlaying();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextPlay(null);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        initMediaPlayer();
        return true;
    }

    /**
     * Detener la reproduccion*/
    private void stopPlayer() {
        if (m_player != null && m_playing_state != PlayingState.Stopped && leaveAudioFocus()) {
            liberarRecursos(true);
            m_playing_state = PlayingState.Stopped;
            MusicPlayerServiceB.this.stopSelf();
        }
    }

    private void pausePlayer() {
        if (m_playing_state == PlayingState.Playing) {
            m_player.pause();
            m_playing_state = PlayingState.Paused;
        }
    }

    /**
     * */
    private void prevPlay() {
        if (m_player != null) {
            nextPlay(null);
        }
    }

    public void addToPlayList(Uri uri) {
        if (m_playlist == null) m_playlist = new ArrayList<>();
        m_playlist.add(uri);
    }

    /**
     * */
    public long getActualPlaylistId() {
        return m_playlist_id;
    }

    // BIND SERVICE

    @Override
    public IBinder onBind(Intent intent) {
        return m_service_conector;
    }

    public class Conector extends Binder {

        public Conector() {
            super();
        }

        public MusicPlayerServiceB getMusicService() {
            return MusicPlayerServiceB.this;
        }
    }

    // retorno el objeto MediaPlayer de la instancia del servicio para obtener su informacion en los
    // componentes que se enlacen al servicio
    public MediaPlayer getPlayer() {
        return m_player;
    }

}
