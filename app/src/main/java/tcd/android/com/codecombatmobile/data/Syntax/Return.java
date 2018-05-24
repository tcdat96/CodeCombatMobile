package tcd.android.com.codecombatmobile.data.syntax;

import android.text.SpannableString;
import android.widget.TextView;

/**
 * Created by ADMIN on 28/04/2018.
 */

public class Return extends Operation {

    public Return() {
        super("return", TYPE_FLOW_CONTROL);
        mSpannable = new SpannableString("return");
        mChildren.add(new Blank());
    }

    @Override
    protected boolean isNewOpValid(int index, Operation op) {
        return op instanceof Variable || op instanceof Value || op instanceof Function;
    }

    @Override
    public void display(TextView container) {
        container.append(mSpannable);
        container.append(" ");
        mChildren.get(0).display(container);
    }
}
