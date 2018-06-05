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
    public int getReplacementIndex(Operation oldOp, Operation newOp) {
        int index = mChildren.indexOf(oldOp);
        return newOp.returnsValue() ? index : -1;
    }

    @Override
    public void display(TextView container) {
        container.append(mSpannable);
        container.append(" ");
        mChildren.get(0).display(container);
    }
}
