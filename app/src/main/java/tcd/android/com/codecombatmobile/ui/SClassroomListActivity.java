package tcd.android.com.codecombatmobile.ui;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.course.Course;
import tcd.android.com.codecombatmobile.data.course.SClassroom;
import tcd.android.com.codecombatmobile.data.game.Session;
import tcd.android.com.codecombatmobile.data.user.User;
import tcd.android.com.codecombatmobile.ui.adapter.SClassroomAdapter;
import tcd.android.com.codecombatmobile.util.CCDataUtil;
import tcd.android.com.codecombatmobile.util.CCRequestManager;
import tcd.android.com.codecombatmobile.util.DataUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class SClassroomListActivity extends ClassroomListActivity implements View.OnClickListener {

    private List<SClassroom> mClassrooms = new ArrayList<>();
    private SClassroomAdapter mAdapter;
    @Nullable
    private AsyncTask<Void, Void, Boolean> mAsyncTask = null;

    private RecyclerView mClassroomListRv;
    private FloatingActionButton mAddClassroomFab;
    private ProgressBar mLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sclassroom_list);
        configureActionBar();

        initUiComponents();
        requestSClassroomList();
    }

    private void initUiComponents() {
        mClassroomListRv = findViewById(R.id.rv_student_classes);
        mAddClassroomFab = findViewById(R.id.fab_add_classroom);
        mLoadingProgressBar = findViewById(R.id.pb_loading);

        mAddClassroomFab.setOnClickListener(this);

        initClassroomRecyclerView();
    }

    private void initClassroomRecyclerView() {
        mClassroomListRv.setLayoutManager(new LinearLayoutManager(this));
        mClassroomListRv.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new SClassroomAdapter(this, mClassrooms);
        mClassroomListRv.setAdapter(mAdapter);
    }

    private void requestSClassroomList() {
        final User user = DataUtil.getUserData(this);
        if (user != null) {
            new GetStudentClassListTask(user).execute();
        }
    }

    private void updateLoadingUi(boolean isLoading) {
        if (isLoading) {
            mClassroomListRv.setVisibility(View.INVISIBLE);
            mAddClassroomFab.setVisibility(View.INVISIBLE);
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        } else {
            mClassroomListRv.setVisibility(View.VISIBLE);
            mAddClassroomFab.setVisibility(View.VISIBLE);
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_classroom:
                showAddClassroomDialog();
        }
    }

    private void showAddClassroomDialog() {
        final EditText classCodeEditText = new EditText(this);
        classCodeEditText.setSingleLine(true);
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params.setMargins(48, 0, 48, 0);
        classCodeEditText.setLayoutParams(params);
        container.addView(classCodeEditText);

        AlertDialog.Builder addFriendDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        addFriendDialog
                .setTitle(getResources().getString(R.string.title_enter_classroom_code))
                .setView(container)
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mAsyncTask == null) {
                            mAsyncTask = new AddClassroomTask(classCodeEditText.getText().toString());
                            mAsyncTask.execute();
                        }
                    }
                })
                .show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }
    }

    private class GetStudentClassListTask extends AsyncTask<Void, Void, Boolean> {

        private final CCRequestManager mReqManager;
        private final User mUser;
        private List<Course> mCourses;

        GetStudentClassListTask(User user) {
            mUser = user;
            mReqManager = CCRequestManager.getInstance(SClassroomListActivity.this);
            updateLoadingUi(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // get all courses
                JSONArray courseArr = mReqManager.requestCoursesSync();
                if (courseArr == null) {
                    return false;
                }
                mCourses = CCDataUtil.parseCourses(courseArr);
                // get student's classrooms
                JSONArray classroomArr = mReqManager.requestStudentClassListSync(mUser.getId());
                if (classroomArr == null) {
                    return false;
                }
                List<SClassroom> classrooms = parseClasses(classroomArr);
                mClassrooms.clear();
                mClassrooms.addAll(classrooms);
                // get class instances
                JSONArray instanceArr = mReqManager.requestCourseInstancesSync(mUser.getId());
                if (instanceArr == null) {
                    return false;
                }
                parseInstances(instanceArr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        private List<SClassroom> parseClasses(JSONArray classroomsJsonArr) throws JSONException {
            int length = classroomsJsonArr.length();
            List<SClassroom> classrooms = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                JSONObject classObj = classroomsJsonArr.getJSONObject(i);
                String id = classObj.getString("_id");
                String name = classObj.getString("name");
                // language
                JSONObject aceConfig = classObj.getJSONObject("aceConfig");
                String language = aceConfig != null ? aceConfig.getString("language") : "python";
                // get owner's name
                String ownerId = classObj.getString("ownerID");
//                String teacher = getOwnerName(ownerId);
                String teacher = "";
                // course's levels
                JSONArray courseArr = classObj.getJSONArray("courses");
                parseCourseLevels(courseArr);

                SClassroom classroom = new SClassroom(id, language, name, teacher);
                classrooms.add(classroom);
            }
            return classrooms;
        }

        private String getOwnerName(String ownerId) throws JSONException {
            // TODO: 24/05/2018 not working
            CCRequestManager util = CCRequestManager.getInstance(SClassroomListActivity.this);
            JSONObject nameResponse = util.requestNamesSync(ownerId);
            String owner = "";
            if (nameResponse != null) {
                JSONObject obj = nameResponse.getJSONObject(ownerId);
                String firstName = obj.getString("firstName");
                String lastName = obj.getString("lastName");
                owner = firstName + lastName;
            }
            return owner;
        }

        private void parseCourseLevels(JSONArray courseArr) throws JSONException {
            for (int i = 0; i < courseArr.length(); i++) {
                JSONObject courseObj = courseArr.getJSONObject(i);
                String id = courseObj.getString("_id");
                Course course = getCourseById(id);
                if (course != null && course.getLevelTypes() == null) {
                    JSONArray levelArr = courseObj.getJSONArray("levels");
                    Map<String, Boolean> levels = new HashMap<>();
                    for (int j = 0; j < levelArr.length(); j++) {
                        JSONObject levelObj = levelArr.getJSONObject(j);
                        String original = levelObj.getString("original");
                        boolean isPrimaryLevel = CCDataUtil.isPrimaryLevel(levelObj);
                        levels.put(original, isPrimaryLevel);
                    }
                    course.setLevelTypes(levels);
                }
            }
        }

        @Nullable
        private Course getCourseById(String id) {
            for (int i = 0; i < mCourses.size(); i++) {
                if (mCourses.get(i).getId().equals(id)) {
                    return mCourses.get(i);
                }
            }
            return null;
        }

        private void parseInstances(JSONArray instanceArr) throws JSONException {
            for (int i = 0; i < instanceArr.length(); i++) {
                JSONObject instObj = instanceArr.getJSONObject(i);
                SClassroom classroom = getClassroomById(instObj.getString("classroomID"));
                if (classroom != null) {
                    classroom.setInstanceId(instObj.getString("_id"));
                    Course course = getCourseById(instObj.getString("courseID"));
                    if (course != null) {
                        classroom.setCourseName(course.getName());
                        classroom.setCampaignId(course.getCampaignId());
                        // progress
                        String instId = instObj.getString("_id");
                        JSONArray sessionArr = mReqManager.requestLevelSessionsSync(instId, mUser.getId());
                        if (sessionArr != null) {
                            // count primary levels
                            Map<String, Boolean> levels = course.getLevelTypes();
                            if (levels == null) {
                                continue;
                            }
                            int levelCount = 0;
                            for (Boolean isPrimary : levels.values()) {
                                levelCount += isPrimary ? 1 : 0;
                            }
                            // count completed primary levels
                            int completedLevelCount = 0;
                            List<Session> sessions = CCDataUtil.parseLevelSessions(sessionArr);
                            for (Session session : sessions) {
                                boolean isPrimary = levels.get(session.getOriginal());
                                boolean isCompleted = session.isCompleted();
                                if (isPrimary && isCompleted) {
                                    completedLevelCount++;
                                }
                            }

                            classroom.setProgress(completedLevelCount * 100 / levelCount);
                        }
                    }
                }
            }
        }

        @Nullable
        private SClassroom getClassroomById(String id) {
            for (int i = 0; i < mClassrooms.size(); i++) {
                if (mClassrooms.get(i).getId().equals(id)) {
                    return mClassrooms.get(i);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mAsyncTask = null;
            if (success) {
                mAdapter.notifyDataSetChanged();
                updateLoadingUi(false);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mAsyncTask = null;
        }
    }

    private class AddClassroomTask extends AsyncTask<Void, Void, Boolean> {

        private String mClassCode;
        AddClassroomTask(String classCode) {
            this.mClassCode = classCode;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            CCRequestManager.getInstance(SClassroomListActivity.this).addSClassroom(mClassCode);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            requestSClassroomList();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mAsyncTask = null;
        }
    }
}
