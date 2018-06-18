package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

import android.text.SpannableString;
import android.widget.TextView;

/**
 * Created by ADMIN on 23/04/2018.
 */

public class Value extends Operation {

    public Value(String value) {
        super(value, TYPE_VALUE);
        mSpannable = new SpannableString(value);

        mReturnsValue = true;
        mIsStatement = false;
    }

    @Override
    public void display(TextView container) {
        container.append(mSpannable);
    }
}
