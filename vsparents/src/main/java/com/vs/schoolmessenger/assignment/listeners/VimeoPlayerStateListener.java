package com.vs.schoolmessenger.assignment.listeners;

public interface VimeoPlayerStateListener {
    void onPlaying(float duration);

    void onPaused(float seconds);

    void onEnded(float duration);
}
