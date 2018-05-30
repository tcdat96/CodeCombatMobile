package tcd.android.com.codecombatmobile.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.thang.Thang;
import tcd.android.com.codecombatmobile.data.thang.ThangType;
import tcd.android.com.codecombatmobile.data.course.Level;
import tcd.android.com.codecombatmobile.ui.widget.GameLevelView;
import tcd.android.com.codecombatmobile.ui.widget.ProgressBar;
import tcd.android.com.codecombatmobile.util.CCDataUtil;
import tcd.android.com.codecombatmobile.util.CCRequestManager;
import tcd.android.com.codecombatmobile.util.DataUtil;

public class GameLevelActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String ARG_LEVEL_DATA = "argLevelData";

    private GameLevelView mGameLevelView;
    private ProgressBar mTimeControlBar, mHpBar;

    private GetThangsTask mAsyncTask;
    private Level mLevel;
    @NonNull
    private List<Thang> mThangs = new ArrayList<>();
    @NonNull
    private List<ThangType> mThangTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // get level data
        Intent data = getIntent();
        if (data == null || !data.hasExtra(ARG_LEVEL_DATA)) {
            finish();
            return;
        }
        mLevel = (Level) data.getSerializableExtra(ARG_LEVEL_DATA);

        initUiComponents();

        mAsyncTask = new GetThangsTask();
        mAsyncTask.execute();
    }

    private void initUiComponents() {
        mGameLevelView = findViewById(R.id.game_view);

        mTimeControlBar = findViewById(R.id.pb_time_control);
        mTimeControlBar.setProgress(0.45f);
        mHpBar = findViewById(R.id.pb_hp);

        setButtonBackgrounds();
    }

    private void setButtonBackgrounds() {
        final int[] buttonIds = new int[] {R.id.btn_map, R.id.btn_instruction, R.id.btn_hints, R.id.btn_run};

        // prepare button background
        final Button mapButton = findViewById(R.id.btn_map);
        mapButton.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_game_button);
        bitmap = Bitmap.createScaledBitmap(bitmap, mapButton.getMeasuredWidth(), mapButton.getMeasuredHeight(), false);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);

        for (int buttonId : buttonIds) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(GameLevelActivity.this);
            button.setBackgroundDrawable(drawable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameLevelView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameLevelView.resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAsyncTask.cancel(true);
//        mGameLevelView.destroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_map:
                break;
            case R.id.btn_instruction:
                break;
            case R.id.btn_hints:
                break;
            case R.id.btn_run:
                break;
        }
    }

    private class GetThangsTask extends AsyncTask<Void, Void, Boolean> {

        private CCRequestManager mManager = CCRequestManager.getInstance(GameLevelActivity.this);

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // get level
                JSONObject levelObj = mManager.requestLevelDataSync(mLevel.getSlug());
                if (levelObj == null) {
                    return false;
                }

                // get thang list from level object
                JSONArray thangArr = levelObj.getJSONArray("thangs");
                mThangs = CCDataUtil.parseThangArr(thangArr);

                // get thang type ID list
                Set<String> thangTypes = CCDataUtil.getThangTypeSet(mThangs);
                JSONArray thangTypeArr = mManager.requestThangTypeListSync(thangTypes);
                if (thangTypeArr == null) {
                    return false;
                }

                // get thang types
                List<String> thangTypeIds = CCDataUtil.getThangTypeIds(thangTypeArr);
                for (String thangTypeId : thangTypeIds) {
                    JSONObject thangTypeObj = mManager.requestThangTypeSync(thangTypeId);
                    if (thangTypeObj != null) {
                        ThangType thangType = CCDataUtil.getThangType(thangTypeObj);
                        // get image (if exists)
                        if (thangType.getImagePath() != null) {
                            String url = mManager.getFileUrl(thangType.getImagePath());
                            thangType.setBitmap(DataUtil.getImageSync(GameLevelActivity.this, url));
                        }
                        mThangTypes.add(thangType);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            
            if (success) {
                mGameLevelView.setThangs(mThangs, mThangTypes);
            } else {
                Toast.makeText(GameLevelActivity.this, R.string.error_get_level_message, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mAsyncTask = null;
        }
    }
}
