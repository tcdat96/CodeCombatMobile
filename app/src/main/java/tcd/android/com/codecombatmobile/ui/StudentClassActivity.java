package tcd.android.com.codecombatmobile.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.Course;
import tcd.android.com.codecombatmobile.data.StudentClass;
import tcd.android.com.codecombatmobile.data.User.User;
import tcd.android.com.codecombatmobile.ui.adapter.StudentClassAdapter;
import tcd.android.com.codecombatmobile.util.DataUtil;
import tcd.android.com.codecombatmobile.util.NetworkUtil;

public class StudentClassActivity extends SearchViewActivity {

    private List<StudentClass> mClasses = new ArrayList<>();
    private StudentClassAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_class);
        configureActionBar();

        initClassList();
        requestClassList();
    }

    private void initClassList() {
        RecyclerView stuClassListRv = findViewById(R.id.rv_student_classes);
        stuClassListRv.setLayoutManager(new LinearLayoutManager(this));
        stuClassListRv.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new StudentClassAdapter(this, mClasses);
        stuClassListRv.setAdapter(mAdapter);
    }

    private void requestClassList() {
        final User user = DataUtil.getUserData(this);
        if (user != null) {
            new GetStudentClassListTask(user).execute();
        }
    }

    private class GetStudentClassListTask extends AsyncTask<Void, Void, Boolean> {

        private final NetworkUtil mNetworkUtil;
        private final User mUser;
        private List<Course> mCourses;

        GetStudentClassListTask(User user) {
            mUser = user;
            mNetworkUtil = NetworkUtil.getInstance(StudentClassActivity.this);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // get all courses
                JSONArray courseArr = mNetworkUtil.requestCoursesSync();
                if (courseArr == null) {
                    return false;
                }
                mCourses = parseCourses(courseArr);
                // get student's classrooms
                JSONArray classroomArr = mNetworkUtil.requestStudentClassListSync(mUser.getId());
                if (classroomArr == null) {
                    return false;
                }
                List<StudentClass> classrooms = parseClasses(classroomArr);
                mClasses.addAll(classrooms);
                // get class instances
                JSONArray instanceArr = mNetworkUtil.requestCourseInstances(mUser.getId());
                if (instanceArr == null) {
                    return false;
                }
                parseInstances(instanceArr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        private List<Course> parseCourses(JSONArray coursesJsonArr) throws JSONException {
            int length = coursesJsonArr.length();
            List<Course> courses = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                JSONObject course = coursesJsonArr.getJSONObject(i);
                String id = course.getString("_id");
                String name = course.getString("name");
                String description = course.getString("description");

                courses.add(new Course(id, name, description));
            }
            return courses;
        }

        private List<StudentClass> parseClasses(JSONArray classroomsJsonArr) throws JSONException {
            int length = classroomsJsonArr.length();
            List<StudentClass> classrooms = new ArrayList<>(length);
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
                String teacher = "Michael Keaton";
                // course's levels
                JSONArray courseArr = classObj.getJSONArray("courses");
                parseCourseLevels(courseArr);

                StudentClass classroom = new StudentClass(id, language, name, teacher);
                classrooms.add(classroom);
            }
            return classrooms;
        }

        private String getOwnerName(String ownerId) throws JSONException {
            NetworkUtil util = NetworkUtil.getInstance(StudentClassActivity.this);
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
                if (course != null && course.getLevels() == null) {
                    JSONArray levelArr = courseObj.getJSONArray("levels");
                    Map<String, String> levels = new HashMap<>();
                    for (int j = 0; j < levelArr.length(); j++) {
                        JSONObject levelObj = levelArr.getJSONObject(j);
                        levels.put(levelObj.getString("original"), levelObj.getString("name"));
                    }
                    course.setLevels(levels);
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
                StudentClass classroom = getClassroomById(instObj.getString("classroomID"));
                if (classroom != null) {
                    Course course = getCourseById(instObj.getString("courseID"));
                    if (course != null) {
                        // course name
                        classroom.setCourseName(course.getName());
                        // progress
                        String instId = instObj.getString("_id");
                        JSONArray sessions = mNetworkUtil.requestLevelSessions(instId);
                        if (sessions != null) {
                            classroom.setProgress(sessions.length() * 100 / course.getLevels().size());
                        }
                    }
                }
            }
        }

        @Nullable
        private StudentClass getClassroomById(String id) {
            for (int i = 0; i < mClasses.size(); i++) {
                if (mClasses.get(i).getId().equals(id)) {
                    return mClasses.get(i);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            if (isSuccessful) {
                mClasses.addAll(new ArrayList<>(mClasses));
                mClasses.addAll(new ArrayList<>(mClasses));
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
