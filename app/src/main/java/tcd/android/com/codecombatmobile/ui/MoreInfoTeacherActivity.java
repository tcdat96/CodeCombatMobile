package tcd.android.com.codecombatmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.user.Teacher;
import tcd.android.com.codecombatmobile.util.CCRequestManager;

public class MoreInfoTeacherActivity extends AccountRequestActivity implements View.OnClickListener{

    public static final String ARG_TEACHER_DATA = "argTeacherData";
    public static final String ARG_PASSWORD_DATA = "argPasswordData";

    private Spinner mCountriesSpinner;
    private Teacher mTeacher;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_teacher);
        setTitle(R.string.title_activity_more_info);

        Intent data = getIntent();
        if (data == null || !data.hasExtra(ARG_TEACHER_DATA)) {
            finish();
            return;
        }
        mTeacher = (Teacher) data.getSerializableExtra(ARG_TEACHER_DATA);
        mPassword = data.getStringExtra(ARG_PASSWORD_DATA);

        initUiComponents();
    }

    private void initUiComponents() {
        mLoginFormView = findViewById(R.id.sv_request_form);
        mProgressView = findViewById(R.id.pb_login);

        // countries list spinner
        mCountriesSpinner = findViewById(R.id.spinner_country_list);
        List<String> countryList = retrieveCountryList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countryList);
        mCountriesSpinner.setAdapter(adapter);

        Button signUpButton = findViewById(R.id.btn_sign_up);
        signUpButton.setOnClickListener(this);
    }

    private List<String> retrieveCountryList() {
        Locale[] locales = Locale.getAvailableLocales();
        List<String> countries = new ArrayList<>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length()>0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);
        return countries;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_up:
                mAuthTask = new TeacherSignUpTask(mTeacher, mPassword);
                ((TeacherSignUpTask)mAuthTask).execute();
            break;
        }
    }

    public class TeacherSignUpTask extends AccountRequestTask {

        TeacherSignUpTask(Teacher teacher, String password) {
            super(teacher, password);
        }

        @Override
        protected JSONObject getResponse() {
            return CCRequestManager.getInstance(MoreInfoTeacherActivity.this).signUpSync(mUser, mPassword);
        }
    }
}
