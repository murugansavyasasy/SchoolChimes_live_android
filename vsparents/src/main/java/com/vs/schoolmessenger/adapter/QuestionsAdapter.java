package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.SubQustions;

import java.util.List;

/**
 * Created by voicesnap on 5/10/2018.
 */

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.MyViewHolder> {


    private List<SubQustions> questionslist;
    Context context;





    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblQuestion1;
        RadioButton radio1, radio2, radio3, radio4, radio5, radio6;

        public MyViewHolder(View view) {
            super(view);

            lblQuestion1 = (TextView) view.findViewById(R.id.lblQuestion1);
            radio1 = (RadioButton) view.findViewById(R.id.radio1);
            radio2 = (RadioButton) view.findViewById(R.id.radio2);
            radio3 = (RadioButton) view.findViewById(R.id.radio3);
            radio4 = (RadioButton) view.findViewById(R.id.radio4);
            radio5 = (RadioButton) view.findViewById(R.id.radio5);
            radio6 = (RadioButton) view.findViewById(R.id.radio6);


        }
    }

    public QuestionsAdapter(List<SubQustions> moviesList, Context context) {
        this.questionslist = moviesList;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_feedback, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final QuestionsAdapter.MyViewHolder holder, final int position) {
        Log.d("listsizeeee", String.valueOf(questionslist.size()));

        final SubQustions movie = questionslist.get(position);

        Log.d("pos", String.valueOf(movie));
        Log.d("position", String.valueOf(position));


        if (movie.equals(0)) {
            holder.lblQuestion1.setText(movie.getMainQuestion());
            holder.radio1.setText(movie.getQuestion());
            holder.radio2.setText(movie.getQuestion());
            holder.radio3.setText(movie.getQuestion());
            holder.radio4.setText(movie.getQuestion());
        }
        if (movie.equals(1)) {
            holder.lblQuestion1.setText(movie.getMainQuestion());
            holder.radio1.setText(movie.getQuestion());
            holder.radio2.setText(movie.getQuestion());
            holder.radio3.setText(movie.getQuestion());
            holder.radio4.setText(movie.getQuestion());
            holder.radio5.setText(movie.getQuestion());
        }


    }

    @Override
    public int getItemCount() {
        return questionslist.size();

    }
}


