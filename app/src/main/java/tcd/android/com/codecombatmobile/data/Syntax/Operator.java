package tcd.android.com.codecombatmobile.data.syntax;

import android.text.SpannableString;
import android.widget.TextView;

/**
 * Created by ADMIN on 23/04/2018.
 */

public class Operator extends Operation {

    public Operator(String operator) {
        super(operator, TYPE_OPERATOR);
        mSpannable = new SpannableString(operator);
    }

    @Override
    public void display(TextView container) {
        container.append(mSpannable);
    }
}
