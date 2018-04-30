package tcd.android.com.codecombatmobile.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.StudentClass;
import tcd.android.com.codecombatmobile.data.TeacherClass;
import tcd.android.com.codecombatmobile.ui.adapter.StudentClassAdapter;
import tcd.android.com.codecombatmobile.ui.adapter.TeacherClassAdapter;
import tcd.android.com.codecombatmobile.util.DataUtil;

public class TeacherClassActivity extends AppCompatActivity {

    private List<TeacherClass> mClasses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_class);

        mClasses = DataUtil.getDebugTeacherClassList(10);
        initClassList();

        configureActionBar();
    }

    private void configureActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initClassList() {
        RecyclerView teacherClassListRv = findViewById(R.id.rv_student_classes);
        teacherClassListRv.setLayoutManager(new LinearLayoutManager(this));
        teacherClassListRv.setItemAnimator(new DefaultItemAnimator());

        TeacherClassAdapter adapter = new TeacherClassAdapter(this, mClasses);
        teacherClassListRv.setAdapter(adapter);
    }
}
