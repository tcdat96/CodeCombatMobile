package tcd.android.com.codecombatmobile.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import tcd.android.com.codecombatmobile.data.user.Student;
import tcd.android.com.codecombatmobile.data.user.Teacher;
import tcd.android.com.codecombatmobile.data.user.User;
import tcd.android.com.codecombatmobile.util.DataUtil;

public abstract class AccountRequestActivity extends AppCompatActivity {

    public static final int RESULT_FAILED = 2;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    protected AsyncTask mAuthTask = null;

    protected View mLoginFormView;
    protected View mProgressView;

    /**
     * Shows the progress UI and hides the login form.
     */
    protected void showProgress(final boolean show) {
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

    public class AccountRequestTask extends AsyncTask<Void, Void, Boolean> {

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
                DataUtil.saveUserData(AccountRequestActivity.this, user);
            }
            return result != null;
        }

        protected JSONObject getResponse() {
            return null;
        }

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
                setResult(RESULT_OK);
                finish();
            } else {
                setResult(RESULT_FAILED);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
