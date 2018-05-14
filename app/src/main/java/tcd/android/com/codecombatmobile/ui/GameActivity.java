package tcd.android.com.codecombatmobile.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.ui.widget.GameView;
import tcd.android.com.codecombatmobile.ui.widget.ProgressBar;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    private GameView mGameView;
    private ProgressBar mTimeControlBar, mHpBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initUiComponents();
    }

    private void initUiComponents() {
        mGameView = findViewById(R.id.game_view);

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
            button.setOnClickListener(GameActivity.this);
            button.setBackgroundDrawable(drawable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameView.resume();
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
}
