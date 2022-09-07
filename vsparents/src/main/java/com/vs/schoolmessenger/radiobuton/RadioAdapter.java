//package com.vs.schoolmessenger.radiobuton;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.vs.schoolmessenger.R;
//
//import java.util.List;
//
//public class RadioAdapter extends RecyclerView.Adapter<RadioAdapter.MyViewHolder> {
//
//    private List<RadioAdapter> lib_list;
//    Context context;
//    private RadioButton lastCheckedRB = null;
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
//      RadioButton radio;
//
//        public MyViewHolder(View view) {
//            super(view);
//
//            radio = (RadioButton) view.findViewById(R.id.radio);
//
//
//        }
//    }
//    public RadioAdapter(List<RadioAdapter> lib_list, Context context) {
//        this.lib_list = lib_list;
//        this.context = context;
//    }
//
//    @Override
//    public RadioAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.radio_acti, parent, false);
//        return new RadioAdapter.MyViewHolder(itemView);
//    }
//
//
//
////    @Override
////    public void onBindViewHolder(final RadioAdapter.MyViewHolder holder, final int position) {
////
////        Log.d("listsizeeee", String.valueOf(lib_list.size()));
////        final RadioAdapter exam = lib_list.get(position);
////
////        holder.Exam_Name.setText(exam.getName());
////        holder.rytExams.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Intent marklist=new Intent(context, MarksListScreen.class);
////                marklist.putExtra("exam_id", exam.getId());
////                context.startActivity(marklist);
////
////            }
////        });
//
//
//    @Override
//    public void onBindViewHolder(final RadioAdapter.MyViewHolder holder, final int position) {
//        holder.radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton checked_rb = (RadioButton) group.findViewById(R.id.radio);
//                if (lastCheckedRB != null) {
//                    lastCheckedRB.setChecked(false);
//                }
//                //store the clicked radiobutton
//                lastCheckedRB = checked_rb;
//            }
//        });
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
//
