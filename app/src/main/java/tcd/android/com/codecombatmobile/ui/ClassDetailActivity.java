package tcd.android.com.codecombatmobile.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.TeacherClass;
import tcd.android.com.codecombatmobile.ui.adapter.ClassStudentAdapter;
import tcd.android.com.codecombatmobile.ui.widget.DetailCardView;
import tcd.android.com.codecombatmobile.util.DataUtil;

public class ClassDetailActivity extends AppCompatActivity {

    private static final String TAG = ClassDetailActivity.class.getSimpleName();
    public static final String ARG_TEACHER_CLASS_DETAIL = "argTeacherClassDetail";

    private TeacherClass mClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);

        if (getIntent() == null || !getIntent().hasExtra(ARG_TEACHER_CLASS_DETAIL)) {
            Log.e(TAG, "onCreate: There was a problem retrieving teacher class");
            mClass = DataUtil.getDebugTeacherClassList(1).get(0);
        } else

        mClass = (TeacherClass) getIntent().getSerializableExtra(ARG_TEACHER_CLASS_DETAIL);
        initUiComponents();
    }

    private void initUiComponents() {
        // cover image
        ImageView languageCoverImageView = findViewById(R.id.iv_language_cover);
        int langType = DataUtil.getLanguageType(mClass.getLanguage());
        int coverResId = DataUtil.getLanguageCoverRes(langType);
        Glide.with(this).load(coverResId).into(languageCoverImageView);

        // language tag
        boolean isPython = langType == DataUtil.LANGUAGE_PYTHON;
        int themeColor = ContextCompat.getColor(this, isPython ? R.color.python_color : R.color.javascript_color);
        TextView languageTextView = findViewById(R.id.tv_programming_language);
        languageTextView.setText(mClass.getLanguage());
        languageTextView.setBackgroundResource(
                isPython ? R.drawable.background_language_python : R.drawable.background_language_javascript);
        languageTextView.setTextColor(themeColor);

        // class name
        TextView classNameTextView = findViewById(R.id.tv_class_name);
        classNameTextView.setText(mClass.getClassName());

        // total number of students
        TextView studentTotalTextView = findViewById(R.id.tv_student_total);
        int studentTotal = mClass.getStudentTotal();
        String studentTotalStr = getResources().getQuantityString(R.plurals.student_total, studentTotal, studentTotal);
        studentTotalTextView.setText(studentTotalStr);

        // date created
        TextView dateCreatedTextView = findViewById(R.id.tv_date_created);
        dateCreatedTextView.setText(mClass.getDateCreated());

        initCardRow();

        // student list
        RecyclerView studentsListRecyclerView = findViewById(R.id.rv_student_list);
        studentsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentsListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ClassStudentAdapter adapter = new ClassStudentAdapter(mClass.getStudents());
        studentsListRecyclerView.setAdapter(adapter);
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
                mClass.getLevelTotal(), mClass.getPlaytimeTotal(), mClass.getProgress(),
                mClass.getLevelTotal(), mClass.getPlaytimeTotal(), mClass.getProgress()
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
