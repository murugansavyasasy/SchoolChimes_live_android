package com.vs.schoolmessenger.interfaces;

import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.model.SmsHistoryModel;

public interface VoiceHistoryListener {

    void voiceHistoryAddList(MessageModel contact);

    void voiceHistoryRemoveList(MessageModel contact);
}

