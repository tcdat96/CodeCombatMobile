package tcd.android.com.codecombatmobile.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;

import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.course.CourseProgress;

/**
 * Created by ADMIN on 30/04/2018.
 */

public class CourseProgressAdapter extends RecyclerView.Adapter<CourseProgressAdapter.StudentViewHolder> {

    private List<CourseProgress> mStudents;

    public CourseProgressAdapter(List<CourseProgress> students) {
        mStudents = students;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_progress_row, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        CourseProgress student = mStudents.get(position);
        holder.mNameTextView.setText(student.getName());
        holder.mEmailTextView.setText(student.getEmail());
        holder.mProgressBar.setProgress(student.getProgress());
        holder.mLastLevelTextView.setText(student.getLatestLevel());
    }

    @Override
    public int getItemCount() {
        return mStudents.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTextView;
        private TextView mEmailTextView;
        private TextView mLastLevelTextView;
        private CircleProgressBar mProgressBar;

        StudentViewHolder(View itemView) {
            super(itemView);
            mNameTextView = itemView.findViewById(R.id.tv_student_name);
            mEmailTextView = itemView.findViewById(R.id.tv_student_email);
            mLastLevelTextView = itemView.findViewById(R.id.tv_last_level);
            mProgressBar = itemView.findViewById(R.id.cpb_class_progress);
        }
    }
}
