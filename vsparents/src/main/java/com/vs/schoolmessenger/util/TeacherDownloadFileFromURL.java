package com.vs.schoolmessenger.util;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.TeacherVoiceCircularPopup;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherMessageModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.TeacherUtil_UrlMethods.MSG_TYPE_VOICE;


/**
 * Created by voicesnap on 5/11/2017.
 */

public class TeacherDownloadFileFromURL {
    static ProgressDialog mProgressDialog;
    static TeacherMessageModel msgModel;
    static String msgType;
    static Boolean is_Archive;

    public static void downloadSampleFile(final Activity activity, TeacherMessageModel msg, final String folder, final String fileName, String type,Boolean archive) {
        msgModel = msg;
        msgType = type;
        is_Archive=archive;
        String url = msgModel.getMsgContent();

        Log.d("File URL", url);

        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(activity.getString(R.string.Downloading));
        mProgressDialog.setCancelable(false);
        if (!activity.isFinishing())
            mProgressDialog.show();

        Log.d("File URL", url);

         TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<ResponseBody> call = apiService.downloadFileWithDynamicUrlAsync(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("DOWNLOADING...", "server contacted and has file");

                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(), folder, fileName,activity);

                            Log.d("DOWNLOADING...", "file download was a success? " + writtenToDisk);
                            return writtenToDisk;
                        }

                        @Override
                        protected void onPostExecute(Boolean status) {
                            super.onPostExecute(status);
                            if (status) {
                                msgModel.setMsgContent(fileName);

                                if (msgType.equals(MSG_TYPE_VOICE)) {
                                    Intent inPdfPopup = new Intent(activity, TeacherVoiceCircularPopup.class);
                                    inPdfPopup.putExtra("VOICE_ITEM", msgModel);
                                    inPdfPopup.putExtra("is_Archive", is_Archive);
                                    activity.startActivity(inPdfPopup);
                                } else
                                    showAlert(activity, activity.getString(R.string.Success), activity.getString(R.string.File_stored) + folder + "/" + fileName);
                            }
                        }
                    }.execute();
                } else {
                    Log.d("failed","failed");
                    Log.d("DOWNLOADING...", "server contact failed");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("DOWNLOADING...", "error: " + t.toString());
            }
        });
    }

    public static boolean writeResponseBodyToDisk(ResponseBody body, String folder, String fileName,Activity activity) {
        try {

//            String filepath;
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
//            {
//                filepath=activity.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
//
//            }
//            else{
//                filepath = Environment.getExternalStorageDirectory().getPath();
//            }
//
//            File file = new File(filepath, folder);
//            File dir = new File(file.getAbsolutePath());
//
//            if (!dir.exists()) {
//                dir.mkdirs();
//                System.out.println("Dir: " + dir);
//            }
//
//            File futureStudioIconFile = new File(dir, fileName);//"Hai.mp3"

            final File dir;
            if (Build.VERSION_CODES.R > Build.VERSION.SDK_INT) {
                dir = new File(Environment.getExternalStorageDirectory().getPath()
                        + folder);
            } else {
                dir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath()
                        + folder);
            }
            System.out.println("body: " + body);

            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("Dir: " + dir);
            }

            File futureStudioIconFile = new File(dir, fileName);//"Hai.mp3"

            if (!futureStudioIconFile.exists())
            {
                File futureStudioIconFile1 = new File(dir, fileName);
                futureStudioIconFile=futureStudioIconFile1;
            }

            // todo change the file location/name according to your needs

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static void showToast(Activity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showAlert(final Activity activity, String title, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                activity);

        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.teacher_ic_atten);

        alertDialog.setNeutralButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        alertDialog.show();
    }

}
