package tcd.android.com.codecombatmobile.ui;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Response;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.course.TClassroom;
import tcd.android.com.codecombatmobile.data.user.User;
import tcd.android.com.codecombatmobile.ui.adapter.TClassroomAdapter;
import tcd.android.com.codecombatmobile.util.CCDataUtil;
import tcd.android.com.codecombatmobile.util.CCRequestManager;
import tcd.android.com.codecombatmobile.util.DataUtil;

public class TClassroomListActivity extends ClassroomListActivity {

    private List<TClassroom> mClassrooms = new ArrayList<>();
    private TClassroomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tclassroom_list);
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
                        List<TClassroom> classrooms = CCDataUtil.getTClassroomList(response);
                        mClassrooms.addAll(classrooms);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }, null);
        }
    }
}
