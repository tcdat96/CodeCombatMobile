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
import tcd.android.com.codecombatmobile.data.course.TClassroom;
import tcd.android.com.codecombatmobile.ui.ClassroomDetailActivity;
import tcd.android.com.codecombatmobile.util.DataUtil;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

/**
 * Created by ADMIN on 30/04/2018.
 */

public class TClassroomAdapter extends RecyclerView.Adapter<TClassroomAdapter.TClassroomViewHolder> {

    private static int sPythonColor, sJavascriptColor;

    private Context mContext;
    private List<TClassroom> mClassrooms;

    public TClassroomAdapter(Context context, List<TClassroom> classes) {
        mContext = context;
        mClassrooms = classes;
        sPythonColor = ContextCompat.getColor(context, R.color.python_color);
        sJavascriptColor = ContextCompat.getColor(context, R.color.javascript_color);
    }

    @NonNull
    @Override
    public TClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_tclassroom_row, parent, false);
        TClassroomViewHolder holder = new TClassroomViewHolder(itemView);
        setOnItemClickListener(itemView, holder);
        return holder;
    }

    private void setOnItemClickListener(View itemView, final TClassroomViewHolder holder) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TClassroom classroom = mClassrooms.get(holder.getAdapterPosition());
                Intent intent = new Intent(mContext, ClassroomDetailActivity.class);
                intent.putExtra(ClassroomDetailActivity.ARG_TEACHER_CLASS_DETAIL, classroom);
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
    public void onBindViewHolder(@NonNull final TClassroomViewHolder holder, int position) {
        final TClassroom classroom = mClassrooms.get(position);
        holder.mProgressBar.setProgress(classroom.getProgress());
        holder.mClassNameTextView.setText(classroom.getClassName());
        holder.mLanguageTextView.setText(DisplayUtil.capitalize(classroom.getLanguage()));

        // total number of students
        int studentTotal = classroom.getStudentTotal();
        String studentTotalStr = mContext.getResources().getQuantityString(R.plurals.student_total, studentTotal, studentTotal);
        holder.mStudentTotalTextView.setText(studentTotalStr);

        // progress color theme
        boolean isPython = classroom.getLanguage().equals("python");
        int themeColor = isPython ? sPythonColor : sJavascriptColor;
        holder.mProgressBar.setProgressStartColor(themeColor);
        holder.mProgressBar.setProgressEndColor(themeColor);
        holder.mProgressBar.setProgressTextColor(themeColor);

        // cover background
        final int coverResId = DataUtil.getLanguageCoverRes(classroom);
        Glide.with(mContext).asBitmap().load(coverResId).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Bitmap darkenBitmap = DisplayUtil.darkenBitmap(resource);
                holder.mCoverImageView.setImageBitmap(darkenBitmap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mClassrooms.size();
    }

    class TClassroomViewHolder extends RecyclerView.ViewHolder {
        private CardView mContainer;
        private ImageView mCoverImageView;
        private TextView mLanguageTextView;
        private TextView mClassNameTextView;
        private TextView mStudentTotalTextView;
        private CircleProgressBar mProgressBar;

        TClassroomViewHolder(View itemView) {
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
