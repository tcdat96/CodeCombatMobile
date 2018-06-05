package tcd.android.com.codecombatmobile.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import info.hoang8f.widget.FButton;
import tcd.android.com.codecombatmobile.data.syntax.Operation;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by ADMIN on 21/04/2018.
 */

public class SyntaxButton extends FButton {

    @Nullable
    private Operation mOperation;
    private boolean mEnabled = true;

    public SyntaxButton(Context context) {
        super(context);
        init(null, 0);
    }

    public SyntaxButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SyntaxButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        setDefaultAttributes();
    }

    private void setDefaultAttributes() {
        int px_8dp = DisplayUtil.dpToPx(getContext(), 8);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.setMargins(0, 0, 0, px_8dp);

        setLayoutParams(params);
        setButtonColor(Color.WHITE);
        setShadowColor(Color.CYAN);
        setAllCaps(false);
        setFButtonPadding(px_8dp, px_8dp, px_8dp, px_8dp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStateListAnimator(null);
        }
    }

    public void setOperation(@NonNull Operation operation) {
        mOperation = operation;
        setButtonColorInternal();
    }

    private void setButtonColorInternal() {
        int color = mEnabled && mOperation != null ? mOperation.getColor() : Color.GRAY;
        setTextColor(color);
        setShadowColor(color);
    }

    @Nullable
    public Operation getOperation() { return mOperation; }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (mEnabled != enabled) {
            mEnabled = enabled;
            setButtonColorInternal();
        }
    }

    @Override
    public void setOnClickListener(@Nullable final OnClickListener listener) {
        OnClickListener modifiedListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEnabled) {
                    if (listener != null) {
                        listener.onClick(v);
                    }
                }
            }
        };
        super.setOnClickListener(modifiedListener);
    }
}
