package tcd.android.com.codecombatmobile.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.user.User;
import tcd.android.com.codecombatmobile.util.CCRequestManager;
import tcd.android.com.codecombatmobile.util.DataUtil;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

import static tcd.android.com.codecombatmobile.ui.AccountRequestActivity.RESULT_FAILED;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int RC_LOGIN_ATTEMPT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        DisplayUtil.hideActionBar(this);
        new CheckLoggedInStateTask(this).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_LOGIN_ATTEMPT:
                setResult(resultCode);
                if (resultCode == RESULT_FAILED) {
                    Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private static class CheckLoggedInStateTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<Context> mContext;
        private User mUser;

        CheckLoggedInStateTask(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Context context = mContext.get();
            if (context != null) {
                mUser = DataUtil.getUserData(context);
                if (mUser == null) {
                    return false;
                }

                JSONObject profile = CCRequestManager.getInstance(context).requestUserProfile(mUser.getId());
                return profile != null;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isLoggedIn) {
            super.onPostExecute(isLoggedIn);
            Context context = mContext.get();
            if (context != null) {
                Activity activity = (Activity) context;
                if (isLoggedIn) {
                    activity.setResult(RESULT_OK);
                    activity.finish();
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    activity.startActivityForResult(intent, RC_LOGIN_ATTEMPT);
                }
            }
        }
    }
}
