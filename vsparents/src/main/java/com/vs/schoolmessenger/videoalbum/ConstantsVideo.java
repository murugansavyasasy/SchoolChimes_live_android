package com.vs.schoolmessenger.videoalbum;

import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;

public class ConstantsVideo {

    public static final int PERMISSION_REQUEST_CODE = 1000;
    public static final int PERMISSION_GRANTED = 1001;
    public static final int PERMISSION_DENIED = 1002;

    public static final int REQUEST_CODE = 2000;

    public static final int FETCH_STARTED = 2001;
    public static final int FETCH_COMPLETED = 2002;
    public static final int ERROR = 2005;

    /**
     * Request code for permission has to be < (1 << 8)
     * Otherwise throws java.lang.IllegalArgumentException: Can only use lower 8 bits for requestCode
     */
    public static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 23;

    public static final String INTENT_EXTRA_ALBUM = "album";
    public static final String INTENT_EXTRA_IMAGES = "images";
    public static final String INTENT_EXTRA_LIMIT = "limit";
//    public static final int DEFAULT_LIMIT = 1;

    //Maximum number of images that can be selected at a time
    public static int limit;

    public static String getCount(Context c, String album_name, long albumId)
    {
        Uri uriExternal = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Uri uriInternal = MediaStore.Video.Media.INTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED };


//        Cursor cursorExternal = c.getContentResolver().query(uriExternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
//        Cursor cursorInternal = c.getContentResolver().query(uriInternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);


        Cursor cursorExternal = c.getContentResolver().query(uriExternal, projection, MediaStore.Video.Media.BUCKET_ID + "=?", new String[]{String.valueOf(albumId)}, null);
        Cursor cursorInternal = c.getContentResolver().query(uriInternal, projection, MediaStore.Video.Media.BUCKET_ID + "=?",new String[]{String.valueOf(albumId)}, null);
        Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});
        return cursor.getCount()+" Videos";


    }
}
