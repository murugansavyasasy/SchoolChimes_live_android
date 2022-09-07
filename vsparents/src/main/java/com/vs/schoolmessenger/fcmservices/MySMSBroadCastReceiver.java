package com.vs.schoolmessenger.fcmservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class MySMSBroadCastReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)

    {

        Log.d("SMSOnReceive","SmsOnReceive");
        // Get Bundle object contained in the SMS intent passed in
        Bundle bundle = intent.getExtras();
        SmsMessage[] smsm = null;
        String sms_str ="";

        if (bundle != null)
        {
            // Get the SMS message
            Object[] pdus = (Object[]) bundle.get("pdus");
            smsm = new SmsMessage[pdus.length];
            for (int i=0; i<smsm.length; i++){

               // SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

                smsm[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                sms_str += "\r\nMessage: ";
                sms_str += smsm[i].getMessageBody().toString();
                sms_str+= "\r\n";
                String Sender = smsm[i].getOriginatingAddress();

                String numberOnly= sms_str.replaceAll("[^0-9]", "");

                //Check here sender is yours

                Intent smsIntent = new Intent("otp");
                smsIntent.putExtra("message",numberOnly);
                LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);

            }
        }
    }

    private String getVerificationCode(String sms_str) {
        String code = null;
        int index = sms_str.indexOf(Config.OTP_DELIMITER);

        if (index != -1) {
            int start = index + 2;
            int length = 6;
            code = sms_str.substring(start, start + length);
            return code;
        }

        return code;
    }
}
