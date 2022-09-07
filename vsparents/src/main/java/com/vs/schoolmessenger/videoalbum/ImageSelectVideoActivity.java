package com.vs.schoolmessenger.videoalbum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.assignment.VideoUpload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;


public class ImageSelectVideoActivity extends HelperVideoActivity {
    private ArrayList<Video> videos =new ArrayList<>();




    private String album;

    private TextView errorDisplay;
    private GridView gridView;
    private CustomImageSelectVideoAdapter adapter;


    private ActionBar actionBar;

    private ActionMode actionMode;
    private int countSelected;

    private ContentObserver observer;
    private Handler handler;
    private Thread thread;

    private final String[] projection = new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_select);
//        setView(findViewById(R.id.layout_image_select));


        actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear);
//
//            actionBar.setDisplayShowTitleEnabled(true);
//            actionBar.setTitle(R.string.image_view);
//        }

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }
        album = intent.getStringExtra(ConstantsVideo.INTENT_EXTRA_ALBUM);
        setTitle(album);

        errorDisplay = (TextView) findViewById(R.id.text_view_error);
        errorDisplay.setVisibility(View.INVISIBLE);


        gridView = (GridView) findViewById(R.id.grid_view_image_select);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (actionMode == null) {
                    actionMode = ImageSelectVideoActivity.this.startActionMode(callback);
                }
                toggleSelection(position);
                actionMode.setTitle(countSelected + " " + getString(R.string.selected));

                if (countSelected == 0) {
                    actionMode.finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ConstantsVideo.PERMISSION_GRANTED: {
                        loadImages();
                        break;
                    }

                    case ConstantsVideo.FETCH_STARTED: {

                        gridView.setVisibility(View.INVISIBLE);
                        break;
                    }

                    case ConstantsVideo.FETCH_COMPLETED: {
                        /*
                        If adapter is null, this implies that the loaded images will be shown
                        for the first time, hence send FETCH_COMPLETED message.
                        However, if adapter has been initialised, this thread was run either
                        due to the activity being restarted or content being changed.
                         */
                        if (adapter == null) {
                            adapter = new CustomImageSelectVideoAdapter(getApplicationContext(), videos);

                            gridView.setAdapter(adapter);


                            gridView.setVisibility(View.VISIBLE);
                            orientationBasedUI(getResources().getConfiguration().orientation);

                        } else {
                            adapter.notifyDataSetChanged();
                            /*
                            Some selected images may have been deleted
                            hence update action mode title
                             */
                            if (actionMode != null) {
                                countSelected = msg.arg1;
                                actionMode.setTitle(countSelected + " " + getString(R.string.selected));
                            }
                        }
                        break;
                    }

                    case ConstantsVideo.ERROR: {

                        errorDisplay.setVisibility(View.VISIBLE);
                        break;
                    }

                    default: {
                        super.handleMessage(msg);
                    }
                }
            }
        };
        observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                loadImages();
            }
        };
        getContentResolver().registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, false, observer);

        checkPermission();
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopThread();

        getContentResolver().unregisterContentObserver(observer);
        observer = null;

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(null);
        }
        videos = null;
        if (adapter != null) {
            adapter.releaseResources();
        }
        gridView.setOnItemClickListener(null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientationBasedUI(newConfig.orientation);
    }

    private void orientationBasedUI(int orientation) {
        final WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        if (adapter != null) {
            int size = orientation == Configuration.ORIENTATION_PORTRAIT ? metrics.widthPixels / 3 : metrics.widthPixels / 5;
            adapter.setLayoutParams(size);
        }
        gridView.setNumColumns(orientation == Configuration.ORIENTATION_PORTRAIT ? 3 : 5);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

            default: {
                return false;
            }
        }
    }

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.menu_contexual_action_bar, menu);


            actionMode = mode;
            countSelected = 0;

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int i = item.getItemId();
            if (i == R.id.menu_item_add_image) {
                sendIntent();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (countSelected > 0) {
                deselectAll();
            }
            actionMode = null;
        }
    };

    @SuppressLint("StringFormatInvalid")
    private void toggleSelection(int position) {
        if (!videos.get(position).isSelected && countSelected >= ConstantsVideo.limit) {
            Toast.makeText(
                    getApplicationContext(),
                    String.format(getString(R.string.limit_exceeded), ConstantsVideo.limit),
                    Toast.LENGTH_SHORT)
                    .show();
            return;


        }

        videos.get(position).isSelected = !videos.get(position).isSelected;

        if (videos.get(position).isSelected) {

            countSelected++;
        } else {
            countSelected--;
        }
        adapter.notifyDataSetChanged();
    }

    private void deselectAll() {
        for (int i = 0, l = videos.size(); i < l; i++) {
            videos.get(i).isSelected = false;
        }
        countSelected = 0;
        adapter.notifyDataSetChanged();
    }
    private ArrayList<String> getSelected() {
        ArrayList<String> selectedImages = new ArrayList<>();
        for (int i = 0; i < videos.size(); i++) {
            Log.d("imagesi", String.valueOf(i));
            if (videos.get(i).isSelected) {
                Log.d("selectedbol", String.valueOf(videos.get(i).isSelected));
                Log.d("iget", String.valueOf(videos.get(i)));

                selectedImages.add(videos.get(i).path);
                Log.d("paths", String.valueOf(selectedImages));

            }

        }
        return selectedImages;
    }

    private void sendIntent() {

        Intent intent = new Intent(ImageSelectVideoActivity.this, VideoUpload.class);
//        intent.putExtra("images", getSelected());
        intent.putStringArrayListExtra("images",getSelected());
        Log.d("selected", String.valueOf(getSelected()));
        setResult(RESULT_OK, intent);
        finish();
    }

    private void loadImages() {
        startThread(new ImageLoaderRunnable());
    }

    private class ImageLoaderRunnable implements Runnable {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            /*
            If the adapter is null, this is first time this activity's view is
            being shown, hence send FETCH_STARTED message to show progress bar
            while images are loaded from phone
             */
            if (adapter == null) {
                sendMessage(ConstantsVideo.FETCH_STARTED);
            }

            File file;
            HashSet<Long> selectedImages = new HashSet<>();
            if (videos != null) {
                Video video;
                for (int i = 0, l = videos.size(); i < l; i++) {
                    video = videos.get(i);
                    file = new File(video.path);
                    if (file.exists() && video.isSelected) {
                        selectedImages.add(video.id);
                    }
                }
            }

            Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{album}, MediaStore.Video.Media.DATE_ADDED);
            if (cursor == null) {
                sendMessage(ConstantsVideo.ERROR);
                return;
            }

            /*
            In case this runnable is executed to onChange calling loadImages,
            using countSelected variable can result in a race condition. To avoid that,
            tempCountSelected keeps track of number of selected images. On handling
            FETCH_COMPLETED message, countSelected is assigned value of tempCountSelected.
             */
            int tempCountSelected = 0;
            ArrayList<Video> temp = new ArrayList<>(cursor.getCount());
            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }

                    long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String path = cursor.getString(cursor.getColumnIndex(projection[2]));

                    boolean isSelected = selectedImages.contains(id);
                    if (isSelected) {
                        tempCountSelected++;
                    }

                    file = new File(path);
                    if (file.exists()) {
                        temp.add(new Video(id, name, path, isSelected));
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();

            if (videos == null) {
                videos = new ArrayList<>();
            }
            videos.clear();
            videos.addAll(temp);

            sendMessage(ConstantsVideo.FETCH_COMPLETED, tempCountSelected);
        }
    }

    private void startThread(Runnable runnable) {
        stopThread();
        thread = new Thread(runnable);
        thread.start();
    }

    private void stopThread() {
        if (thread == null || !thread.isAlive()) {
            return;
        }

        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(int what) {
        sendMessage(what, 0);
    }

    private void sendMessage(int what, int arg1) {
        if (handler == null) {
            return;
        }

        Message message = handler.obtainMessage();
        message.what = what;
        message.arg1 = arg1;
        message.sendToTarget();
    }

    @Override
    protected void permissionGranted() {
        sendMessage(ConstantsVideo.PERMISSION_GRANTED);
    }

    @Override
    protected void hideViews() {

        gridView.setVisibility(View.INVISIBLE);
    }

}



