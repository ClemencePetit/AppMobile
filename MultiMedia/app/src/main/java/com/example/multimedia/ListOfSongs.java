package com.example.multimedia;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

public class ListOfSongs {

    private ArrayList<String> paths = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();

    ListOfSongs(Context cxt, String criteria) {
        ContentResolver cr = cxt.getContentResolver();
        Uri songsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = cr.query(songsUri, new String[]{MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME}, criteria, null, sortOrder);

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0) != null) {
                    paths.add(cursor.getString(0));
                    names.add(cursor.getString(1));
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    public ArrayList<String> getPaths(){ return paths;}
    public ArrayList<String> getNames(){ return names;}
}
