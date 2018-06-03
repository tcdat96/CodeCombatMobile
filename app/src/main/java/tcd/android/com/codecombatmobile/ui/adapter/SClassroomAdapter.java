package tcd.android.com.codecombatmobile.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;

import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.course.SClassroom;
import tcd.android.com.codecombatmobile.ui.GameMapActivity;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

/**
 * Created by ADMIN on 30/04/2018.
 */

public class SClassroomAdapter extends RecyclerView.Adapter<SClassroomAdapter.SClassroomViewHolder> {

    private static int sPythonColor, sJavascriptColor;

    private Context mContext;
    private List<SClassroom> mClassrooms;

    public SClassroomAdapter(Context context, List<SClassroom> classrooms) {
        mContext = context;
        mClassrooms = classrooms;
        sPythonColor = ContextCompat.getColor(context, R.color.python_color);
        sJavascriptColor = ContextCompat.getColor(context, R.color.javascript_color);
    }

    @NonNull
    @Override
    public SClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_student_class_row, parent, false);
        final SClassroomViewHolder holder = new SClassroomViewHolder(itemView);
        setOnItemClickListener(itemView, holder);
        return holder;
    }

    private void setOnItemClickListener(View itemView, final SClassroomViewHolder holder) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SClassroom classroom = mClassrooms.get(holder.getAdapterPosition());
                if (classroom.getCourseName() != null) {
                    Intent intent = new Intent(mContext, GameMapActivity.class);
                    intent.putExtra(GameMapActivity.ARG_STUDENT_CLASSROOM, classroom);
                    mContext.startActivity(intent);
                }
            }
        });
    }


    @Override
    public void onBindViewHolder(@NonNull SClassroomViewHolder holder, int position) {
        final SClassroom classroom = mClassrooms.get(position);
        holder.mLanguageTextView.setText(DisplayUtil.capitalize(classroom.getLanguage()));
        holder.mClassNameTextView.setText(classroom.getClassName());
        holder.mProgressBar.setProgress(classroom.getProgress());

        // teacher name
        String teacher = !TextUtils.isEmpty(classroom.getTeacher()) ? classroom.getTeacher() : mContext.getString(R.string.not_available);
        holder.mTeacherTextView.setText(teacher);

        // course name
        String courseName = !TextUtils.isEmpty(classroom.getCourseName()) ? classroom.getCourseName() : mContext.getString(R.string.no_course_message);
        holder.mCourseNameTextView.setText(courseName);

        // language color
        boolean isPython = classroom.getLanguage().equals("python");
        int themeColor = isPython ? sPythonColor : sJavascriptColor;
        holder.mLanguageTextView.setBackgroundResource(
                isPython ? R.drawable.background_language_python : R.drawable.background_language_javascript);
        holder.mLanguageTextView.setTextColor(themeColor);
        holder.mProgressBar.setProgressStartColor(themeColor);
        holder.mProgressBar.setProgressEndColor(themeColor);
        holder.mProgressBar.setProgressTextColor(themeColor);
    }

    @Override
    public int getItemCount() {
        return mClassrooms.size();
    }

    class SClassroomViewHolder extends RecyclerView.ViewHolder {
        private TextView mLanguageTextView;
        private TextView mClassNameTextView;
        private TextView mTeacherTextView;
        private TextView mCourseNameTextView;
        private CircleProgressBar mProgressBar;

        SClassroomViewHolder(View itemView) {
            super(itemView);
            mLanguageTextView = itemView.findViewById(R.id.tv_programming_language);
            mClassNameTextView = itemView.findViewById(R.id.tv_class_name);
            mTeacherTextView = itemView.findViewById(R.id.tv_teacher);
            mCourseNameTextView = itemView.findViewById(R.id.tv_course_name);
            mProgressBar = itemView.findViewById(R.id.cpb_class_progress);
        }
    }
}
