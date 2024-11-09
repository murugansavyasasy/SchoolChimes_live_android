package com.vs.schoolmessenger.util;

public class ScreenState {
    private static ScreenState instance;
    private boolean isIncomingCallScreen = false;

    private ScreenState() {

    }

    public static synchronized ScreenState getInstance() {
        if (instance == null) {
            instance = new ScreenState();
        }
        return instance;
    }

    public boolean isIncomingCallScreen() {
        return isIncomingCallScreen;
    }

    public void setIncomingCallScreen(boolean isIncomingCallScreen) {
        this.isIncomingCallScreen = isIncomingCallScreen;
    }
}