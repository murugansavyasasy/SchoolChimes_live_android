//package com.vs.schoolmessenger.adapter;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.vs.schoolmessenger.R;
//
//import java.util.List;
//
//public class SplitAdapter extends RecyclerView.Adapter<SplitAdapter.MyViewHolder> {
//
//    private List<String> lib_list;
//    Context context;
//
//    public void clearAllData() {
//        int size = this.lib_list.size();
//        if (size > 0) {
//            for (int i = 0; i < size; i++) {
//                this.lib_list.remove(0);
//            }
//            this.notifyItemRangeRemoved(0, size);
//        }
//    }
//
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//        public TextView Exam_Name;
//        public RelativeLayout rytExams;
//
//        public MyViewHolder(View view) {
//            super(view);
//
//            Exam_Name = (TextView) view.findViewById(R.id.Exam_Name);
//            rytExams = (RelativeLayout) view.findViewById(R.id.rytExams);
//
//        }
//    }
//    public SplitAdapter(List<String> lib_list, Context context) {
//        this.lib_list = lib_list;
//        this.context = context;
//    }
//
//    @Override
//    public SplitAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.exams, parent, false);
//        return new SplitAdapter.MyViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(final SplitAdapter.MyViewHolder holder, final int position) {
//
//        Log.d("listsizeeee", String.valueOf(lib_list.size()));
//        final String exam = lib_list.get(position);
//
//        holder.Exam_Name.setText(exam);
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return lib_list.size();
//
//    }
//}
//
