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
import tcd.android.com.codecombatmobile.ui.adapter.StudentClassAdapter;
import tcd.android.com.codecombatmobile.util.DataUtil;

public class StudentClassActivity extends AppCompatActivity {

    private List<StudentClass> mClasses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_class);

        mClasses = DataUtil.getDebugStudentClassList(10);
        initClassList();

        configureActionBar();
    }

    private void configureActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initClassList() {
        RecyclerView stuClassListRv = findViewById(R.id.rv_student_classes);
        stuClassListRv.setLayoutManager(new LinearLayoutManager(this));
        stuClassListRv.setItemAnimator(new DefaultItemAnimator());

        StudentClassAdapter adapter = new StudentClassAdapter(this, mClasses);
        stuClassListRv.setAdapter(adapter);
    }
}
