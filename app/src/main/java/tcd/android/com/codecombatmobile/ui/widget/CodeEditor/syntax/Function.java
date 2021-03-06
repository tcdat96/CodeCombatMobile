package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

import android.text.SpannableString;
import android.widget.TextView;

import java.lang.Object;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ADMIN on 26/04/2018.
 */

public class Function extends Operation {
    public Function(String name, int paramTotal) {
        super(name, TYPE_FUNCTION);

        mReturnsValue = true;
        mSpannable = new SpannableString(name.substring(0, name.length() - 2));

        for (int i = 0; i < paramTotal; i++) {
            mChildren.add(new Blank());
        }
    }

    public Function(String name, Operation[] parameters) {
        this(name, parameters.length);

        mChildren.clear();
        mChildren.addAll(Arrays.asList(parameters));
    }

    public int getParamTotal() {
        return mChildren.size();
    }

    @Override
    public int getReplacementIndex(Operation oldOp, Operation newOp) {
        int index = mChildren.indexOf(oldOp);
        return newOp.returnsValue() ? index : -1;
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
