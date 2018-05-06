package tcd.android.com.codecombatmobile.data.Syntax;

import android.text.SpannableString;
import android.widget.TextView;

public class CodeBlock extends Operation {

    public CodeBlock(String name) {
        super(name.toLowerCase(), TYPE_CONDITION);
        mChildren.add(new Blank());
        mChildren.add(new Blank());

        mSpannable = new SpannableString(mName);
        setSpannableColor();
    }

    @Override
    public boolean replaceOperation(Operation oldOp, Operation newOp) {
        int index = mChildren.indexOf(oldOp);
        if (index >= 0) {
            if (isNewOpValid(index, newOp)) {
                mChildren.set(index, newOp);
            }

            if (!(mChildren.get(mChildren.size() - 1) instanceof Blank)) {
                mChildren.add(new Blank());
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean isNewOpValid(int index, Operation op) {
        if (index == 1) {
            return op instanceof Value || op instanceof Function || op instanceof Variable;
        }
        return true;
    }

    @Override
    public void removeOperation(Operation op) {
        int index = mChildren.indexOf(op);
        if (index >= 0) {
            if (op instanceof Blank) {
                if (index == 0) {
                    // TODO: 06/05/2018 remove container
                } else if (index < mChildren.size() - 1) {
                    mChildren.remove(index);
                } else {
                    mChildren.set(index, new Blank());
                }
            } else {
                mChildren.set(index, new Blank());
            }
        }
    }

    @Override
    public void display(TextView container) {
        container.append(mSpannable);
        container.append(" ");
        mChildren.get(0).display(container);
        container.append(":");
        for (int i = 1; i < mChildren.size(); i++) {
            container.append("\n" + DEFAULT_INDENT);
            mChildren.get(i).display(container);
        }
    }
}
