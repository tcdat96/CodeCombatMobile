package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

import android.widget.TextView;

public class FuncDeclaration extends CodeBlock {

    private int mParamTotal = 1;

    public FuncDeclaration() {
        super("def", TYPE_DECLARATION, 2 /* blank name and parameter */);
    }

    @Override
    public int getReplacementIndex(Operation oldOp, Operation newOp) {
        int index = mChildren.indexOf(oldOp);
        if (index < mParamTotal + 1) {
            // TODO: 10/05/2018 must be string
            return newOp instanceof Value ? index : -1;
        }
        return index;
    }

    @Override
    protected void removeAdjacentBlank(int index) {
        // check parameters part
        boolean isLeftBlank = index - 1 >= 1 && index - 1 <= mParamTotal && isItemBlank(index - 1);
        if (isLeftBlank) {
            mChildren.remove(index - 1);
        }
        boolean isRightBlank = index + 1 <= mParamTotal && isItemBlank(index + 1);
        if (isRightBlank) {
            mChildren.remove(index + 1);
        }
        // check statements part
        super.removeAdjacentBlank(index);
    }

    @Override
    public void display(TextView container) {
        // def keyword
        container.append(mSpannable);
        container.append(" ");
        // function name
        mChildren.get(0).display(container);
        container.append("(");
        // parameters
        for (int i = 1; i <= mParamTotal; i++) {
            mChildren.get(i).display(container);
            if (i < mParamTotal) {
                container.append(", ");
            }
        }
        // end of header
        container.append("):");
        // statements
        int startIndex = 1 + mParamTotal;
        for (int i = startIndex; i < mChildren.size(); i++) {
            container.append("\n" + DEFAULT_INDENT);
            mChildren.get(i).display(container);
        }
    }
}
