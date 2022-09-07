package com.vs.schoolmessenger.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaptureSignature extends Activity {
    LinearLayout mContent;
    Button mClear, mGetSign, mCancel;

    signature mSignature;

    private static final String SIGN_FOLDER = "School Voice/Sign";

    public int count = 1;
    public String current = null;
    private Bitmap mBitmap;
    View mView;
    File mypath;

    private String uniqueId;
//    String strSignImagePath = "";

    String strName = "";
    String strCirID = "", strAns = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomTitle();
        setContentView(R.layout.activity_capture_signature);

//        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }

        strCirID = getIntent().getExtras().getString("MSG_ID");
        strAns = getIntent().getExtras().getString("ANSWER");

        ImageView ivBack = (ImageView) findViewById(R.id.signature_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        TextView tvBarTitle = (TextView) findViewById(R.id.signature_ToolBarTvTitle);
//        tvBarTitle.setText("");

        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, SIGN_FOLDER);
        File dir = new File(file.getAbsolutePath());

        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Dir: " + dir);
        }
        uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();
        current = uniqueId + ".png";
        mypath = new File(dir, current);
        // End - My settings

        mContent = (LinearLayout) findViewById(R.id.sign_llSignLay);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        mClear = (Button) findViewById(R.id.sign_btnClear);
        mGetSign = (Button) findViewById(R.id.sign_btnSubmit);
        mGetSign.setEnabled(false);
        mCancel = (Button) findViewById(R.id.sign_btnCancel);

        mView = mContent;

        mClear.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Saved");
//				boolean error = captureSignature();
//				if (!error)
                {
                    mView.setDrawingCacheEnabled(true);
                    mSignature.save(mView);

                    uploadSignatureRetroFit();
                }
            }
        });

        mCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                Bundle b = new Bundle();
                b.putString("status", "cancel");
                Intent intent = new Intent();
                intent.putExtras(b);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.w("GetSignature", "onDestory");
        super.onDestroy();
    }

    private void setCustomTitle() {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 0);
        }
    }

    private void showToast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 105, 50);
        toast.show();
    }

    private boolean captureSignature() {

        boolean error = false;
        String errorMessage = "";

//		if (yourName.getText().toString().trim().equalsIgnoreCase(""))
//		{
//			errorMessage = errorMessage + "Please enter your Name\n";
//			error = true;
//		}

        if (error) {
            showToast(errorMessage);
        }

        return error;
    }

    private String getTodaysDate() {

        final Calendar c = Calendar.getInstance();
        int todaysDate = (c.get(Calendar.YEAR) * 10000)
                + ((c.get(Calendar.MONTH) + 1) * 100)
                + (c.get(Calendar.DAY_OF_MONTH));
        Log.w("DATE:", String.valueOf(todaysDate));
        return (String.valueOf(todaysDate));

    }

    private String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000)
                + (c.get(Calendar.MINUTE) * 100) + (c.get(Calendar.SECOND));
        Log.w("TIME:", String.valueOf(currentTime));
        return (String.valueOf(currentTime));

    }

//	private boolean prepareDirectory()
//	{
//		try
//		{
//			if (makedirs())
//			{
//				return true;
//			}
//			else
//			{
//				return false;
//			}
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			showToast("Could not initiate File System.. Is Sdcard mounted properly?");
//			return false;
//		}
//	}

//	private boolean makedirs()
//	{
//		File tempdir = new File(tempDir);
//		if (!tempdir.exists())
//			tempdir.mkdirs();
//
//		if (tempdir.isDirectory())
//		{
//			File[] files = tempdir.listFiles();
//			for (File file : files)
//			{
//				if (!file.delete())
//				{
//					System.out.println("Failed to delete " + file);
//				}
//			}
//		}
//		return (tempdir.isDirectory());
//	}

    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(mContent.getWidth(),
                        mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(mBitmap);
            try {
                FileOutputStream mFileOutStream = new FileOutputStream(mypath);

                v.draw(canvas);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();
                // This will draw image additionally  in DCIM/Camera/... location
//				strSignImagePath = Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);
//				Log.v("log_tag", "url: " + strSignImagePath);

                // In case you want to delete the file
                // boolean deleted = mypath.delete();
                // Log.v("log_tag","deleted: " + mypath.toString() + deleted);
                // If you want to convert the image to string use base64
                // converter

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    //
    private void uploadSignatureRetroFit() {

//        MessengerApiClient.BASE_URL = Util_SharedPreference.getBaseUrlFromSP(CaptureSignature.this);
        // MessengerApiClient.changeApiBaseUrl(Util_SharedPreference.getBaseUrlFromSP(CaptureSignature.this));

        String strChildID = Util_SharedPreference.getChildIdFromSP(CaptureSignature.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(CaptureSignature.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = new File(mypath.getPath());
//        File file = FileUtils.getFile(this, Uri.parse(recFilePath));

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

//        // add another part within the multipart request
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_InsertQueReply(strCirID, strChildID, strSchoolID, strAns);
//        String descriptionString = "hello, this is description speaking";
        RequestBody requestBody =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, jsonReqArray.toString());
        // Or
//        RequestBody requestContent =
//                RequestBody.create(
//                        MediaType.parse("multipart/form-data"), jsonReqArray.toString());

        final ProgressDialog mProgressDialog = new ProgressDialog(CaptureSignature.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        // finally, execute the request
//        JsonArray jsonReqArray = Util_JsonRequest.getJsonArray_InsertQueReply(strCirID, strChildID, strSchoolID, strAns);
        Call<JsonArray> call = apiService.SignUpload(requestBody, bodyFile);
//        Call<JsonObject> call = apiService.InsertQueReply(Util_UrlMethods.INSERT_QUE_REPLY, strCirID, strChildID, strSchoolID, strAns, bodyFile);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");


                            if ((strStatus).equals("1")) {
                                showToast(strMsg);
                                Bundle b = new Bundle();
                                b.putString("status", "done");
                                Intent intent = new Intent();
                                intent.putExtras(b);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                showToast(strMsg);
                            }
                        } else {

                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage());
            }
        });
    }


}
