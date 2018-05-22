package tcd.android.com.codecombatmobile.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.User.Student;
import tcd.android.com.codecombatmobile.data.User.Teacher;
import tcd.android.com.codecombatmobile.data.User.User;
import tcd.android.com.codecombatmobile.util.DataUtil;
import tcd.android.com.codecombatmobile.util.NetworkUtil;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int RC_PERMISSION_READ_CONTACTS = 0;
    private static final int RC_GOOGLE_SIGN_IN = 1;
    private static final int RC_FACEBOOK_SIGN_IN = 2;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private AsyncTask mAuthTask = null;
    private GoogleSignInClient mGoogleSignInClient;

    // UI references.
    private TextView mSignInTextView, mSignUpTextView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private TextView mStudentTextView, mTeacherTextView;
    private LinearLayout mAccountTypeLayout, mUsernameLayout, mFirstLastNameLayout;
    private EditText mUsernameView, mFirstNameView, mLastNameView;
    private Button mRequestButton;
    private View mProgressView;
    private View mLoginFormView;

    private EditText mErrorView;

    private boolean mIsSignInSelected = true;
    private boolean mIsStudentSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setContentView(R.layout.activity_login);

        // hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initUiComponents();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void initUiComponents() {
        mEmailView = findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mRequestButton = findViewById(R.id.btn_account_request);
        mRequestButton.setOnClickListener(this);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // sign in or sign up options
        mSignInTextView = findViewById(R.id.tv_sign_in);
        mSignInTextView.setOnClickListener(this);
        mSignUpTextView = findViewById(R.id.tv_sign_up);
        mSignUpTextView.setOnClickListener(this);

        // student or teacher option (for sign up)
        mStudentTextView = findViewById(R.id.tv_student);
        mStudentTextView.setOnClickListener(this);
        mTeacherTextView = findViewById(R.id.tv_teacher);
        mTeacherTextView.setOnClickListener(this);
        mUsernameLayout = findViewById(R.id.ll_username);
        mUsernameView = findViewById(R.id.edt_username);
        mFirstLastNameLayout = findViewById(R.id.ll_user_name);
        mFirstNameView = findViewById(R.id.edt_first_name);
        mLastNameView = findViewById(R.id.edt_last_name);
        mAccountTypeLayout = findViewById(R.id.ll_account_type);

        // default options
        toggleSignInOption();

        // debug
        boolean isStudent = true;
        String studentName = "student1";
        mEmailView.setText(isStudent ? studentName + "@gmail.com" : "teacher@gmail.com");
        mPasswordView.setText(isStudent ? studentName : "teacher");
        mUsernameView.setText(isStudent ? studentName : "ndhuy");
        mFirstNameView.setText(isStudent ? "" : "Duc Huy");
        mLastNameView.setText(isStudent ? "" : "Nguyen");

        SignInButton googleSignInButton = findViewById(R.id.btn_google_sign_in);
        googleSignInButton.setOnClickListener(this);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, RC_PERMISSION_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, RC_PERMISSION_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == RC_PERMISSION_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sign_in:
                mIsSignInSelected = true;
                toggleSignInOption();
                break;
            case R.id.tv_sign_up:
                mIsSignInSelected = false;
                toggleSignInOption();
                break;
            case R.id.tv_student:
                mIsStudentSelected = true;
                toggleStudentOption();
                break;
            case R.id.tv_teacher:
                mIsStudentSelected = false;
                toggleStudentOption();
                break;
            case R.id.btn_account_request:
                attemptLogin();
                break;
            case R.id.btn_google_sign_in:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
                break;
            default:
                break;
        }
    }

    private void toggleSignInOption() {
        TransitionManager.beginDelayedTransition((ScrollView) mLoginFormView);

        // show/hide sign up options
        int visibility = mIsSignInSelected ? View.GONE : View.VISIBLE;
        mAccountTypeLayout.setVisibility(visibility);

        // highlight selection
        if (mIsSignInSelected) {
            mSignInTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.shape_circle);
            mSignInTextView.setTextColor(Color.WHITE);
            mSignUpTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mSignUpTextView.setTextColor(Color.GRAY);

            mRequestButton.setText(R.string.action_sign_in);
            mUsernameLayout.setVisibility(View.GONE);
            mFirstLastNameLayout.setVisibility(View.GONE);
        } else {
            mSignInTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mSignInTextView.setTextColor(Color.GRAY);
            mSignUpTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.shape_circle);
            mSignUpTextView.setTextColor(Color.WHITE);

            mRequestButton.setText(R.string.action_sign_up);
            toggleStudentOption();
        }
    }

    private void toggleStudentOption() {
        TransitionManager.beginDelayedTransition((ScrollView) mLoginFormView);

        int selectedColor = ContextCompat.getColor(this, R.color.colorAccent);
        if (mIsStudentSelected) {
            mStudentTextView.setTextColor(selectedColor);
            mTeacherTextView.setTextColor(Color.GRAY);

            mUsernameLayout.setVisibility(View.VISIBLE);
            mFirstLastNameLayout.setVisibility(View.GONE);
        } else {
            mStudentTextView.setTextColor(Color.GRAY);
            mTeacherTextView.setTextColor(selectedColor);

            mUsernameLayout.setVisibility(View.GONE);
            mFirstLastNameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        if (mErrorView != null) {
            mErrorView.setError(null);
        }

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String username = mUsernameView.getText().toString();
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();

        // enforce all required fields
        boolean cancel = validateEmptyEmail(email)
                || validateEmptyNames(username, firstName, lastName)
                || validateEmptyPassword(password);

        if (cancel) {
            // show error
            mErrorView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user attempt.
            showProgress(true);
            if (mIsSignInSelected) {
                mAuthTask = new UserLoginTask(new User(email), password);
                ((UserLoginTask) mAuthTask).execute();
            } else {
                User user = mIsStudentSelected ?
                        new Student(email, username) :
                        new Teacher(email, firstName, lastName);
                mAuthTask = new CheckAvailabilityTask(user, password);
                ((CheckAvailabilityTask) mAuthTask).execute();
            }
        }
    }

    private boolean validateEmptyPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            mErrorView = mPasswordView;
            mErrorView.setError(getString(R.string.error_field_required));
            return true;
        } else if (!isPasswordValid(password)) {
            mErrorView = mPasswordView;
            mErrorView.setError(getString(R.string.error_invalid_password));
            return true;
        }
        return false;
    }

    private boolean validateEmptyEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            mErrorView = mEmailView;
            mEmailView.setError(getString(R.string.error_field_required));
            return true;
        } else if (!isEmailValid(email)) {
            mErrorView = mEmailView;
            mEmailView.setError(getString(R.string.error_invalid_email));
            return true;
        }
        return false;
    }

    private boolean validateEmptyNames(String username, String firstName, String lastName) {
        if (mIsSignInSelected) {
            return false;
        }

        if (mIsStudentSelected) {
            if (TextUtils.isEmpty(username)) {
                mErrorView = mUsernameView;
                mUsernameView.setError(getString(R.string.error_field_required));
                return true;
            }
        } else {
            if (TextUtils.isEmpty(firstName)) {
                mErrorView = mFirstNameView;
                mFirstNameView.setError(getString(R.string.error_field_required));
                return true;
            } else if (TextUtils.isEmpty(lastName)) {
                mErrorView = mLastNameView;
                mLastNameView.setError(getString(R.string.error_field_required));
                return true;
            }
        }
        return false;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_GOOGLE_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    updateUI(account);
                } catch (ApiException e) {
                    Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                    updateUI(null);
                }
                break;
            default:
                break;
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        Log.d(TAG, "updateUI: " + (account != null ? account.getEmail() : null));
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private class CheckAvailabilityTask extends AsyncTask<Void, Void, Void> {

        private User mUser;
        private String mPassword;
        private String mErrorMsg;

        CheckAvailabilityTask(User user, String password) {
            mUser = user;
            mPassword = password;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // validate email
                JSONObject response = NetworkUtil.getInstance(LoginActivity.this).validateEmail(mUser.getEmail());
                boolean isEmailValid = response != null && !response.getBoolean("exists");
                if (!isEmailValid) {
                    mErrorView = mEmailView;
                    mErrorMsg = getString(R.string.error_in_used_email);
                    return null;
                }
                // validate username
                if (mUser instanceof Student) {
                    Student student = (Student) mUser;
                    response = NetworkUtil.getInstance(LoginActivity.this).validateUsername(student.getUsername());
                    boolean isUsernameValid = response != null && !response.getBoolean("conflicts");
                    if (!isUsernameValid) {
                        mErrorView = mUsernameView;
                        mErrorMsg = getString(R.string.error_in_used_username);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mAuthTask = null;
            if (mErrorView != null) {
                mErrorView.setError(mErrorMsg);
                mErrorView.requestFocus();
                showProgress(false);
            } else {
                if (mUser instanceof Student) {
                    new StudentSignUpTask((Student) mUser, mPassword).execute();
                } else {
                    // TODO: 20/05/2018 launch more info activity and handle sign up task there
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public abstract class AccountRequestTask extends AsyncTask<Void, Void, Boolean> {

        protected User mUser;
        protected String mPassword;

        AccountRequestTask(User user, String password) {
            mUser = user;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject result = getResponse();
            if (result != null) {
                User user = getUserData(result);
                DataUtil.saveUserData(LoginActivity.this, user);
            }
            return result != null;
        }

        protected abstract JSONObject getResponse();

        private User getUserData(JSONObject result) {
            User user = null;
            try {
                String id = result.getString("_id");
                String email = result.getString("email");
                String role = result.getString("role");
                if ("teacher".equalsIgnoreCase(role)) {
                    String firstName = result.getString("firstName");
                    String lastName = result.getString("lastName");
                    user = new Teacher(email, firstName, lastName);
                } else {
                    String username = result.getString("name");
                    user = new Student(email, username);
                }
                user.setId(id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return user;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class StudentSignUpTask extends AccountRequestTask {

        StudentSignUpTask(Student student, String password) {
            super(student, password);
        }

        @Override
        protected JSONObject getResponse() {
            return NetworkUtil.getInstance(LoginActivity.this).signUpSync(mUser, mPassword);
        }
    }

    public class UserLoginTask extends AccountRequestTask {

        UserLoginTask(User user, String password) {
            super(user, password);
        }

        @Override
        protected JSONObject getResponse() {
            return NetworkUtil.getInstance(LoginActivity.this).logInSync(mUser.getEmail(), mPassword);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            if (!success) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }
    }
}

