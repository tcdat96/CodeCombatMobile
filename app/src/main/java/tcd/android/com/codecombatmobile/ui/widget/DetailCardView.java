package tcd.android.com.codecombatmobile.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import tcd.android.com.codecombatmobile.R;

/**
 * Created by ADMIN on 01/05/2018.
 */

public class DetailCardView extends CardView {

    private ImageView mIconImageView;
    private TextView mTitleTextView, mValueTextView;

    public DetailCardView(@NonNull Context context) {
        super(context);
        init(null, 0);
    }

    public DetailCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DetailCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(@Nullable AttributeSet attrs, int defStyleAttr) {
        inflate(getContext(), R.layout.widget_detail_card_view, this);
        mIconImageView = findViewById(R.id.iv_card_icon);
        mTitleTextView = findViewById(R.id.tv_card_title);
        mValueTextView = findViewById(R.id.tv_card_value);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DetailCardView, defStyleAttr, 0);
        Drawable drawable = a.getDrawable(R.styleable.DetailCardView_iconSrc);
        mIconImageView.setImageDrawable(drawable);
        String title = a.getString(R.styleable.DetailCardView_title);
        mTitleTextView.setText(title);
        a.recycle();
    }

    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size, size);
    }

    public void setIcon(@DrawableRes int resId) {
        Glide.with(getContext()).load(resId).into(mIconImageView);
    }

    public void setTitle(String title) {
        mTitleTextView.setText(title);
    }

    public void setValue(String value) {
        mValueTextView.setText(value);
    }
}
