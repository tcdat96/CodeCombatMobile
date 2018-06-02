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
import tcd.android.com.codecombatmobile.data.course.Position;
import tcd.android.com.codecombatmobile.data.level.Thang;
import tcd.android.com.codecombatmobile.data.level.ThangType;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

public class GameLevelView extends SurfaceView implements Runnable {
    private static final String TAG = GameLevelView.class.getSimpleName();

    @Nullable
    private Thread mGameThread = null;
    private volatile boolean mIsRunning = false;

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Paint mPaint;
    private Paint mTextPaint, mTextBgrPaint;

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

        mTextPaint = new Paint();
        mTextPaint.setTextSize(30);
        mTextPaint.setColor(Color.WHITE);
        mTextBgrPaint = new Paint();
        mTextBgrPaint.setColor(Color.DKGRAY);

        mScreenSize = DisplayUtil.getScreenSize(context);

        // load character size
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sprite_sheet_bob);
        mBitmap = Bitmap.createScaledBitmap(mBitmap, mFrameWidth * mFrameTotal, mFrameHeight, false);
    }

    public void setThangs(@NonNull List<Thang> thangs, @NonNull List<ThangType> thangTypes) {
        mThangs = thangs;
        mThangTypes = thangTypes;

        int widthPixel = 1, heightPixel= 1;
        float bgrWidth = 1, bgrHeight = 1;
        // level background
        for (ThangType thangType : thangTypes) {
            if (thangType.getKind().equals("Floor")) {
                Bitmap originalBgr = thangType.getBitmap();
                if (originalBgr == null) {
                    return;
                }
                float scale = (float) originalBgr.getWidth() / originalBgr.getHeight();
                heightPixel = (int) (mScreenSize.y * 0.81f);
                widthPixel = (int) (heightPixel * scale);
                mLevelBackground = Bitmap.createScaledBitmap(originalBgr, widthPixel, heightPixel, false);

                mHolder.setFixedSize(widthPixel, heightPixel);

                // find background size
                for (Thang thang : mThangs) {
                    if (thang.getThangType().equals(thangType.getOriginal())) {
                        bgrHeight = thang.getHeight() != 0 ? thang.getHeight() : thangType.getHeight();
                        bgrWidth = thang.getWidth() != 0 ? thang.getWidth() : thangType.getWidth();
                        break;
                    }
                }

                break;
            }
        }

        // readjust thang info
        for (Thang thang : mThangs) {
            // position
            Position position = thang.getPosition();
            position.x = position.x / bgrWidth * widthPixel;
            position.y = (bgrHeight - position.y) / bgrHeight * heightPixel;

            // rotation (from radian to degree)
            float degree = (float) (thang.getRotation() / Math.PI * 180);
            // TODO: 31/05/2018 temporary workaround, remove when actually implement rendering
            if (thang.getId().startsWith("Spike Walls")) {
                degree += 90;
            }
            thang.setRotation(degree);
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

            // thangs name
            for (Thang thang : mThangs) {
                int x = (int) thang.getPosition().x;
                int y = (int) thang.getPosition().y;
                String name = getThangName(thang.getId());
                if (!name.equals("")) {
                    int width = (int) (mTextPaint.measureText(name) / 2);
                    int height = (int) (mTextPaint.getTextSize() / 2);
                    int margin = 5;

                    mCanvas.save();
                    mCanvas.rotate(-thang.getRotation(), x, y);
                    mCanvas.drawRect(x - width - margin, y - height - margin, x + width + margin, y + height + margin, mTextBgrPaint);
                    mCanvas.drawText(name, x - width, y + height, mTextPaint);
                    mCanvas.restore();
                }
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

    @NonNull
    private String getThangName(@NonNull String id) {
        if (id.endsWith("Background")) {
            return "";
        } else if (id.startsWith("Movement Stone")) {
            return id.substring(9);
        } else if (id.startsWith("Hero")) {
            return "Hero";
        }
        return id;
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
