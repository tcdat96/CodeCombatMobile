package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

import android.text.SpannableString;
import android.widget.TextView;

/**
 * Created by ADMIN on 23/04/2018.
 */

public class Operator extends Operation {

    public static String[] OPERATORS = new String[] {"+", "-", /*"*",*/ "/", "%", "^", "&", "|"};

    public Operator(String operator) {
        super(operator, TYPE_OPERATOR);
        mSpannable = new SpannableString(operator);

        mIsStatement = false;
    }

    @Override
    public void display(TextView container) {
        container.append(mSpannable);
    }
}
