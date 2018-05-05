package tcd.android.com.codecombatmobile.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;

import java.util.ArrayList;
import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.StudentClass;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

/**
 * Created by ADMIN on 30/04/2018.
 */

public class StudentClassAdapter extends RecyclerView.Adapter<StudentClassAdapter.StudentClassViewHolder> {

    private static int sPythonColor, sJavascriptColor;

    private List<StudentClass> mClasses;

    public StudentClassAdapter(Context context, List<StudentClass> classes) {
        mClasses = classes;
        sPythonColor = ContextCompat.getColor(context, R.color.python_color);
        sJavascriptColor = ContextCompat.getColor(context, R.color.javascript_color);
    }

    @NonNull
    @Override
    public StudentClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_student_class_row, parent, false);
        return new StudentClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentClassViewHolder holder, int position) {
        StudentClass stuClass = mClasses.get(position);
        holder.mLanguageTextView.setText(DisplayUtil.capitalize(stuClass.getLanguage()));
        holder.mClassNameTextView.setText(stuClass.getClassName());
        holder.mTeacherTextView.setText(stuClass.getTeacher());
        holder.mCourseNameTextView.setText(stuClass.getCourseName());
        holder.mProgressBar.setProgress(stuClass.getProgress());

        boolean isPython = stuClass.getLanguage().equals("python");
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
        return mClasses.size();
    }

    class StudentClassViewHolder extends RecyclerView.ViewHolder {
        private TextView mLanguageTextView;
        private TextView mClassNameTextView;
        private TextView mTeacherTextView;
        private TextView mCourseNameTextView;
        private CircleProgressBar mProgressBar;

        StudentClassViewHolder(View itemView) {
            super(itemView);
            mLanguageTextView = itemView.findViewById(R.id.tv_programming_language);
            mClassNameTextView = itemView.findViewById(R.id.tv_class_name);
            mTeacherTextView = itemView.findViewById(R.id.tv_teacher);
            mCourseNameTextView = itemView.findViewById(R.id.tv_course_name);
            mProgressBar = itemView.findViewById(R.id.cpb_class_progress);
        }
    }
}
