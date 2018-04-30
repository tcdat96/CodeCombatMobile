package tcd.android.com.codecombatmobile.data.Syntax;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.widget.TextView;

/**
 * Created by ADMIN on 23/04/2018.
 */

public class Variable extends Operation {

    public Variable(@NonNull String name) {
        super(name, TYPE_VARIABLE);
        mSpannable = new SpannableString(name);
        setSpannableColor();
    }

    @Override
    public void display(TextView container) {
        container.append(mSpannable);
    }
}
