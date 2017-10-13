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

import com.bubble.musikero.model.data.Folder;
import com.bubble.musikero.model.data.PlayItem;
import com.bubble.musikero.model.data.Playlist;
import com.bubble.musikero.model.data.Song;

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
    public static List<PlayItem> getDeviceSongs(Context context) {
        // handler that helps to show messages to the user from a thread
        UIMessager ui_messager = new UIMessager(context);
        Message msg = ui_messager.obtainMessage();
        Bundle bundle_msg = new Bundle();
        // retrieving the songs
        String storage_state = Environment.getExternalStorageState();
        if (storage_state.equals(Environment.MEDIA_MOUNTED) ||
                storage_state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            // parametros de la consulta al proveedor de contenidos
            String[] projection = new String[]{
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DURATION
            };
            String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1 AND " +
                    MediaStore.Audio.Media.DISPLAY_NAME + " LIKE ? OR " +
                    MediaStore.Audio.Media.DISPLAY_NAME + " LIKE ?"; // consulta sql regular
            String[] selectionArgs = new String[]{"%.mp3", "%.wav"}; // formatos elegidos
            // utilizamos un cursor de database (sirve igual)
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, // la uri hace referencia a un contentprovider que basicamente es un directorio convertido en tabla de datos de su contenido
                    projection, // las columnas elegidas (select id, name, etc.(cols) from ... - como en sql). null = select * (all)
                    selection,
                    selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) { // si definitivamente hay datos
                if (cursor.moveToFirst()) {
                    List<PlayItem> device_songs = new ArrayList<>();
                    do {
                        device_songs.add(new Song(
                                cursor.getLong(cursor.getColumnIndex(projection[0])),
                                cursor.getString(cursor.getColumnIndex(projection[1])),
                                cursor.getString(cursor.getColumnIndex(projection[2])),
                                cursor.getLong(cursor.getColumnIndex(projection[3]))
                        ));
                    } while (cursor.moveToNext());
                    cursor.close();
                    return device_songs;
                }
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

    /**
     * Static void that give us the list of folders of reproducible device's files.
     */
    @Nullable
    public static List<PlayItem> getPlayFolders(Context context) {
        List<PlayItem> device_songs = getDeviceSongs(context);
        if (device_songs != null) {
            List<PlayItem> play_folders = new ArrayList<>();
            // Se recorrera la lista de archivos reproducibles almacenados en el dispositivo,
            // recorriendo por cada item la lista de las carpetas, definidas estas por la ruta de
            // cada archivo, para asi agruparlos por las rutas de directorio que comparten,
            // defieniendo asi la lista de carpetas.
            Song song;
            Folder folder;
            boolean registered_folder;
            for (PlayItem song_item : device_songs) {
                song = (Song) song_item;
                registered_folder = false;
                for (PlayItem folder_item : play_folders) {
                    folder = (Folder) folder_item;
                    if (song.getFolderPath().equals(folder.getPath())) {
                        registered_folder = true;
                        folder.addMember(song);
                        break;
                    }
                }
                if (!registered_folder) {
                    folder = new Folder(song.getFolderName(), song.getFolderPath());
                    folder.addMember(song);
                    play_folders.add(folder);
                }
            }
            return play_folders;
        }
        return null;
    }

    /**
     *
     */
    @Nullable
    public static List<PlayItem> getFolderSongs(Context context, String folderPath) {
        // handler that helps to show messages to the user from a thread
        UIMessager ui_messager = new UIMessager(context);
        Message msg = ui_messager.obtainMessage();
        Bundle bundle_msg = new Bundle();
        //
        String storage_state = Environment.getExternalStorageState();
        if (storage_state.equals(Environment.MEDIA_MOUNTED) ||
                storage_state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            // parametros de la consulta al proveedor de contenidos
            String[] projection = new String[]{
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DURATION
            };
            String selection = MediaStore.Audio.Media.DATA + " LIKE ? AND " +
                    MediaStore.Audio.Media.DISPLAY_NAME + " LIKE ? OR " +
                    MediaStore.Audio.Media.DISPLAY_NAME + " LIKE ?";
            String[] selectionArgs = new String[]{folderPath + "%", "%.mp3", "%.wav"};
            //
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, // la uri hace referencia a un contentprovider que basicamente es un directorio convertido en tabla de datos de su contenido
                    projection, // las columnas elegidas (select id, name, etc.(cols) from ... - como en sql). null = select * (all)
                    selection,
                    selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) { // si definitivamente hay datos
                List<PlayItem> folderSongList = new ArrayList<>();
                do {
                    folderSongList.add(
                            new Song(
                            cursor.getLong(cursor.getColumnIndex(projection[0])),
                            cursor.getString(cursor.getColumnIndex(projection[1])),
                            cursor.getString(cursor.getColumnIndex(projection[2])),
                            cursor.getLong(cursor.getColumnIndex(projection[3]))
                    ));
                } while (cursor.moveToNext());
                cursor.close();
                return folderSongList;
            } else {
                bundle_msg.putString(KEY_HANDLER_MESSAGE, "No hay contenido en esta carpeta");
            }
        } else {
            bundle_msg.putString(KEY_HANDLER_MESSAGE, "Almacenamiento no disponible.");
        }
        msg.setData(bundle_msg);
        msg.sendToTarget();
        return null;
    }

    @Nullable
    public static List<PlayItem> getPlaylists(Context context) {
        // handler that helps to show messages to the user from a thread
        UIMessager ui_messager = new UIMessager(context);
        Message msg = ui_messager.obtainMessage();
        Bundle bundle_msg = new Bundle();
        String storage_state = Environment.getExternalStorageState();
        if (storage_state.equals(Environment.MEDIA_MOUNTED) ||
                storage_state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            String[] projection = new String[] {
                    MediaStore.Audio.Playlists._ID,
                    MediaStore.Audio.Playlists.NAME,
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

    /**
     * */
    @Nullable
    public static List<PlayItem> getPlaylistSongs(Context context, long playlist_id) {
        // handler that helps to show messages to the user from a thread
        UIMessager ui_messager = new UIMessager(context);
        Message msg = ui_messager.obtainMessage();
        Bundle bundle_msg = new Bundle();
        String storage_state = Environment.getExternalStorageState();
        if (storage_state.equals(Environment.MEDIA_MOUNTED) ||
                storage_state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            String[] projection = new String[] {
                    MediaStore.Audio.Playlists.Members.AUDIO_ID,
                    MediaStore.Audio.Playlists.Members.DISPLAY_NAME,
                    MediaStore.Audio.Playlists.Members.DATA,
                    MediaStore.Audio.Playlists.Members.DURATION
            };
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id),
                    projection,
                    null,
                    null,
                    null
            );
            if (cursor != null && cursor.moveToFirst()) {
                List<PlayItem> playlist_content = new ArrayList<>();
                do {
                    playlist_content.add(
                            new Song(
                            cursor.getLong(cursor.getColumnIndex(projection[0])),
                            cursor.getString(cursor.getColumnIndex(projection[1])),
                            cursor.getString(cursor.getColumnIndex(projection[2])),
                            cursor.getInt(cursor.getColumnIndex(projection[3]))
                    ));
                } while (cursor.moveToNext());
                cursor.close();
                return playlist_content;
            } else {
                bundle_msg.putString(KEY_HANDLER_MESSAGE, "No hay elementos guardados en esta lista.");
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

        UIMessager(Context context) {
            super(Looper.getMainLooper());
            m_context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(m_context, msg.getData().getString(KEY_HANDLER_MESSAGE), Toast.LENGTH_LONG).show();
        }
    }

}
