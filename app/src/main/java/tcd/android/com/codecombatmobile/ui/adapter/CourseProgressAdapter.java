package tcd.android.com.codecombatmobile.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.course.MemberProgress;

/**
 * Created by ADMIN on 30/04/2018.
 */

public class CourseProgressAdapter extends RecyclerView.Adapter<CourseProgressAdapter.StudentViewHolder> {

    @NonNull
    private MemberProgress[] mMembers;
    private int mLevelTotal;

    public CourseProgressAdapter(@NonNull MemberProgress[] members, int levelTotal) {
        mMembers = members;
        mLevelTotal = levelTotal;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_member_progress, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        MemberProgress member = mMembers[position];
        holder.mNameTextView.setText(member.getName());
        holder.mEmailTextView.setText(member.getEmail());

        String completedLevels = String.format(Locale.getDefault(), "%d/%d", member.getCompletedLevels(), mLevelTotal);
        holder.mCompletedLevelsTextView.setText(completedLevels);
    }

    @Override
    public int getItemCount() {
        return mMembers.length;
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTextView;
        private TextView mEmailTextView;
        private TextView mCompletedLevelsTextView;

        StudentViewHolder(View itemView) {
            super(itemView);
            mNameTextView = itemView.findViewById(R.id.tv_student_name);
            mEmailTextView = itemView.findViewById(R.id.tv_student_email);
            mCompletedLevelsTextView = itemView.findViewById(R.id.tv_completed_level_total);
        }
    }
}
