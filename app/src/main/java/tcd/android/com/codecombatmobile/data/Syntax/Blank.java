package tcd.android.com.codecombatmobile.data.syntax;

import android.text.SpannableString;
import android.widget.TextView;

/**
 * Created by ADMIN on 28/04/2018.
 */

public class Blank extends Operation {
    public Blank() {
        super("___", TYPE_VARIABLE);
        mSpannable = new SpannableString(mName);
    }

    @Override
    public void display(TextView container) {
        container.append(mSpannable);
    }
}
