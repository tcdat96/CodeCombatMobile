package tcd.android.com.codecombatmobile.data.Syntax;

import android.text.SpannableString;
import android.widget.TextView;

/**
 * Created by ADMIN on 28/04/2018.
 */

public class Blank extends Operation {
    public Blank() {
        super("___", TYPE_VARIABLE);
        mSpannable = new SpannableString(mName);
        setSpannableColor();
    }

    @Override
    public void display(TextView container) {
        container.append(mSpannable);
    }
}
