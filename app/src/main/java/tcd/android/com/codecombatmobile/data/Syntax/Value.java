package tcd.android.com.codecombatmobile.data.syntax;

import android.text.SpannableString;
import android.widget.TextView;

/**
 * Created by ADMIN on 23/04/2018.
 */

public class Value extends Operation {

    public Value(String value) {
        super(value, TYPE_VALUE);
        mSpannable = new SpannableString(value);
    }

    @Override
    public void display(TextView container) {
        container.append(mSpannable);
    }
}
