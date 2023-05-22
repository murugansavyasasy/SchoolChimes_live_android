package com.vs.schoolmessenger.payment;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_PDF;
import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_VOICE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.VoiceCircularPopup;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.DownloadFileFromURL;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PdfWebView extends AppCompatActivity {
    WebView receiptWebView;
    String PdfURL = "", Title = "";
    TextView btnDownload;
    private static final String PDF_FEE_FOLDER = "//SchoolChimesFeeReceipt";
    private static final String PDF_CIRCULAR_FOLDER = "//SchoolChimesPDF";
    private static final String PDF_CERTIFICATE_FOLDER = "//SchoolChimesCertificates";

    static ProgressDialog mProgressDialog;


    private long downloadId;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_web_view);

        PdfURL = getIntent().getExtras().getString("URL", "");
        Title = getIntent().getExtras().getString("tittle", "");
        Log.d("URL",PdfURL);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(Title);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading receipt...");
        progressDialog.setCancelable(false);
        receiptWebView = findViewById(R.id.receiptWebView);
        btnDownload = findViewById(R.id.btnDownload);

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                String fileName = "certificate.pdf";
//
//               File dir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath() + "CERTIFICATES");
//               if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//
//                File futureStudioIconFile1 = new File(dir, fileName);
//                // https://developer.android.com/reference/android/app/DownloadManager.Request
//                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(PdfURL))
//                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                        .setTitle("downloaded")
//                        .setDescription("file")
//                        .setDestinationInExternalFilesDir(getApplicationContext(), dir.getPath(), fileName)
//                        .setTitle(fileName)
//                        .setMimeType("application/pdf");
//
//                DownloadManager dm = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
//                downloadId = dm.enqueue(request); // add download request to the queue


                if(Title.equals("Circular")){
                    long unixTime = System.currentTimeMillis() / 1000L;
                    String timeStamp = String.valueOf(unixTime);
                    downLoadFeeReceipt(timeStamp+"_Circular" +".pdf",PDF_CIRCULAR_FOLDER);
                }
                else if(Title.equals("Fee Receipt")) {
                    long unixTime = System.currentTimeMillis() / 1000L;
                    String timeStamp = String.valueOf(unixTime);
                    downLoadFeeReceipt(timeStamp+"_FeeReceipt" +".pdf",PDF_FEE_FOLDER);
                }
                else {
                    long unixTime = System.currentTimeMillis() / 1000L;
                    String timeStamp = String.valueOf(unixTime);
                    downLoadFeeReceipt(timeStamp+"_Certificates" +".pdf",PDF_CERTIFICATE_FOLDER);
                }

            }
        });

        receiptWebView.requestFocus();
        receiptWebView.getSettings().setJavaScriptEnabled(true);
        receiptWebView.getSettings().setBuiltInZoomControls(true);
        receiptWebView.getSettings().setSupportZoom(true);

        String url = "https://drive.google.com/viewerng/viewer?embedded=true&url=" + PdfURL;
        receiptWebView.loadUrl(url);
        receiptWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        receiptWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressDialog.show();
                }
                if (progress == 100) {
                    progressDialog.dismiss();
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }


    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the download ID received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            // Check if the ID matches our download ID
            if (downloadId == id) {
                Log.d("ID", "Download ID: " + downloadId);

                // Get file URI
                DownloadManager dm = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor c = dm.query(query);
                if (c.moveToFirst()) {
                    int colIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(colIndex)) {
                        Log.d("Complete", "Download Complete");
                        Toast.makeText(PdfWebView.this, "Download Complete", Toast.LENGTH_SHORT).show();

                        String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        Log.d("URI", "URI: " + uriString);
                    } else {
                        Log.d("status code", "Download Unsuccessful, Status Code: " + c.getInt(colIndex));
                        Toast.makeText(PdfWebView.this, "Download Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    private void downLoadFeeReceipt(String filename,String folder) {

        mProgressDialog = new ProgressDialog(PdfWebView.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Downloading...");
        mProgressDialog.setCancelable(false);
        if (!PdfWebView.this.isFinishing())
            mProgressDialog.show();

        Log.d("File URL", PdfURL);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<ResponseBody> call = apiService.downloadFileWithDynamicUrlAsync(PdfURL);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("DOWNLOADING...", "server contacted and has file");

                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(), folder, filename,PdfWebView.this);
                            Log.d("DOWNLOADING...", "file download was a success? " + writtenToDisk);
                            return writtenToDisk;
                        }

                        @Override
                        protected void onPostExecute(Boolean status) {
                            super.onPostExecute(status);
                            if (status) {
                                    showAlert(PdfWebView.this, "Success", "File stored in: " + folder + "/" + filename);
                            }
                        }
                    }.execute();
                } else {
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

            System.out.println("futureStudioIconFile: " + futureStudioIconFile);

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

    public static void showAlert(final Activity activity, String title, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                activity);

        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.ic_pdf);

        alertDialog.setNeutralButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }


}