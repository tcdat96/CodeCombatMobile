package tcd.android.com.codecombatmobile.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.course.SClassroom;
import tcd.android.com.codecombatmobile.data.game.Level;
import tcd.android.com.codecombatmobile.data.game.Session;
import tcd.android.com.codecombatmobile.data.user.User;
import tcd.android.com.codecombatmobile.ui.widget.GameMapView;
import tcd.android.com.codecombatmobile.util.CCDataUtil;
import tcd.android.com.codecombatmobile.util.CCRequestManager;
import tcd.android.com.codecombatmobile.util.DataUtil;

public class GameMapActivity extends AppCompatActivity {
    public static final String ARG_STUDENT_CLASSROOM = "argStudentClassroom";
    public static final int RC_GAME_LEVEL = 0;

    private ProgressBar mLoadingProgressBar;
    private GameMapView mMapView;

    private CCRequestManager mManager;
    private AsyncTask<Void, Void, Boolean> mAsyncTask = null;
    private SClassroom mClassroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_map);

        // get course info
        Intent data = getIntent();
        if (data == null || !data.hasExtra(ARG_STUDENT_CLASSROOM)) {
            finish();
            return;
        }
        mClassroom = (SClassroom) data.getSerializableExtra(ARG_STUDENT_CLASSROOM);

        initUiComponents();
        mManager = CCRequestManager.getInstance(this);

        // retrieve map from server
        mAsyncTask = new GetMapTask();
        mAsyncTask.execute();

        // for user to be able to change the volume of the proper stream
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void initUiComponents() {
        mLoadingProgressBar = findViewById(R.id.pb_loading);
        mMapView = findViewById(R.id.game_map_view);
    }

    private void updateLoadingUi(boolean isLoading) {
        if (isLoading) {
            mMapView.setVisibility(View.INVISIBLE);
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        } else {
            mMapView.setVisibility(View.VISIBLE);
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_GAME_LEVEL:
                if (resultCode == RESULT_OK) {
                    if (mAsyncTask != null) {
                        mAsyncTask.cancel(true);
                    }
                    mAsyncTask = new GetLevelSessions();
                    mAsyncTask.execute();
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class GetMapTask extends AsyncTask<Void, Void, Boolean> {

        private Bitmap mBackground;
        @NonNull
        private List<Level> mLevels = new ArrayList<>();

        GetMapTask() {
            updateLoadingUi(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject campaignObj = mManager.requestCampaignSync(mClassroom.getCampaignId());
            if (campaignObj != null) {
                try {
                    // map background
                    JSONArray bgrUrls = campaignObj.getJSONArray("backgroundImage");
                    String path = bgrUrls.getJSONObject(0).getString("image");
                    String bgrUrl = mManager.getFileUrl(path);
                    mBackground = Glide.with(GameMapActivity.this).asBitmap().load(bgrUrl).submit().get();
                    if (mBackground == null) {
                        return false;
                    }

                    // map levels
                    JSONObject levelsObj = campaignObj.getJSONObject("levels");
                    Iterator<?> keys = levelsObj.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        if (levelsObj.get(key) instanceof JSONObject) {
                            JSONObject levelObj = (JSONObject) levelsObj.get(key);
                            Level level = CCDataUtil.getLevel(levelObj);
                            mLevels.add(level);
                        }
                    }
                    if (mLevels.size() == 0) {
                        return false;
                    }

                    // sort levels
                    Collections.sort(mLevels, new Comparator<Level>() {
                        @Override
                        public int compare(Level lv1, Level lv2) {
                            return lv1.getCampaignIndex() - lv2.getCampaignIndex();
                        }
                    });

                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mAsyncTask = null;

            if (success) {
                mMapView.setClassroom(mClassroom);
                mMapView.setMapBackground(mBackground);
                mMapView.setLevels(mLevels);

                mAsyncTask = new GetLevelSessions();
                mAsyncTask.execute();
            } else {
                finish();
                Toast.makeText(getApplicationContext(), R.string.error_get_data_message, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mAsyncTask = null;
        }
    }

    private class GetLevelSessions extends AsyncTask<Void, Void, Boolean> {

        @Nullable
        private List<Session> mLevelSessions;

        GetLevelSessions() {
            updateLoadingUi(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                User user = DataUtil.getUserData(GameMapActivity.this);
                if (user != null) {
                    JSONArray sessionsObj = mManager.requestLevelSessionsSync(mClassroom.getInstanceId(), user.getId());
                    if (sessionsObj != null) {
                        mLevelSessions = CCDataUtil.parseLevelSessions(sessionsObj);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mLevelSessions != null;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if (success && mLevelSessions != null) {
                mMapView.setLevelSessions(mLevelSessions);
                updateLoadingUi(false);
            } else {
                finish();
                Toast.makeText(getApplicationContext(), R.string.error_get_data_message, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mAsyncTask = null;
        }
    }
}
