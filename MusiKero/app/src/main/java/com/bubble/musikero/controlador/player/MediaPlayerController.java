package com.bubble.musikero.controlador.player;

import android.content.Context;
import android.widget.MediaController;

/**
 * Created by Miguel on 13/10/2017.
 */

public class MediaPlayerController extends MediaController {

    public MediaPlayerController(Context context) {
        super(context, Boolean.TRUE);
    }
}
