package tcd.android.com.codecombatmobile.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.thang.Thang;
import tcd.android.com.codecombatmobile.data.thang.ThangType;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

public class GameLevelView extends SurfaceView implements Runnable {
    private static final String TAG = GameLevelView.class.getSimpleName();

    @Nullable
    private Thread mGameThread = null;
    private volatile boolean mIsRunning = false;

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Paint mPaint;

    private int mFps = 60;
    private long mTimeThisFrame;

    private Point mScreenSize;
    private Bitmap mLevelBackground;
    private Bitmap mBitmap;
    private boolean mIsMoving = true;             // bitmap not moving
    private float mSpeedPerSec = 250;
    private float mPositionX = 10;

    // frame info
    private int mFrameTotal = 5;
    private int mCurFrame = 0;
    private long mLastFrameChangeTime = 0;
    private int mFrameAnimMillis = 100;

    private int mFrameWidth = 300, mFrameHeight = 150;
    private Rect mFrameToDraw = new Rect(0, 0, mFrameWidth, mFrameHeight);
    private RectF mWhereToDraw = new RectF(mPositionX, 0, mPositionX + mFrameWidth, mFrameHeight);

    @NonNull
    private List<Thang> mThangs = new ArrayList<>();
    @NonNull
    private List<ThangType> mThangTypes = new ArrayList<>();

    public GameLevelView(Context context) {
        super(context);
        init(context);
    }

    public GameLevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameLevelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mHolder = getHolder();
        mPaint = new Paint();

        mScreenSize = DisplayUtil.getScreenSize(context);

        // load character size
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sprite_sheet_bob);
        mBitmap = Bitmap.createScaledBitmap(mBitmap, mFrameWidth * mFrameTotal, mFrameHeight, false);
    }

    public void setThangs(@NonNull List<Thang> thangs, @NonNull List<ThangType> thangTypes) {
        mThangs = thangs;
        mThangTypes = thangTypes;

        // level background
        for (ThangType thangType : thangTypes) {
            if (thangType.getKind().equals("Floor")) {
                mLevelBackground = thangType.getBitmap();
                float scale = (float) mLevelBackground.getWidth() / mLevelBackground.getHeight();
                int height = (int) (mScreenSize.y * 0.81f);
                int width = (int) (height * scale);
                mLevelBackground = Bitmap.createScaledBitmap(mLevelBackground, width, height, false);

                mHolder.setFixedSize(width, height);
            }
        }
    }

    @Override
    public void run() {
        while (mIsRunning) {
            long startFrameTime = System.currentTimeMillis();

            update();
            draw();

            mTimeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (mTimeThisFrame >= 1) {
                mFps = (int) (1000 / mTimeThisFrame);
            }
        }
    }

    private void update() {
        if (mIsMoving) {
            mPositionX += mSpeedPerSec / mFps;
//            mPositionX += 1;
            if (mPositionX > 900) {
                mPositionX = 0;
            }
        }
    }

    private void draw() {
        if (mHolder.getSurface().isValid()) {
            mCanvas = mHolder.lockCanvas();

            // background
            mCanvas.drawColor(Color.parseColor("#211C10"));
            if (mLevelBackground != null) {
                mCanvas.drawBitmap(mLevelBackground, 0, 0, mPaint);
            }

            // fps text
            mPaint.setColor(Color.argb(255,  249, 129, 0));
            mPaint.setTextSize(45);
            mCanvas.drawText("FPS:" + mFps, 500, 40, mPaint);

            // sprite_sheet_bob
            mWhereToDraw.set(mPositionX, 0, mPositionX + mFrameWidth, mFrameHeight);
            getCurrentFrame();
            mCanvas.drawBitmap(mBitmap, mFrameToDraw, mWhereToDraw, mPaint);

            mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    public void getCurrentFrame() {
        long curTime = System.currentTimeMillis();
        if (mIsMoving) {
            if (curTime > mLastFrameChangeTime + mFrameAnimMillis) {
                mLastFrameChangeTime = curTime;
                mCurFrame = ++mCurFrame < mFrameTotal ? mCurFrame : 0;
            }
        }
        mFrameToDraw.left = mCurFrame * mFrameWidth;
        mFrameToDraw.right = mFrameToDraw.left + mFrameWidth;
    }

    public void pause() {
        mIsRunning = false;
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
        mGameThread = new Thread(this);
        mGameThread.start();
    }
}
