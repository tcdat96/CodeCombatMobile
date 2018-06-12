package tcd.android.com.codecombatmobile.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import java.util.Map;
import java.util.concurrent.ExecutionException;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.course.SClassroom;
import tcd.android.com.codecombatmobile.data.level.Level;
import tcd.android.com.codecombatmobile.ui.widget.GameMapView;
import tcd.android.com.codecombatmobile.util.CCDataUtil;
import tcd.android.com.codecombatmobile.util.CCRequestManager;

public class GameMapActivity extends AppCompatActivity {
    public static final String ARG_STUDENT_CLASSROOM = "argStudentClassroom";

    private GameMapView mMapView;

    private GetMapTask mAuthTask = null;
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

        // retrieve map from server
        mAuthTask = new GetMapTask();
        mAuthTask.execute();

        // for user to be able to change the volume of the proper stream
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void initUiComponents() {
        mMapView = findViewById(R.id.game_map_view);
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
        mAuthTask.cancel(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.destroy();
    }

    private class GetMapTask extends AsyncTask<Void, Void, Boolean> {

        private Bitmap mBackground;
        @NonNull
        private List<Level> mLevels = new ArrayList<>();
        private Map<String, Boolean> mLevelSessions;

        @Override
        protected Boolean doInBackground(Void... voids) {
            CCRequestManager manager = CCRequestManager.getInstance(GameMapActivity.this);
            JSONObject campaignObj = manager.requestCampaignSync(mClassroom.getCampaignId());
            if (campaignObj != null) {
                try {
                    // map background
                    JSONArray bgrUrls = campaignObj.getJSONArray("backgroundImage");
                    String path = bgrUrls.getJSONObject(0).getString("image");
                    String bgrUrl = manager.getFileUrl(path);
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

                    // level sessions
                    JSONArray sessionsObj = manager.requestLevelSessionsSync(mClassroom.getInstanceId());
                    if (sessionsObj != null) {
                        mLevelSessions = CCDataUtil.getLevelSessions(sessionsObj);
                    }
                    return mLevelSessions != null;
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

            if (success) {
                mMapView.setClassroom(mClassroom);
                mMapView.setMapBackground(mBackground);
                mMapView.setLevels(mLevels);
                mMapView.setLevelSessions(mLevelSessions);

                findViewById(R.id.pb_game_map).setVisibility(View.GONE);
                mMapView.setVisibility(View.VISIBLE);
            } else {
                finish();
                Toast.makeText(getApplicationContext(), R.string.error_get_level_message, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mAuthTask = null;
        }
    }
}
