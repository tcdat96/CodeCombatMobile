package tcd.android.com.codecombatmobile.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.TeacherClass;
import tcd.android.com.codecombatmobile.data.User.User;
import tcd.android.com.codecombatmobile.ui.adapter.TeacherClassAdapter;
import tcd.android.com.codecombatmobile.util.DataUtil;
import tcd.android.com.codecombatmobile.util.NetworkUtil;

public class TeacherClassActivity extends SearchViewActivity {

    private List<TeacherClass> mClasses = new ArrayList<>();
    private TeacherClassAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_class);
        configureActionBar();

        initClassListView();
        requestClassList();
    }

    private void initClassListView() {
        RecyclerView teacherClassListRv = findViewById(R.id.rv_student_classes);
        teacherClassListRv.setLayoutManager(new LinearLayoutManager(this));
        teacherClassListRv.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new TeacherClassAdapter(this, mClasses);
        teacherClassListRv.setAdapter(mAdapter);
    }

    private void requestClassList() {
        User user = DataUtil.getUserData(this);
        if (user != null) {
            NetworkUtil.getInstance(this).requestClassList(user.getId(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    if (response != null) {
                        List<TeacherClass> classes = readClassList(response);
                        // TODO: 20/05/2018 remove this
                        classes.addAll(new ArrayList<>(classes));
                        classes.addAll(new ArrayList<>(classes));
                        mClasses.addAll(classes);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }, null);
        }
    }

    private List<TeacherClass> readClassList(@NonNull JSONArray response) {
        List<TeacherClass> classrooms = new ArrayList<>(response.length());
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject classObj = response.getJSONObject(i);
                TeacherClass classroom = readClass(classObj);
                classrooms.add(classroom);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return classrooms;
    }

    private TeacherClass readClass(JSONObject classObj) throws JSONException {
        String name = classObj.getString("name");
        String code = classObj.getString("codeCamel");

        // language
        JSONObject aceConfig = classObj.getJSONObject("aceConfig");
        String language = aceConfig != null ? aceConfig.getString("language") : "python";

        // number of students
        JSONArray members = classObj.getJSONArray("members");
        int studentTotal = members != null ? members.length() : 0;

        // TODO: 20/05/2018 do something with this
        int progress = new Random().nextInt(100);

        return new TeacherClass(language, name, code, studentTotal, progress);
    }
}
