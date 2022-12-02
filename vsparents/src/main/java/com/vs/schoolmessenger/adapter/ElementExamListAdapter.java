package com.vs.schoolmessenger.adapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Element;
import com.vs.schoolmessenger.model.ExamList;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;
import org.json.JSONObject;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
public class ElementExamListAdapter extends RecyclerView.Adapter<ElementExamListAdapter.MyViewHolder> {
    private List<Element> data;
    Context context;
    public ElementExamListAdapter(Context context, List<Element> data) {
        this.context=context;
        this.data=data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblname,lblmark;

        public MyViewHolder(View view) {
            super(view);
            lblname = (TextView) view.findViewById(R.id.lblname);
            lblmark = (TextView) view.findViewById(R.id.lblmark);
        }
    }
    @Override
    public ElementExamListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elements_exam_marks, parent, false);
        return new ElementExamListAdapter.MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final ElementExamListAdapter.MyViewHolder holder, final
    int position) {
        final Element elements = data.get(position);
        holder.lblname.setText(elements.getName());
        holder.lblmark.setText(elements.getMark());
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

}