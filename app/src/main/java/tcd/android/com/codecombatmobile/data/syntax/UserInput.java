package tcd.android.com.codecombatmobile.data.syntax;

import android.text.SpannableString;
import android.widget.TextView;

public class UserInput extends Operation {

    private int mTop, mLeft;

    public UserInput(String name) {
        super(name, TYPE_VALUE);
        mSpannable = new SpannableString(name);
        mReturnsValue = true;
    }

    public void setName(String name) {
        mName = name;
        mSpannable = new SpannableString(name);
        setSpannableColor();
    }

    public int getTop() {
        return mTop;
    }

    public int getLeft() {
        return mLeft;
    }

    @Override
    public void display(TextView container) {
        if (mTop == 0) {
            mTop = container.getTop();
            mLeft = container.getLeft() + (int)container.getPaint().measureText(container.getText().toString());
        }

        container.append(mSpannable);
    }
}
