package tcd.android.com.codecombatmobile.data.syntax;

import android.text.SpannableString;
import android.widget.TextView;

/**
 * Created by ADMIN on 23/04/2018.
 */

public class Assignment extends Operation {

    public Assignment(String assignment) {
        super(assignment, TYPE_OPERATOR);
        mSpannable = new SpannableString(assignment);
    }

    @Override
    public void display(TextView container) {
        container.append(" " + mSpannable + " ");
    }
}
