package tcd.android.com.codecombatmobile.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText mOrganizationEditText, mPhoneNumberEditText;
    private Teacher mTeacher;
    private String mPassword;

    private EditText mErrorView;

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

        mOrganizationEditText = findViewById(R.id.edt_school_name);
        mPhoneNumberEditText = findViewById(R.id.edt_phone_number);

        setUpCountriesSpinner();

        Button signUpButton = findViewById(R.id.btn_sign_up);
        signUpButton.setOnClickListener(this);
    }

    private void setUpCountriesSpinner() {
        mCountriesSpinner = findViewById(R.id.spinner_country_list);
        List<String> countryList = retrieveCountryList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countryList);
        mCountriesSpinner.setAdapter(adapter);

        // set default country
        Locale locale = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                getResources().getConfiguration().getLocales().get(0) :
                getResources().getConfiguration().locale;
        String countryName = locale.getDisplayCountry();
        if (countryName != null) {
            int position = adapter.getPosition(countryName);
            if (position > 0) {
                mCountriesSpinner.setSelection(position);
            }
        }
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
                attemptSignUp();
                break;
        }
    }

    private void attemptSignUp() {
        // reset error
        if (mErrorView != null) {
            mErrorView.setError(null);
        }

        // validate editTexts
        String organization = mOrganizationEditText.getText().toString();
        String phoneNumber = mPhoneNumberEditText.getText().toString();
        boolean isValid = validateOrganization(organization) && validatePhoneNumber(phoneNumber);

        if (isValid) {
            // retrieve information
            String country = mCountriesSpinner.getSelectedItem().toString();
            String role = ((Spinner) findViewById(R.id.spinner_role)).getSelectedItem().toString();
            String estimatedStudents = ((Spinner) findViewById(R.id.spinner_estimated_students)).getSelectedItem().toString();
            mTeacher.update(organization, country, phoneNumber, role, estimatedStudents);
            // then sign up
            mAuthTask = new TeacherSignUpTask(mTeacher, mPassword);
            ((TeacherSignUpTask) mAuthTask).execute();
        } else {
            mErrorView.requestFocus();
        }
    }

    private boolean validateOrganization(String organization) {
        if (TextUtils.isEmpty(organization)) {
            mErrorView = mOrganizationEditText;
            mErrorView.setError(getString(R.string.error_field_required));
            return false;
        }
        return true;
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            mErrorView = mPhoneNumberEditText;
            mErrorView.setError(getString(R.string.error_field_required));
            return false;
        } else if (!isPhoneNumberValid(phoneNumber)) {
            mErrorView = mPhoneNumberEditText;
            mErrorView.setError(getString(R.string.error_invalid_phone_number));
            return false;
        }
        return true;
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        return !TextUtils.isEmpty(phoneNumber) && phoneNumber.length() >= 10;
    }

    private class TeacherSignUpTask extends AccountRequestTask {

        TeacherSignUpTask(Teacher teacher, String password) {
            super(teacher, password);
        }

        @Override
        protected JSONObject getResponse() {
            return CCRequestManager.getInstance(MoreInfoTeacherActivity.this).signUpSync(mUser, mPassword);
        }
    }
}
