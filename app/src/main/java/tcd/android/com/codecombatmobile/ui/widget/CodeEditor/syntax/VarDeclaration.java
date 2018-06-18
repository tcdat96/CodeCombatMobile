package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

import android.widget.TextView;

import java.util.ArrayList;

public class VarDeclaration extends Operation {

    public VarDeclaration() {
        super("var", TYPE_DECLARATION);

        mChildren = new ArrayList<>(2);
        mChildren.add(new Blank());
        mChildren.add(new Blank());
    }

    @Override
    public boolean replaceOperation(Operation oldOp, Operation newOp) {
        int index = getReplacementIndex(oldOp, newOp);
        if (index >= 0) {
            if (index == 1) {
                newOp = new Expression(newOp);
            }
            mChildren.set(index, newOp);
            return true;
        }
        return false;
    }

    @Override
    public int getReplacementIndex(Operation oldOp, Operation newOp) {
        int index = mChildren.indexOf(oldOp);
        switch (index) {
            case 0: return newOp instanceof UserInput ? index : -1;
            case 1: return newOp.returnsValue() ? index : -1;
        }
        return super.getReplacementIndex(oldOp, newOp);
    }

    @Override
    public void display(TextView container) {
        // variable
        mChildren.get(0).display(container);
        // assignment
        container.append(" = ");
        // value
        mChildren.get(1).display(container);
    }
}
