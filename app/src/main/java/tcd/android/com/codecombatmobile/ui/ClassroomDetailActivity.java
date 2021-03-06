package tcd.android.com.codecombatmobile.ui;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.course.MemberProgress;
import tcd.android.com.codecombatmobile.data.course.TClassroom;
import tcd.android.com.codecombatmobile.data.course.TClassroomDetail;
import tcd.android.com.codecombatmobile.data.game.Playtime;
import tcd.android.com.codecombatmobile.data.game.Session;
import tcd.android.com.codecombatmobile.ui.adapter.CourseProgressAdapter;
import tcd.android.com.codecombatmobile.ui.widget.DetailCardView;
import tcd.android.com.codecombatmobile.util.CCDataUtil;
import tcd.android.com.codecombatmobile.util.CCRequestManager;
import tcd.android.com.codecombatmobile.util.DataUtil;
import tcd.android.com.codecombatmobile.util.TimeUtil;

public class ClassroomDetailActivity extends AppCompatActivity {

    private static final String TAG = ClassroomDetailActivity.class.getSimpleName();
    public static final String ARG_TEACHER_CLASS_DETAIL = "argTeacherClassDetail";

    private TClassroom mClassroom;
    private TClassroomDetail mDetail;
    @Nullable
    private GetClassroomDetailTask mAsyncTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);

        if (getIntent() == null || !getIntent().hasExtra(ARG_TEACHER_CLASS_DETAIL)) {
            Log.e(TAG, "onCreate: There was a problem retrieving teacher class");
            finish();
            return;
        }

        mClassroom = (TClassroom) getIntent().getSerializableExtra(ARG_TEACHER_CLASS_DETAIL);
        initUiComponents();

        mAsyncTask = new GetClassroomDetailTask();
        mAsyncTask.execute();
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
        int studentTotal = mClassroom.getMemberTotal();
        String studentTotalStr = getResources().getQuantityString(R.plurals.student_total, studentTotal, studentTotal);
        studentTotalTextView.setText(studentTotalStr);

        // date created
        TextView dateCreatedTextView = findViewById(R.id.tv_date_created);
        String dateCreated = DateUtils.formatDateTime(this, mClassroom.getDateCreated(), DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_YEAR);
        dateCreatedTextView.setText(dateCreated);

        // class code
        TextView classCodeTextView = findViewById(R.id.tv_class_code);
        classCodeTextView.setText(mClassroom.getClassCode());

        expandReservedSpace(classCodeTextView);
    }

    private void initMemberList() {
        MemberProgress[] members = mDetail.getMembers();
        if (members != null && members.length > 0) {
            RecyclerView studentsListRecyclerView = findViewById(R.id.rv_student_list);
            studentsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            studentsListRecyclerView.setItemAnimator(new DefaultItemAnimator());
            CourseProgressAdapter adapter = new CourseProgressAdapter(members, mDetail.getLevelTotal());
            studentsListRecyclerView.setAdapter(adapter);
        } else {
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

    private void initDetailCards() {
        int[] iconResIds = new int[]{
                R.drawable.ic_person_white_24dp, R.drawable.ic_level_white_24dp, R.drawable.ic_timer_white_24dp,
                R.drawable.ic_complete_levels_white_24dp, R.drawable.ic_avg_level_white_24dp, R.drawable.ic_av_timer_white_24dp
        };
        int[] titleResIds = new int[]{
                R.string.total_student, R.string.total_level, R.string.total_playtime,
                R.string.completed, R.string.average_level, R.string.average_play_time
        };
        Playtime playtime = TimeUtil.getDisplayPlaytime(this, mDetail.getPlaytime());
        Playtime avgPlaytime = TimeUtil.getDisplayPlaytime(this,
                mDetail.getPlaytime() / (mDetail.getMemberTotal() > 0 ? mDetail.getMemberTotal() : 1));
        String[] values = new String[]{
                "" + mDetail.getMemberTotal(),
                "" + mDetail.getLevelTotal(),
                playtime.getCompactTime(),
                "" + mDetail.getCompletedLevelTotal(),
                "" + mDetail.getAvgCompletedLevelTotal(),
                avgPlaytime.getCompactTime()
        };

        GridLayout container = findViewById(R.id.gl_card_container);
        for (int i = 0; i < iconResIds.length; i++) {
            DetailCardView cardView = new DetailCardView(this);
            cardView.setIcon(iconResIds[i]);
            cardView.setTitle(getString(titleResIds[i]));
            cardView.setValue(values[i]);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(i / 3, 1f);
            params.columnSpec = GridLayout.spec(i % 3, 1f);
            container.addView(cardView, params);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
    }

    private class GetClassroomDetailTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                CCRequestManager manager = CCRequestManager.getInstance(ClassroomDetailActivity.this);
                JSONArray sessionArr = manager.requestMemberSessionsSync(mClassroom.getId());
                if (sessionArr == null) {
                    return false;
                }
                List<Session> sessions = CCDataUtil.parseMemberSessions(sessionArr);

                // members
                JSONArray memberArr = manager.requestMembersSync(mClassroom.getId());
                if (memberArr == null) {
                    return false;
                }
                MemberProgress[] members = CCDataUtil.parseTClassroomMembers(memberArr);
                CCDataUtil.updateMembersWithSessions(members, sessions);

                // playtime total
                int playtime = CCDataUtil.sumPlaytimeTotal(sessions);
                // avg completed level total
                int avgLevel = sessions.size() / (members.length > 0 ? members.length : 1);
                // level total
                JSONArray levelArr = manager.requestClassroomLevels(mClassroom.getId());
                if (levelArr == null) {
                    return false;
                }
                // completed level total
                int completedLevelTotal = CCDataUtil.countCompletedLevels(sessions);

                mDetail = new TClassroomDetail(members, playtime, levelArr.length(), completedLevelTotal, avgLevel);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                initDetailCards();
                initMemberList();
            } else {
                Toast.makeText(ClassroomDetailActivity.this, R.string.error_get_data_message, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mAsyncTask = null;
        }
    }
}
