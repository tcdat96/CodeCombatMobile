package tcd.android.com.codecombatmobile.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import tcd.android.com.codecombatmobile.R;

/**
 * Created by ADMIN on 02/05/2018.
 */

public class RatioImageView extends android.support.v7.widget.AppCompatImageView {

    private float mRatio = 4f / 3f;

    public RatioImageView(Context context) {
        super(context);
        init(null, 0);
    }

    public RatioImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RatioImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RatioImageView, defStyleAttr, 0);
        String ratio = a.getString(R.styleable.RatioImageView_ratio);
        a.recycle();

        if (ratio != null) {
            int colonIndex = ratio.indexOf(":");
            if (colonIndex < 1) {
                throw new IllegalArgumentException("Incorrect ratio format");
            }
            mRatio = Float.valueOf(ratio.substring(colonIndex + 1)) / Float.valueOf(ratio.substring(0, colonIndex));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = Math.round(width * mRatio);
        setMeasuredDimension(width, height);
    }
}
