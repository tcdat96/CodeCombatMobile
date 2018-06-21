package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

import android.widget.TextView;

public class FlowControl extends CodeBlock {
    public FlowControl(String name) {
        super(name, TYPE_FLOW_CONTROL, 1);
    }

    @Override
    public int getReplacementIndex(Operation oldOp, Operation newOp) {
        int index = mChildren.indexOf(oldOp);
        // comparison
        if (index == 0) {
            return newOp.returnsValue() ? index : -1;
        }
        // others
        return newOp.isStatement() && !(newOp instanceof FuncDeclaration) ? index : -1;
    }

    @Override
    public void display(TextView container) {
        container.append(mSpannable);
        container.append(" ");
        mChildren.get(0).display(container);
        container.append(":");
        for (int i = 1; i < mChildren.size(); i++) {
            container.append("\n" + INDENT_VALUE);
            mChildren.get(i).display(container);
        }
    }
}
