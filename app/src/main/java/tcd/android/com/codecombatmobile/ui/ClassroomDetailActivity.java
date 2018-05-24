package tcd.android.com.codecombatmobile.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.course.TClassroom;
import tcd.android.com.codecombatmobile.ui.adapter.CourseProgressAdapter;
import tcd.android.com.codecombatmobile.ui.widget.DetailCardView;
import tcd.android.com.codecombatmobile.util.DataUtil;

public class ClassroomDetailActivity extends AppCompatActivity {

    private static final String TAG = ClassroomDetailActivity.class.getSimpleName();
    public static final String ARG_TEACHER_CLASS_DETAIL = "argTeacherClassDetail";
    public static final String ARG_COVER_RESOURCE_ID = "argCoverResId";

    private TClassroom mClassroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);

        if (getIntent() == null || !getIntent().hasExtra(ARG_TEACHER_CLASS_DETAIL)) {
            Log.e(TAG, "onCreate: There was a problem retrieving teacher class");
//            finish();
//            return;
//        }
            // TODO: 24/05/2018 remove this line and else statement
            mClassroom = DataUtil.getDebugTClassroomList(1).get(0);
        } else

        mClassroom = (TClassroom) getIntent().getSerializableExtra(ARG_TEACHER_CLASS_DETAIL);
        initUiComponents();
    }

    private void initUiComponents() {
        // cover image
        ImageView languageCoverImageView = findViewById(R.id.iv_language_cover);
        int coverResId = DataUtil.getLanguageCoverRes(mClassroom);
        Glide.with(this).load(coverResId).into(languageCoverImageView);

        // language tag
        boolean isPython = mClassroom.getLanguage().toLowerCase().equals("python");
        int langColor = ContextCompat.getColor(this, isPython ? R.color.python_color : R.color.javascript_color);
        TextView languageTextView = findViewById(R.id.tv_programming_language);
        languageTextView.setText(mClassroom.getLanguage());
        languageTextView.setBackgroundResource(
                isPython ? R.drawable.background_language_python : R.drawable.background_language_javascript);
        languageTextView.setTextColor(langColor);

        // class name
        TextView classNameTextView = findViewById(R.id.tv_class_name);
        classNameTextView.setText(mClassroom.getClassName());

        // total number of students
        TextView studentTotalTextView = findViewById(R.id.tv_student_total);
        int studentTotal = mClassroom.getStudentTotal();
        String studentTotalStr = getResources().getQuantityString(R.plurals.student_total, studentTotal, studentTotal);
        studentTotalTextView.setText(studentTotalStr);

        // date created
        TextView dateCreatedTextView = findViewById(R.id.tv_date_created);
        dateCreatedTextView.setText(mClassroom.getDateCreated());

        // class code
        TextView classCodeTextView = findViewById(R.id.tv_class_code);
        classCodeTextView.setText(mClassroom.getClassCode());

        expandReservedSpace(classCodeTextView);

        initCardRow();

        // student list
        RecyclerView studentsListRecyclerView = findViewById(R.id.rv_student_list);
        studentsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentsListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        CourseProgressAdapter adapter = new CourseProgressAdapter(mClassroom.getStudents());
        studentsListRecyclerView.setAdapter(adapter);

        // if it has no student yet
        if (mClassroom.getStudents().size() == 0) {
            findViewById(R.id.tv_no_student_message).setVisibility(View.VISIBLE);
        }
    }

    private void expandReservedSpace(final View lastView) {
        final View rootView = findViewById(R.id.cl_root);
        final Space reservedSpace = findViewById(R.id.space_reserved);
        lastView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    lastView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    lastView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                float lastViewBottom = lastView.getBottom();
                float rootBottom = rootView.getBottom();
                int height = (int) (rootBottom - lastViewBottom);
                if (height > 0) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) reservedSpace.getLayoutParams();
                    params.height = height;
                    reservedSpace.setLayoutParams(params);
                }
            }
        });
    }

    private void initCardRow() {
        int[] iconResIds = new int[] {
                R.drawable.ic_person_white_24dp, R.drawable.ic_level_white_24dp, R.drawable.ic_timer_white_24dp,
                R.drawable.ic_latest_level_white_24dp, R.drawable.ic_avg_level_white_24dp, R.drawable.ic_av_timer_white_24dp
        };
        int[] titleResIds = new int[] {
                R.string.total_student, R.string.total_level, R.string.total_play_time,
                R.string.latest_level, R.string.average_level, R.string.average_play_time
        };
        int[] values = new int[] {
                mClassroom.getLevelTotal(), mClassroom.getPlaytimeTotal(), mClassroom.getProgress(),
                mClassroom.getLevelTotal(), mClassroom.getPlaytimeTotal(), mClassroom.getProgress()
        };

        GridLayout container = findViewById(R.id.gl_card_container);
        for (int i = 0; i < iconResIds.length; i++) {
            DetailCardView cardView = new DetailCardView(this);
            cardView.setIcon(iconResIds[i]);
            cardView.setTitle(getString(titleResIds[i]));
            cardView.setValue(String.valueOf(values[i]));

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(i / 3, 1f);
            params.columnSpec = GridLayout.spec(i % 3, 1f);
            container.addView(cardView, params);
        }
    }
}
