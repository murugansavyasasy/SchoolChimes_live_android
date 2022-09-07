package com.vs.schoolmessenger.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.vs.schoolmessenger.activity.VoiceCircularPopup;
import com.vs.schoolmessenger.model.MessageModel;

import java.io.File;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_VOICE;

/**
 * Created by voicesnap on 5/15/2018.
 */

public class OfflineVoiceMessages {


    static MessageModel msgModel;
    static String msgType;
    public static void downloadSampleFile(final Activity activity, MessageModel msg, final String folder, final String fileName, String type) {
        msgModel = msg;
        msgType = type;
        String url = msgModel.getMsgContent();

        fetchSong(activity,folder,fileName);


    }

    private static void fetchSong(final Activity activity, String folder,String fileName) {
        Log.d("FetchSong", "Start***************************************");
        try {
            String filepath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filepath, folder);
            File dir = new File(file.getAbsolutePath());
            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("Dir: " + dir);
            }
            System.out.println("model: " +  msgModel.getMsgContent());


            File futureStudioIconFile = new File(dir,msgModel.getMsgTitle());

            System.out.println("futureStudioIconFile: " + futureStudioIconFile);


            System.out.println("msgType: " + msgType);
            System.out.println("MSG_TYPE_VOICE: " + MSG_TYPE_VOICE);

            if(!futureStudioIconFile.equals("")){


                if (msgType.equals(MSG_TYPE_VOICE)) {
                    Intent inPdfPopup = new Intent(activity, VoiceCircularPopup.class);
                    inPdfPopup.putExtra("VOICE_ITEM", msgModel);
                    activity.startActivity(inPdfPopup);
                }
            }
            else {
                Toast.makeText(activity, "Please connect the internet", Toast.LENGTH_SHORT).show();
            }
            System.out.println("FILE_PATH:" + futureStudioIconFile.getPath());



        } catch (Exception e) {
            Log.d("in Fetch Song", e.toString());
        }

        Log.d("FetchSong", "END***************************************");
    }


}

