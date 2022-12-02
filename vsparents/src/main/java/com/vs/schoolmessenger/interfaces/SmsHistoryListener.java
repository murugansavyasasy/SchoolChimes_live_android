package com.vs.schoolmessenger.interfaces;

import com.vs.schoolmessenger.model.SmsHistoryModel;

public interface SmsHistoryListener {
    void smsHistoryAddList(SmsHistoryModel contact);
    void smsHistoryRemoveList(SmsHistoryModel contact);
}
