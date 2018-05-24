package tcd.android.com.codecombatmobile.data.syntax;

import android.text.SpannableString;
import android.widget.TextView;

/**
 * Created by ADMIN on 26/04/2018.
 */

public class Function extends Operation {
    public Function(String name, int paramTotal) {
        super(name, TYPE_FUNCTION);

        mSpannable = new SpannableString(name.substring(0, name.length() - 2));
        setSpannableColor();

        for (int i = 0; i < paramTotal; i++) {
            mChildren.add(new Blank());
        }
    }

    @Override
    protected boolean isNewOpValid(int index, Operation op) {
        return op instanceof Variable || op instanceof Value || op instanceof Function;
    }

    @Override
    public void display(TextView container) {
        container.append(mSpannable);
        container.append("(");
        for (int i = 0; i < mChildren.size(); i++) {
            mChildren.get(i).display(container);
            if (i < mChildren.size() - 1) {
                container.append(", ");
            }
        }
        container.append(")");
    }
}
