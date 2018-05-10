package tcd.android.com.codecombatmobile.data.Syntax;

import android.text.SpannableString;
import android.widget.TextView;

public abstract class CodeBlock extends Operation {

    private int mHeaderTotal;

    public CodeBlock(String name, @SyntaxType int syntaxType, int headerTotal) {
        super(name.toLowerCase(), syntaxType);

        mHeaderTotal = headerTotal;
        for (int i = 0; i < headerTotal + 1; i++) {
            mChildren.add(new Blank());
        }

        mSpannable = new SpannableString(mName);
        setSpannableColor();
    }

    @Override
    public boolean replaceOperation(Operation oldOp, Operation newOp) {
        int index = mChildren.indexOf(oldOp);
        if (index >= 0) {
            if (isNewOpValid(index, newOp)) {
                mChildren.set(index, newOp);
                if (!(mChildren.get(mChildren.size() - 1) instanceof Blank)) {
                    mChildren.add(new Blank());
                }
                return true;
            }
        }
        return false;
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
                }
            } else {
                mChildren.set(index, new Blank());
                removeAdjacentBlank(index);
            }
        }
    }

    protected void removeAdjacentBlank(int index) {
        // left item
        if (index - 1 >= mHeaderTotal && isItemBlank(index - 1)) {
            mChildren.remove(index - 1);
        }
        // right item
        if (isItemBlank(index + 1)) {
            mChildren.remove(index + 1);
        }
    }

    boolean isItemBlank(int position) {
        return position >= 0 && position < mChildren.size() && mChildren.get(position) instanceof Blank;
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
