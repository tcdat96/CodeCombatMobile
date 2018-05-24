package tcd.android.com.codecombatmobile.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dinuscxj.progressbar.CircleProgressBar;

import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.StudentClass;
import tcd.android.com.codecombatmobile.data.TeacherClass;
import tcd.android.com.codecombatmobile.ui.ClassDetailActivity;
import tcd.android.com.codecombatmobile.ui.TeacherClassActivity;
import tcd.android.com.codecombatmobile.util.DataUtil;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

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
    public void onBindViewHolder(@NonNull final TeacherClassViewHolder holder, int position) {
        final TeacherClass teacherClass = mClasses.get(position);
        holder.mProgressBar.setProgress(teacherClass.getProgress());
        holder.mClassNameTextView.setText(teacherClass.getClassName());
        holder.mLanguageTextView.setText(DisplayUtil.capitalize(teacherClass.getLanguage()));

        // total number of students
        int studentTotal = teacherClass.getStudentTotal();
        String studentTotalStr = mContext.getResources().getQuantityString(R.plurals.student_total, studentTotal, studentTotal);
        holder.mStudentTotalTextView.setText(studentTotalStr);

        // progress color theme
        boolean isPython = teacherClass.getLanguage().equals("python");
        int themeColor = isPython ? sPythonColor : sJavascriptColor;
        holder.mProgressBar.setProgressStartColor(themeColor);
        holder.mProgressBar.setProgressEndColor(themeColor);
        holder.mProgressBar.setProgressTextColor(themeColor);

        // cover background
        final int coverResId = DataUtil.getLanguageCoverRes(teacherClass);
        Glide.with(mContext).asBitmap().load(coverResId).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Bitmap darkenBitmap = DisplayUtil.darkenBitmap(resource);
                holder.mCoverImageView.setImageBitmap(darkenBitmap);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ClassDetailActivity.class);
                intent.putExtra(ClassDetailActivity.ARG_TEACHER_CLASS_DETAIL, teacherClass);
                intent.putExtra(ClassDetailActivity.ARG_COVER_RESOURCE_ID, coverResId);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (Activity)mContext,
                            holder.mContainer,
                            ViewCompat.getTransitionName(holder.mContainer));
                    mContext.startActivity(intent, options.toBundle());
                } else {
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mClasses.size();
    }

    class TeacherClassViewHolder extends RecyclerView.ViewHolder {
        private CardView mContainer;
        private ImageView mCoverImageView;
        private TextView mLanguageTextView;
        private TextView mClassNameTextView;
        private TextView mStudentTotalTextView;
        private CircleProgressBar mProgressBar;

        TeacherClassViewHolder(View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.cv_container);
            mCoverImageView = itemView.findViewById(R.id.iv_language_cover);
            mLanguageTextView = itemView.findViewById(R.id.tv_programming_language);
            mClassNameTextView = itemView.findViewById(R.id.tv_class_name);
            mStudentTotalTextView = itemView.findViewById(R.id.tv_student_total);
            mProgressBar = itemView.findViewById(R.id.cpb_class_progress);
        }
    }
}
