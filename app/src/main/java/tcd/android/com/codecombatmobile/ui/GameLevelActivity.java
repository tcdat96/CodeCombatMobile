package tcd.android.com.codecombatmobile.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.level.Goal;
import tcd.android.com.codecombatmobile.data.level.Thang;
import tcd.android.com.codecombatmobile.data.level.ThangType;
import tcd.android.com.codecombatmobile.ui.widget.GameLevelView;
import tcd.android.com.codecombatmobile.ui.widget.ProgressBar;
import tcd.android.com.codecombatmobile.util.CCDataUtil;
import tcd.android.com.codecombatmobile.util.CCRequestManager;
import tcd.android.com.codecombatmobile.util.DataUtil;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class GameLevelActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String ARG_LEVEL_ID = "argLevelId";

    private GameLevelView mGameLevelView;
    private ProgressBar mTimeControlBar, mHpBar;

    private GetThangsTask mAsyncTask;
    String mLevelId = null;

    @NonNull
    private List<Goal> mGoals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_level);

        // get level data
        Intent data = getIntent();
        mLevelId = data != null && data.hasExtra(ARG_LEVEL_ID) ? data.getStringExtra(ARG_LEVEL_ID) : null;
        if (mLevelId == null) {
            finish();
            return;
        }

        initUiComponents();

        mAsyncTask = new GetThangsTask();
        mAsyncTask.execute();
    }

    private void initUiComponents() {
        mGameLevelView = findViewById(R.id.game_view);

        mTimeControlBar = findViewById(R.id.pb_time_control);
        mTimeControlBar.setProgress(0.45f);
        mHpBar = findViewById(R.id.pb_hp);

        // buttons
        findViewById(R.id.btn_map).setOnClickListener(this);
        findViewById(R.id.btn_goals).setOnClickListener(this);
        findViewById(R.id.btn_hints).setOnClickListener(this);
        findViewById(R.id.btn_run).setOnClickListener(this);

        setButtonBackgrounds();
    }

    private void setButtonBackgrounds() {
        final int[] buttonIds = new int[] {R.id.btn_map, R.id.btn_goals, R.id.btn_hints, R.id.btn_run};

        // prepare button background
        final Button mapButton = findViewById(R.id.btn_map);
        mapButton.measure(WRAP_CONTENT, WRAP_CONTENT);
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
            case R.id.btn_goals:
                showGoalsDialog();
                break;
            case R.id.btn_hints:
                break;
            case R.id.btn_run:
                break;
        }
    }

    private void showGoalsDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_goals_info);

        // show all goals
        LinearLayout goalContainerLayout = dialog.findViewById(R.id.ll_goal_container);
        boolean isAllCompleted = true;
        for (Goal goal : mGoals) {
            TextView goalTextView = getGoalTextView(goal);
            goalContainerLayout.addView(goalTextView);

            if (!goal.isCompleted()) {
                isAllCompleted = false;
            }
        }

        // goal state
        TextView stateTextView = dialog.findViewById(R.id.tv_goal_state);
        if (isAllCompleted) {
            stateTextView.setTextColor(ContextCompat.getColor(this, R.color.complete_level_color));
            stateTextView.setText(R.string.success);
        } else {
            stateTextView.setTextColor(ContextCompat.getColor(this, R.color.incomplete_state_color));
            stateTextView.setText(R.string.incomplete);
        }

        dialog.show();
    }

    private TextView getGoalTextView(Goal goal) {
        TextView goalTv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.setMargins(0, 16, 0, 0);
        goalTv.setLayoutParams(params);

        TextViewCompat.setTextAppearance(goalTv, R.style.TextAppearance_AppCompat_Medium);
        goalTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        int color = goal.isCompleted() ? ContextCompat.getColor(this, R.color.complete_level_color) : Color.BLACK;
        goalTv.setTextColor(color);

        goalTv.setText(goal.getName());

        return goalTv;
    }

    private class GetThangsTask extends AsyncTask<Void, Void, Boolean> {

        private CCRequestManager mManager = CCRequestManager.getInstance(GameLevelActivity.this);
        @NonNull
        private List<Thang> mThangs = new ArrayList<>();
        @NonNull
        private List<ThangType> mThangTypes = new ArrayList<>();

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // get level
                JSONObject levelObj = mManager.requestLevelDataSync(mLevelId);
                if (levelObj == null) {
                    return false;
                }

                // get goals
                JSONArray goalArr = levelObj.getJSONArray("goals");
                mGoals = CCDataUtil.parseGoalArr(goalArr);

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
