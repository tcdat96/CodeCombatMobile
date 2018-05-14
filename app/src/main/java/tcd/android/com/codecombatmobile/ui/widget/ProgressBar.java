package tcd.android.com.codecombatmobile.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import tcd.android.com.codecombatmobile.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ProgressBar extends FrameLayout {

    private static final String TAG = ProgressBar.class.getSimpleName();

    private Paint mPaint = new Paint();
    private float mProgressValue = 1f;
    @ColorInt
    private int mProgressColor;
    private int mSpace = 0;
    private Bitmap mThumbBitmap;

    // handle touch event
    private boolean mIsDragging = false;
    private int mTouchSlop = 10;

    public ProgressBar(@NonNull Context context) {
        super(context);
        init(null, 0);
    }

    public ProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ProgressBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    protected void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressBar, defStyleAttr, 0);
        mProgressColor = a.getColor(R.styleable.ProgressBar_progressColor, ContextCompat.getColor(getContext(), R.color.time_progress_color));
        boolean isThumbEnabled = a.getBoolean(R.styleable.ProgressBar_controlThumbEnabled, false);
        a.recycle();

        setBackgroundResource(R.drawable.background_capsule_black);
        mPaint.setColor(mProgressColor);
        if (isThumbEnabled) {
            mThumbBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.widget_control_thumb);
        }

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mThumbBitmap != null) {
            mThumbBitmap = Bitmap.createScaledBitmap(mThumbBitmap, h, h, false);
        }
        mSpace = getHeight() / 4;
    }

    public void setProgress(float progress) {
        if (progress < 0 || progress > 1) {
            return;
        }
        mProgressValue = progress;
        invalidate();
    }

    public void setProgressColor(@ColorInt int color) {
        mProgressColor = color;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int radius = (getHeight() - mSpace * 2) / 2;
        int center = mSpace + radius;
        int progressWidth = (int) ((getWidth() - mSpace) * mProgressValue) - radius;
        if (progressWidth < center) progressWidth = center;
        canvas.drawCircle(center, center, radius, mPaint);
        canvas.drawRect(new Rect(center, mSpace, progressWidth, center + radius), mPaint);
        canvas.drawCircle(progressWidth, center, radius, mPaint);

        if (mThumbBitmap != null) {
            canvas.drawBitmap(mThumbBitmap, progressWidth - mThumbBitmap.getWidth() / 2, 0, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mThumbBitmap == null) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setProgress(event.getX() / (getWidth() - mSpace / 2));
                mIsDragging = true;
            case MotionEvent.ACTION_MOVE:
                if (mIsDragging) {
                    setProgress(event.getX() / (getWidth() - mSpace / 2));
                }
                return mIsDragging;
            case MotionEvent.ACTION_UP:
                performClick();
            case MotionEvent.ACTION_CANCEL:
                mIsDragging = false;
                return true;
        }
        return super.onTouchEvent(event);
    }
}
