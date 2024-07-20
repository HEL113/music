package com.mk.music.helongjie;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SongHelper {

    public static List<Song> list;

    @SuppressLint("Range")
    public static List<Song> getMusic(Context context) {
        list = new ArrayList<>();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        String selection = MediaStore.Audio.Media.DATA + " LIKE ?";
        String[] selectionArgs = new String[]{"%/storage/emulated/0/%"};
        Cursor cursor = resolver.query(musicUri, null, selection, selectionArgs, null);
        Log.e("SongHelper", "musicUri: "+musicUri);
        Log.e("SongHelper", "cursor: "+cursor);
        if (cursor != null) {
            //解析cursor数据，获得音乐列表
            Log.e("SongHelper", "cursor: "+cursor.moveToNext());
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                song.singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                song.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                song.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                song.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                list.add(song);
            }
        }else {
            Log.w("SongHelper", "Music cursor is NULL");
        }
        cursor.close();
        return list;
    }


}
