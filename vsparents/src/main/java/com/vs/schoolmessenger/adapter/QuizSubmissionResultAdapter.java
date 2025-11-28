package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.PreviewActivity;
import com.vs.schoolmessenger.model.QuizSubmissions;
import java.util.ArrayList;

public class QuizSubmissionResultAdapter extends RecyclerView.Adapter<QuizSubmissionResultAdapter.MyViewHolder> {

    private ArrayList<QuizSubmissions> textDataList;
    private Context context;

    public QuizSubmissionResultAdapter(ArrayList<QuizSubmissions> textDataList, Context context) {
        this.textDataList = textDataList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.submissions_quiz_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return textDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView lblqueno, lblque, lblcorrectans;
        ImageView imgview, imgVideo, imgShadow, imgplay;
        LinearLayout containerOptions, lnrPDFtext;
        FrameLayout videoview;
        TextView lblPDF;
        WebView webview;

        public MyViewHolder(View view) {
            super(view);
            lblqueno = view.findViewById(R.id.lblqueno);
            lblque = view.findViewById(R.id.lblque);
            lblcorrectans = view.findViewById(R.id.lblcorrectans);
            containerOptions = view.findViewById(R.id.containerOptions);
            webview = view.findViewById(R.id.webview);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        QuizSubmissions menu = textDataList.get(position);

        holder.containerOptions.removeAllViews();

        holder.lblqueno.setText((position + 1) + " .");
        holder.lblque.setText(menu.getQuestion());
        holder.lblcorrectans.setText("Correct Answer : " + menu.getCorrectAnswer());

        if (menu.getqImage().isEmpty()) {
            holder.webview.setVisibility(View.GONE);
        } else {
            holder.webview.setVisibility(View.VISIBLE);
            loadFileInWebView(holder.webview, menu.getqImage());
        }
        final GestureDetector gestureDetector = new GestureDetector(holder.webview.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                openPreviewImage(menu.getqImage());
                return true;
            }
        });

        holder.webview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });


        ArrayList<String> options = new ArrayList<>();
        options.add(menu.getaOption());
        options.add(menu.getbOption());
        options.add(menu.getcOption());
        options.add(menu.getdOption());

        ArrayList<String> optionImages = new ArrayList<>();
        optionImages.add(menu.getaImage());
        optionImages.add(menu.getbImage());
        optionImages.add(menu.getcImage());
        optionImages.add(menu.getdImage());

        for (int i = 0; i < options.size(); i++) {
            String ansId = "";
            String ansText = "";

            if (options.get(i) != null && options.get(i).contains("~")) {
                String[] split = options.get(i).split("~");
                ansId = split[0];
                ansText = split.length > 1 ? split[1] : "";
            }

            View optionView = LayoutInflater.from(context).inflate(R.layout.item_quiz_option, holder.containerOptions, false);

            LinearLayout itemContainer = optionView.findViewById(R.id.itemContainer);
            TextView txtOption = optionView.findViewById(R.id.txtOption);
            TextView txtContent = optionView.findViewById(R.id.txtContent);
            ImageView imgCheck = optionView.findViewById(R.id.imgCheck);
            ImageView imgAttachment = optionView.findViewById(R.id.imgAttachment);
            TextView txtAttachmentName = optionView.findViewById(R.id.txtAttachmentName);
            ConstraintLayout rlyCard = optionView.findViewById(R.id.rlyCard);
            LinearLayout lnrAttachment = optionView.findViewById(R.id.lnrAttachment);
            txtOption.setText(String.valueOf((char) ('A' + i)));
            txtContent.setText(ansText);
            String url = optionImages.get(i);

            imgAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openPreviewImage(url);
                }
            });

            if (url != null && !url.isEmpty()) {
                lnrAttachment.setVisibility(View.VISIBLE);
                Glide.with(context).load(url).into(imgAttachment);
                txtAttachmentName.setText("Option " + txtOption.getText());
            } else {
                lnrAttachment.setVisibility(View.GONE);
            }
            boolean isStudentAns = ansId.equals(menu.getStudentAnswer());
            boolean isCorrectAns = ansId.equals(menu.getAnswer());

            if (isStudentAns && isCorrectAns) {
                imgCheck.setVisibility(View.VISIBLE);
                imgCheck.setImageResource(R.drawable.violet_tick);
                rlyCard.setBackgroundResource(R.drawable.bg_green_correct);

            } else if (isStudentAns && !isCorrectAns) {
                imgCheck.setVisibility(View.GONE);
                rlyCard.setBackgroundResource(R.drawable.bg_red_wrong);

            } else if (isCorrectAns) {
                imgCheck.setVisibility(View.VISIBLE);
                imgCheck.setImageResource(R.drawable.violet_tick);
                rlyCard.setBackgroundResource(R.drawable.bg_green_correct);
            } else {
                imgCheck.setVisibility(View.GONE);
                rlyCard.setBackgroundResource(R.drawable.bg_shadow_white_card);
            }
            holder.containerOptions.addView(optionView);
        }
    }

    private void openPreviewImage(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra("fileUrl", fileUrl);
        context.startActivity(intent);
    }

    public void loadFileInWebView(WebView webView, String url) {

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        String lower = url.toLowerCase();

        if (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".gif") || lower.endsWith(".webp")) {

            String html = "<html><body style='margin:0;padding:0;'>" + "<img src='" + url + "' width='100%' />" + "</body></html>";

            webView.loadData(html, "text/html", "UTF-8");
        } else if (lower.endsWith(".pdf")) {
            String viewerUrl = "https://docs.google.com/gview?embedded=true&url=" + url;
            webView.loadUrl(viewerUrl);
        } else if (lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".3gp") || lower.endsWith(".webm")) {

            String html = "<html><body style='margin:0;padding:0;'>" + "<video width='100%' height='100%' controls>" + "<source src='" + url + "' type='video/mp4'>" + "</video>" + "</body></html>";

            webView.loadData(html, "text/html", "UTF-8");
        } else if (lower.endsWith(".doc") || lower.endsWith(".docx") || lower.endsWith(".xls") || lower.endsWith(".xlsx") || lower.endsWith(".ppt") || lower.endsWith(".pptx") || lower.endsWith(".txt")) {

            String viewerUrl = "https://docs.google.com/gview?embedded=true&url=" + url;
            webView.loadUrl(viewerUrl);
        } else {
            webView.loadUrl(url);
        }
    }
}