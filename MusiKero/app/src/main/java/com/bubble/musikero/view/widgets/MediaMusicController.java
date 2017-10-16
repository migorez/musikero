package com.bubble.musikero.view.widgets;

import android.content.Context;
import android.view.View;
import android.widget.MediaController;

import com.bubble.musikero.controlador.player.MusicService;

/**
 * Created by Miguel on 13/10/2017.
 * Media Controller Class
 *
 */

public class MediaMusicController extends MediaController {

    private OnClickListener nextPlayListener, prevPlayListener;

    /**
     *
     */
    public MediaMusicController(Context context) {
        super(context, true);
    }

    /**
     *
     */
    public void setPlayerControlsListener(final MediaController.MediaPlayerControl mediaPlayerControl) {
        if (mediaPlayerControl != null) {
            setMediaPlayer(mediaPlayerControl);
            nextPlayListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MusicService) mediaPlayerControl).actionNext(null);
                }
            };
            prevPlayListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MusicService) mediaPlayerControl).actionPrev();
                }
            };
            setPrevNextListeners(nextPlayListener, prevPlayListener);
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        // not hide if this method is empty
        //super.hide(); // however the super method will make it
    }

    public void mHide() {
        super.hide();
    }

}
