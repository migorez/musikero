package com.bubble.musikero.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.bubble.musikero.model.structure.Folder;
import com.bubble.musikero.model.structure.PlayItem;
import com.bubble.musikero.model.structure.Playlist;
import com.bubble.musikero.model.structure.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miguel on 10/09/2017.
 */

public class PlayItemProvider {

    private static final String KEY_HANDLER_MESSAGE = "handler_message";

    // ATTRIBUTES

    // IMPLEMENT METHODS

    /**
     * Metodo general para la obtencion de las referencias ContentProvider de los archivos de musica
     * que alberga el dispositivo.
     */
    @Nullable
    public static synchronized List<PlayItem> getDeviceSongs(Context context) {
        // handler that helps to show messages to the user from a thread
        UIMessager ui_messager = new UIMessager(context);
        Message msg = ui_messager.obtainMessage();
        Bundle bundle_msg = new Bundle();
        // retrieving the songs
        String storage_state = Environment.getExternalStorageState();
        if (storage_state.equals(Environment.MEDIA_MOUNTED) ||
                storage_state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            // parametros de la consulta al proveedor de contenidos
            String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1 AND " +
                    MediaStore.Audio.Media.DISPLAY_NAME + " LIKE ? OR " +
                    MediaStore.Audio.Media.DISPLAY_NAME + " LIKE ?"; // consulta sql regular
            String[] selectionArgs = new String[]{"%.mp3", "%.wav"}; // formatos elegidos
            // utilizamos un cursor de database (sirve igual)
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, // la uri hace referencia a un contentprovider que basicamente es un directorio convertido en tabla de datos de su contenido
                    null, // las columnas elegidas (select id, name, etc.(cols) from ... - como en sql). null = select * (all)
                    selection,
                    selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) { // si definitivamente hay datos
                List<PlayItem> device_songs = new ArrayList<>();
                do {
                    device_songs.add(new Song(
                            cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                            cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    ));
                } while (cursor.moveToNext());
                cursor.close();
                return device_songs;
            } else {
                bundle_msg.putString(KEY_HANDLER_MESSAGE, "No hay contenido de música en el dispositivo.");
            }
        } else {
            bundle_msg.putString(KEY_HANDLER_MESSAGE, "Almacenamiento no disponible.");
        }
        msg.setData(bundle_msg);
        msg.sendToTarget();
        return null;
    }

    @Nullable
    public static List<PlayItem> getPlayFolders(Context context) {
        List<PlayItem> device_songs = getDeviceSongs(context);
        if (device_songs != null) {
            List<PlayItem> play_folders = new ArrayList<>();
            Song song;
            String last_folder_name = "";
            for (PlayItem playitem : device_songs) {
                song = (Song) playitem;
                if (!(song.getFolderName().equals(last_folder_name))) {
                    last_folder_name = song.getFolderName();
                    play_folders.add(new Folder(
                            song.getFolderName(),
                            song.getFolderPath())
                    );
                }
            }
            return play_folders;
        }
        return null;
    }

    @Nullable
    public static List<PlayItem> getPlaylists(Context context) {
        // handler that helps to show messages to the user from a thread
        UIMessager ui_messager = new UIMessager(context);
        Message msg = ui_messager.obtainMessage();
        Bundle bundle_msg = new Bundle();
        String storage_state = Environment.getExternalStorageState();
        if (storage_state.equals(Environment.MEDIA_MOUNTED)) {
            String[] projection = new String[] {
                    MediaStore.Audio.Playlists._ID,
                    MediaStore.Audio.Playlists.NAME
            };
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );
            if (cursor != null && cursor.moveToFirst()) {
                List<PlayItem> playlists = new ArrayList<>();
                do {
                    playlists.add(new Playlist(
                            cursor.getLong(cursor.getColumnIndex(projection[0])),
                            cursor.getString(cursor.getColumnIndex(projection[1]))
                    ));
                } while (cursor.moveToNext());
                cursor.close();
                return playlists;
            } else {
                bundle_msg.putString(KEY_HANDLER_MESSAGE, "No existen listas de reproducción guardadas.");
            }
        } else {
            bundle_msg.putString(KEY_HANDLER_MESSAGE, "Almacenamiento no disponible.");
        }
        msg.setData(bundle_msg);
        msg.sendToTarget();
        return null;
    }

    // INNER CLASS

    // manage messages to the ui, because in general the methods of this class will be used in a thread
    private static class UIMessager extends Handler {

        private Context m_context;

        public UIMessager(Context context) {
            super(Looper.getMainLooper());
            m_context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(m_context, msg.getData().getString(KEY_HANDLER_MESSAGE), Toast.LENGTH_LONG).show();
        }
    }

}
