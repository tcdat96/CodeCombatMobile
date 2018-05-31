package tcd.android.com.codecombatmobile.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.course.Level;
import tcd.android.com.codecombatmobile.data.course.Position;
import tcd.android.com.codecombatmobile.ui.GameLevelActivity;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

public class GameMapView extends SurfaceView implements Runnable {
    private static final String TAG = GameMapView.class.getSimpleName();
    private static final int LEVEL_BANNER_WIDTH = 50;
    private static final long VALID_CLICK_TIME_DIFFERENCE = TimeUnit.SECONDS.toMillis(2);

    @Nullable
    private Thread mGameThread = null;
    private volatile boolean mIsRunning = false;

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Paint mPaint, mIncompleteLevelPaint, mCompleteLevelPaint;

    private Point mScreenSize;
    private Bitmap mMapBackground;

    @NonNull
    private List<Level> mLevels = new ArrayList<>();
    @NonNull
    private List<RectF> mLevelsPosition = new ArrayList<>();
    private Bitmap mLevelBannerBitmap;
    private Position mCurLevelPos = null;

    private SoundPool mSoundPool;

    public GameMapView(Context context) {
        super(context);
        init(context);
    }

    public GameMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        mHolder = getHolder();
        mScreenSize = DisplayUtil.getScreenSize(context);

        // paint
        mPaint = new Paint();
        mIncompleteLevelPaint = new Paint();
        mIncompleteLevelPaint.setColor(ContextCompat.getColor(context, R.color.incomplete_level_color));
        mCompleteLevelPaint = new Paint();
        mCompleteLevelPaint.setColor(ContextCompat.getColor(context, R.color.complete_level_color));

        // current level banner
        mLevelBannerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.level_banner_started);
        float ratio = (float) mLevelBannerBitmap.getWidth() / mLevelBannerBitmap.getHeight();
        mLevelBannerBitmap = Bitmap.createScaledBitmap(mLevelBannerBitmap, LEVEL_BANNER_WIDTH, (int) (LEVEL_BANNER_WIDTH / ratio), false);

        // init sound pool
        initSoundPool();

        // play ambient dungeon sound
        playSound(R.raw.sound_ambient_dungeon);
    }

    private void initSoundPool() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(attrs)
                    .build();
        } else {
            mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }
    }

    private void playSound(@RawRes final int rawResId) {
        final int soundId = mSoundPool.load(getContext(), rawResId, 1);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (sampleId == soundId) {
                    mSoundPool.play(soundId, 1, 1, 0, -1 /* loop forever */, 1);
                }
            }
        });
    }

    public void setMapBackground(Bitmap mapBgr) {
        float scale = (float) mapBgr.getWidth() / mapBgr.getHeight();
        int height = mScreenSize.y;
        int width = (int) (height * scale);
//        int width = mScreenSize.x;
//        int height = mScreenSize.y;
        mMapBackground = Bitmap.createScaledBitmap(mapBgr, width, height, false);

        updateLevelsPosition();
        mHolder.setFixedSize(width, height);
    }

    public void setLevels(@NonNull List<Level> levels) {
        mLevels = levels;
        updateLevelsPosition();
    }

    private void updateLevelsPosition() {
        if (mMapBackground != null && mLevels.size() > 0) {
            int left = getLeft();
            int height = mMapBackground.getHeight();
            float scaleX = mMapBackground.getWidth() / 100f;
            float scaleY = height / 100f;
            for (int i = 0; i < mLevels.size(); i++) {
                Position position = mLevels.get(i).getPosition();
                position.x *= scaleX + left;
                position.y = height - position.y * scaleY;

                float semiMajor = 12f;
                float semiMinor = 10f;
                RectF rectF = new RectF(position.x - semiMajor, position.y - semiMinor,
                        position.x + semiMajor, position.y + semiMinor);
                mLevelsPosition.add(rectF);
            }
        }
    }

    public void setLevelSessions(List<String> sessions) {
        int lastIdx = sessions.size() - 1;
        for (int i = 0; i < lastIdx; i++) {
            mLevels.get(i).setComplete(true);
        }

        // the last item is the current level
        Level lastLevel = mLevels.get(lastIdx);
        lastLevel.setComplete(false);
        mCurLevelPos = new Position(lastLevel.getPosition());
        mCurLevelPos.x -= mLevelBannerBitmap.getWidth() / 2;
        mCurLevelPos.y -= mLevelBannerBitmap.getHeight();
    }

    @Override
    public void run() {
        while (mIsRunning) {
            draw();
        }
    }

    private void draw() {
        if (mHolder.getSurface().isValid()) {
            mCanvas = mHolder.lockCanvas();

            // background
            if (mMapBackground != null) {
                mCanvas.drawBitmap(mMapBackground, 0, 0, mPaint);
            }

            // levels
            for (int i = 0; i < mLevels.size(); i++) {
                mCanvas.drawOval(mLevelsPosition.get(i), mLevels.get(i).isComplete() ? mCompleteLevelPaint : mIncompleteLevelPaint);
            }

            // mark current level
            if (mCurLevelPos != null) {
                mCanvas.drawBitmap(mLevelBannerBitmap, mCurLevelPos.x, mCurLevelPos.y, mPaint);
            }

            mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private boolean isPressed = false;
    private long mLastClickTime = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isPressed = true;
                return true;
            case MotionEvent.ACTION_UP:
                long difference = System.currentTimeMillis() - mLastClickTime;
                if (isPressed && difference > VALID_CLICK_TIME_DIFFERENCE) {
                    mLastClickTime = System.currentTimeMillis();
                    float x = event.getX();
                    float y = event.getRawY();
                    Log.d(TAG, "onTouchEvent: ");
                    // TODO: 27/05/2018 fix the coordinate precision issue
                    for (int i = 0; i < mLevelsPosition.size(); i++) {
                        if (mLevelsPosition.get(i).contains(x, y)) {
                            Log.d(TAG, "onTouchEvent: " + i);
                            showLevelInfoDialog(i);
                            break;
                        }
                    }
                }
                isPressed = false;
                return false;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void showLevelInfoDialog(int position) {
        final Context context = getContext();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_level_info);

        final Level level = mLevels.get(position);
        ((TextView)dialog.findViewById(R.id.tv_level_name)).setText(level.getName());
        ((TextView)dialog.findViewById(R.id.tv_level_description)).setText(level.getDescription());
        dialog.findViewById(R.id.btn_play).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(context, GameLevelActivity.class);
                intent.putExtra(GameLevelActivity.ARG_LEVEL_DATA, level);
                context.startActivity(intent);
            }
        });

        dialog.show();
    }

    public void pause() {
        mIsRunning = false;
        mSoundPool.autoPause();
        try {
            if (mGameThread != null) {
                mGameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        mIsRunning = true;
        mSoundPool.autoResume();
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    public void destroy() {
        mSoundPool.release();
    }
}
