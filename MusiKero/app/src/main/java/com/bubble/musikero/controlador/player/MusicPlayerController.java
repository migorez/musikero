package com.bubble.musikero.controlador.player;

import android.content.Context;
import android.view.View;
import android.widget.MediaController;

/**
 * Created by Miguel on 13/10/2017.
 */

public class MusicPlayerController extends MediaController implements MediaController.MediaPlayerControl {

    private MusicService m_musicService;

    public MusicPlayerController(Context context) {
        super(context, true);
        setMediaPlayer(this);
        setPrevNextListeners(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (m_musicService != null) {
                            m_musicService.controlNext();
                            MusicPlayerController.super.show(13000);
                        }
                    }
                },
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (m_musicService != null) {
                            m_musicService.controlPrev();
                            MusicPlayerController.super.show(13000);
                        }
                    }
                }
        );
    }

    public void setMusicPlayer(MusicService musicService) {
        this.m_musicService = musicService;
    }

    @Override
    public void hide() {
        // not hide if this method is empty
        super.hide();
    }

    // MediaPlayerControl callbacks

    @Override
    public void start() {
        m_musicService.controlPlayPause();
    }

    @Override
    public void pause() {
        m_musicService.controlPlayPause();
    }

    @Override
    public int getDuration() {
        return m_musicService.getPlaybackDuration();
    }

    @Override
    public int getCurrentPosition() {
        return m_musicService.getPlaybackPosition();
    }

    @Override
    public void seekTo(int pos) {
        m_musicService.setSeekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return m_musicService != null && m_musicService.isOnPlayback();
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

}
