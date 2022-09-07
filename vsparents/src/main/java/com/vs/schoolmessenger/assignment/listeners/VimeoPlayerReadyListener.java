package com.vs.schoolmessenger.assignment.listeners;


import com.vs.schoolmessenger.assignment.model.TextTrack;

public interface VimeoPlayerReadyListener {
    void onReady(String title, float duration, TextTrack[] textTrackArray);

    void onInitFailed();
}
