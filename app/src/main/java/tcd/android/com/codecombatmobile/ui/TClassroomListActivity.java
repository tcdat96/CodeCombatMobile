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
import tcd.android.com.codecombatmobile.data.course.TClassroom;
import tcd.android.com.codecombatmobile.data.user.User;
import tcd.android.com.codecombatmobile.ui.adapter.TClassroomAdapter;
import tcd.android.com.codecombatmobile.util.DataUtil;
import tcd.android.com.codecombatmobile.util.CCRequestManager;

public class TClassroomListActivity extends ClassroomListActivity {

    private List<TClassroom> mClassrooms = new ArrayList<>();
    private TClassroomAdapter mAdapter;

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

        mAdapter = new TClassroomAdapter(this, mClassrooms);
        teacherClassListRv.setAdapter(mAdapter);
    }

    private void requestClassList() {
        User user = DataUtil.getUserData(this);
        if (user != null) {
            CCRequestManager.getInstance(this).requestTeacherClassList(user.getId(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    if (response != null) {
                        List<TClassroom> classrooms = readClassroomList(response);
                        // TODO: 20/05/2018 remove this
                        classrooms.addAll(new ArrayList<>(classrooms));
                        classrooms.addAll(new ArrayList<>(classrooms));
                        mClassrooms.addAll(classrooms);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }, null);
        }
    }

    private List<TClassroom> readClassroomList(@NonNull JSONArray response) {
        List<TClassroom> classrooms = new ArrayList<>(response.length());
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject classObj = response.getJSONObject(i);
                TClassroom classroom = readClassroom(classObj);
                classrooms.add(classroom);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return classrooms;
    }

    private TClassroom readClassroom(JSONObject classObj) throws JSONException {
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

        return new TClassroom(language, name, code, studentTotal, progress);
    }
}
