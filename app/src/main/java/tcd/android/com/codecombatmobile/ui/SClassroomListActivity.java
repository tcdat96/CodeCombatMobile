package tcd.android.com.codecombatmobile.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
import tcd.android.com.codecombatmobile.data.user.User;
import tcd.android.com.codecombatmobile.ui.adapter.SClassroomAdapter;
import tcd.android.com.codecombatmobile.util.CCDataUtil;
import tcd.android.com.codecombatmobile.util.CCRequestManager;
import tcd.android.com.codecombatmobile.util.DataUtil;

public class SClassroomListActivity extends ClassroomListActivity {

    private List<SClassroom> mClassrooms = new ArrayList<>();
    private SClassroomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sclassroom_list);
        configureActionBar();

        initClassList();
        requestClassList();
    }

    private void initClassList() {
        RecyclerView classroomListRv = findViewById(R.id.rv_student_classes);
        classroomListRv.setLayoutManager(new LinearLayoutManager(this));
        classroomListRv.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new SClassroomAdapter(this, mClassrooms);
        classroomListRv.setAdapter(mAdapter);
    }

    private void requestClassList() {
        final User user = DataUtil.getUserData(this);
        if (user != null) {
            new GetStudentClassListTask(user).execute();
        }
    }

    private class GetStudentClassListTask extends AsyncTask<Void, Void, Boolean> {

        private final CCRequestManager mReqManager;
        private final User mUser;
        private List<Course> mCourses;

        GetStudentClassListTask(User user) {
            mUser = user;
            mReqManager = CCRequestManager.getInstance(SClassroomListActivity.this);
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
                            Map<String, Boolean> sessions = CCDataUtil.getLevelSessions(sessionArr);
                            for (Map.Entry<String, Boolean> session : sessions.entrySet()) {
                                boolean isPrimary = levels.get(session.getKey());
                                boolean isCompleted = session.getValue();
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
        protected void onPostExecute(Boolean isSuccessful) {
            if (isSuccessful) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
