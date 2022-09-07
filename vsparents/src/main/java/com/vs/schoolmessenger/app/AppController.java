package com.vs.schoolmessenger.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.util.LruBitmapCache;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;


/**
 * Created by voicesnap on 8/31/2016.
 */

public class AppController extends Application
{
    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

//        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
//        appSignatureHelper.getAppSignatures();

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/zawgyione.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

       // Thread.setDefaultUncaughtExceptionHandler(handleAppCrash);
        mInstance = this;
    }




//    private Thread.UncaughtExceptionHandler handleAppCrash =
//            new Thread.UncaughtExceptionHandler() {
//                @Override
//                public void uncaughtException(Thread thread, Throwable ex) {
//                    Log.e("error", ex.toString());
//                    //send email here
//                    Intent   intent = new Intent(Intent.ACTION_SEND);
//                   intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"murugan@voicesnap.com"});
//                    intent.putExtra(Intent.EXTRA_SUBJECT, "SchoolApp_Crash Report");
//                    intent.putExtra(Intent.EXTRA_TEXT, ex.toString());
//                    intent.setType("message/rfc822");
//                    startActivity(Intent.createChooser(intent, "Crash Report send to email"));
//                }
//            };

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}