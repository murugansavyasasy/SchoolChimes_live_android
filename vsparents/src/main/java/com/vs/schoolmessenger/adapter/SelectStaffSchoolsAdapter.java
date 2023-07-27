package com.vs.schoolmessenger.adapter;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.Teacher_AA_Test;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import java.util.List;

public class SelectStaffSchoolsAdapter extends RecyclerView.Adapter<SelectStaffSchoolsAdapter.MyViewHolder> {

    private List<TeacherSchoolsModel> lib_list;
    Context context;

    public void clearAllData() {
        int size = this.lib_list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.lib_list.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView aHome_tvSchoolName;
        public TextView aHome_tvSchoolAddress;
        public TextView aHome_tvStaffName;
        public ImageView aHome_nivSchoolLogo;
        public LinearLayout aHome_llSchoollayout;

        public MyViewHolder(View view) {
            super(view);

            aHome_tvSchoolName = (TextView) view.findViewById(R.id.aHome_tvSchoolName);
            aHome_tvSchoolAddress = (TextView) view.findViewById(R.id.aHome_tvSchoolAddress);
            aHome_tvStaffName = (TextView) view.findViewById(R.id.aHome_tvStaffName);
            aHome_nivSchoolLogo = (ImageView) view.findViewById(R.id.aHome_nivSchoolLogo);
            aHome_llSchoollayout = (LinearLayout) view.findViewById(R.id.aHome_llSchoollayout);

        }
    }

    public SelectStaffSchoolsAdapter(List<TeacherSchoolsModel> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public SelectStaffSchoolsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.staff_schools_item, parent, false);
        return new SelectStaffSchoolsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SelectStaffSchoolsAdapter.MyViewHolder holder, final int position) {

        final TeacherSchoolsModel schoolmodel = lib_list.get(position);
        holder.aHome_tvSchoolName.setText(schoolmodel.getStrSchoolName());
        holder.aHome_tvSchoolAddress.setText(schoolmodel.getStrSchoolAddress());
        holder.aHome_tvStaffName.setText(schoolmodel.getStrStaffName());

        holder.aHome_tvStaffName.setTypeface(holder.aHome_tvStaffName.getTypeface(), Typeface.BOLD);


        String url = schoolmodel.getStrSchoolLogoUrl();
        if (!url.equals("")) {
            Glide.with(context).load(url).centerCrop().into(holder.aHome_nivSchoolLogo);
        }

        holder.aHome_llSchoollayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TeacherUtil_Common.Principal_staffId = schoolmodel.getStrStaffID();
                TeacherUtil_Common.Principal_SchoolId = schoolmodel.getStrSchoolID();

                Intent i = new Intent(context, Teacher_AA_Test.class);
                i.putExtra("SCHOOL_ID & Staff_ID", schoolmodel.getStrSchoolID() + " " + schoolmodel.getStrStaffID());
                i.putExtra("schoolname", schoolmodel.getStrSchoolName());
                i.putExtra("Staff_ID1", schoolmodel.getStrStaffID());
                i.putExtra("STAFF_ID", schoolmodel.getStrStaffID());
                i.putExtra("SCHOOL_ID", schoolmodel.getStrSchoolID());
                i.putExtra("schooladdress", schoolmodel.getStrSchoolAddress());
                i.putExtra("TeacherSchoolsModel", schoolmodel);
                i.putExtra("list", listschooldetails);
                Log.d("Schoolid", TeacherUtil_Common.Principal_SchoolId);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}