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

import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.StudentClass;
import tcd.android.com.codecombatmobile.data.TeacherClass;
import tcd.android.com.codecombatmobile.ui.TeacherClassActivity;

/**
 * Created by ADMIN on 30/04/2018.
 */

public class TeacherClassAdapter extends RecyclerView.Adapter<TeacherClassAdapter.TeacherClassViewHolder> {

    private static int sPythonColor, sJavascriptColor;

    private Context mContext;
    private List<TeacherClass> mClasses;

    public TeacherClassAdapter(Context context, List<TeacherClass> classes) {
        mContext = context;
        mClasses = classes;
        sPythonColor = ContextCompat.getColor(context, R.color.python_color);
        sJavascriptColor = ContextCompat.getColor(context, R.color.javascript_color);
    }

    @NonNull
    @Override
    public TeacherClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_teacher_class_row, parent, false);
        return new TeacherClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherClassViewHolder holder, int position) {
        TeacherClass teacherClass = mClasses.get(position);
        holder.mLanguageTextView.setText(teacherClass.getLanguage());
        holder.mClassNameTextView.setText(teacherClass.getClassName());
        holder.mProgressBar.setProgress(teacherClass.getProgress());

        int studentTotal = teacherClass.getStudentTotal();
        String studentTotalStr = mContext.getResources().getQuantityString(R.plurals.student_total, studentTotal, studentTotal);
        holder.mStudentTotalTextView.setText(studentTotalStr);

        boolean isPython = teacherClass.getLanguage().equals("python");
        int themeColor = isPython ? sPythonColor : sJavascriptColor;
        holder.mLanguageTextView.setBackgroundResource(
                isPython ? R.drawable.background_language_python : R.drawable.background_language_javascript);
        holder.mLanguageTextView.setTextColor(themeColor);
        holder.mArchiveTextView.setTextColor(themeColor);
        holder.mProgressBar.setProgressStartColor(themeColor);
        holder.mProgressBar.setProgressEndColor(themeColor);
        holder.mProgressBar.setProgressTextColor(themeColor);
    }

    @Override
    public int getItemCount() {
        return mClasses.size();
    }

    class TeacherClassViewHolder extends RecyclerView.ViewHolder {
        private TextView mLanguageTextView;
        private TextView mClassNameTextView;
        private TextView mStudentTotalTextView;
        private TextView mArchiveTextView;
        private CircleProgressBar mProgressBar;

        TeacherClassViewHolder(View itemView) {
            super(itemView);
            mLanguageTextView = itemView.findViewById(R.id.tv_programming_language);
            mClassNameTextView = itemView.findViewById(R.id.tv_class_name);
            mStudentTotalTextView = itemView.findViewById(R.id.tv_student_total);
            mArchiveTextView = itemView.findViewById(R.id.tv_archive_class);
            mProgressBar = itemView.findViewById(R.id.cpb_class_progress);
        }
    }
}
