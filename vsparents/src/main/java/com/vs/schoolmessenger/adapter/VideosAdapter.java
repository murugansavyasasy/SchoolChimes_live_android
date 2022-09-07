package com.vs.schoolmessenger.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.VimeoVideoPlayerActivity;
import com.vs.schoolmessenger.assignment.view.DefaultControlPanelView;
import com.vs.schoolmessenger.assignment.view.VimeoPlayerView;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.VideoModelClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyViewHolder> {

    private List<VideoModelClass> lib_list;
    Context context;

    public
    void clearAllData() {
        int size = this.lib_list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.lib_list.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
//        public VimeoPlayerView card_video_image;
        public ImageView imgShadow,imgplay,imgVideo;
        public FrameLayout frmPlayVideo;
        public TextView title,createdon,createdby,description,tvnew;



        public MyViewHolder(View view) {
            super(view);
//            card_video_image = (VimeoPlayerView) view.findViewById(R.id.card_video_image);

            imgVideo = (ImageView) view.findViewById(R.id.imgVideo);
            imgShadow = (ImageView) view.findViewById(R.id.imgShadow);
            imgplay = (ImageView) view.findViewById(R.id.imgplay);
            frmPlayVideo = (FrameLayout) view.findViewById(R.id.frmPlayVideo);
            title = (TextView) view.findViewById(R.id.cardImage_tvTitle);
            createdon = (TextView) view.findViewById(R.id.cardImage_tvDate);
            createdby = (TextView) view.findViewById(R.id.cardImage_tvTime);
            description = (TextView) view.findViewById(R.id.tv_description_image);
            tvnew = (TextView) view.findViewById(R.id.cardImage_tvNew);

            }
    }

    public VideosAdapter(List<VideoModelClass> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public VideosAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_videos, parent, false);
        return new VideosAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VideosAdapter.MyViewHolder holder, final int position) {

        Log.d("listsizeeee", String.valueOf(lib_list.size()));
        final VideoModelClass data = lib_list.get(position);
        holder.createdon.setText(data.getCreatedOn());
        holder.createdby.setText(data.getCreatedBy());
        holder.title.setText(data.getTitle());
        if(data.getIsAppViewed().equals("0")){
            holder.tvnew.setVisibility(View.VISIBLE);
        }
        else{
            holder.tvnew.setVisibility(View.GONE);
        }
        if(data.getDescription().isEmpty()||data.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);
        }
        else{
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(data.getDescription());
        }
//        holder.card_video_image.setEnabled(false);
        holder.imgShadow.setEnabled(false);
//        DefaultControlPanelView defaultControlPanelView=new DefaultControlPanelView(holder.card_video_image);
//             defaultControlPanelView.dismissControls(1);
//        holder.card_video_image.initialize(true,data.getVimeoId());

        holder.imgShadow.setBackgroundColor(context.getResources().getColor(R.color.clr_white_fifty));



        Glide.with(context)
//                .load("http://img.youtube.com/vi/P3mAtvs5Elc/0.jpg")
                .load(R.drawable.video_parent_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.IMMEDIATE)
                .skipMemoryCache(false)
                .into(holder.imgVideo);

  holder.frmPlayVideo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//          if(data.getIsAppViewed().equals("0")) {
          Intent play=new Intent(context, VimeoVideoPlayerActivity.class);
          play.putExtra("VIDEO_ID",data.getIframe());
          play.putExtra("DETAILID",data.getDetailID());
          play.putExtra("ISAPPVIEW",data.getIsAppViewed());
          play.putExtra("is_Archive",data.getIs_Archive());
          context.startActivity(play);
//          }
//          String[] separated = data.getIframe().split("\\?(?!\\?)");
//
//          String name = separated[0];  //"/"
//          Log.d("name", name);
//
//          String FileName = separated[1];
//          Log.d("FileName", FileName);
//
//          String id=name.substring(name.lastIndexOf('/')+1);
//          Log.d("id",id);
//
//          String url=id+"/";
//          VimeoAPi(url);

      }
  });

    }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
    private void VimeoAPi(String url) {
//        OkHttpClient.Builder client = new OkHttpClient.Builder();

        OkHttpClient client1;
        client1 = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client1)
                .baseUrl("https://player.vimeo.com/video/"+url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        TeacherMessengerApiInterface service = retrofit.create(TeacherMessengerApiInterface.class);

        Call<JsonObject> call = service.Videoplay();

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if(mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                int res = response.code();
                Log.d("RESPONSE", String.valueOf(res));
                if (response.isSuccessful()) {
                    try {

                        Log.d("try","testtry");
                        JSONObject object1 = new JSONObject(response.body().toString());
                        JSONObject obj = object1.getJSONObject("request");
                        JSONObject files = obj.getJSONObject("files");

                        JSONArray progressive=files.getJSONArray("progressive");
                        JSONObject url = progressive.getJSONObject(0);
                        final String playurl=url.getString("url");


                        Log.d("Response sucess", playurl.toString());

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(playurl), "video/*");
                        context.startActivity(intent);

//                               myWebView.loadUrl(playurl);
//                        videoView.setVideoPath("https://vod-progressive.akamaized.net/exp=1591077888~acl=%2Fvimeo-prod-skyfire-std-us%2F01%2F4899%2F16%2F424498179%2F1837778844.mp4~hmac=5f2a6767d647d5f2a2390c71871abf1affb3bee6988b2a574a3272a13fb44833/vimeo-prod-skyfire-std-us/01/4899/16/424498179/1837778844.mp4");
//                        videoView.setVideoPath(playurl);
//                        videoView.start();
//                        myWebView.setWebChromeClient(new WebChromeClient() {
//                            public void onProgressChanged(WebView view, int progress) {
//                                Log.d("chrome", playurl);
//                                setProgress(progress * 100);
//                                if (progress == 100) {
//                                    myWebView.loadUrl(playurl);
//                                }
//                            }
//                        });
//                        myWebView.setWebViewClient(new WebViewClient() {
//                            @Override
//                            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
//                                Log.d("webview", playurl.toString());
//                webView.loadUrl(playurl);
//                                return true;
//                            }
//                        });

                    } catch (Exception e) {
                        Log.e("VIMEO Exception", e.getMessage());
                    }

                } else {
                    Log.d("Response fail", "fail");

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if(mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());

            }
        });
    }
}


