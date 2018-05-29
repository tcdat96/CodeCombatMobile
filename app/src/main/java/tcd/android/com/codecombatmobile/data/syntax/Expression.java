package tcd.android.com.codecombatmobile.data.syntax;

import android.widget.TextView;

/**
 * Created by ADMIN on 23/04/2018.
 */

public class Expression extends Operation {

    public Expression(Operation operation) {
        // TODO: 28/05/2018 why value type?
        super("Expression", TYPE_VALUE);
        mChildren.add(operation);
    }

    public void add(int index, Operator operator) {
        mChildren.add(index, operator);
        mChildren.add(index + 1, new Blank());
    }

    @Override
    public boolean replaceOperation(Operation oldOp, Operation newOp) {
        int index = mChildren.indexOf(oldOp);
        if (index >= 0) {
            if (newOp instanceof Operator && !(oldOp instanceof Operator)) {
                mChildren.set(index, oldOp);
                add(index + 1, (Operator) newOp);
                return true;
            }
            if (isNewOpValid(index, newOp)) {
                mChildren.set(index, newOp);
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeOperation(Operation op) {
        int index = mChildren.indexOf(op);
        if (index >= 0) {
            Operation curOp = mChildren.get(index);
            if (curOp instanceof Operator) {
                return;
            }
            if (curOp instanceof Blank) {
                if (index > 0) {
                    mChildren.remove(index);
                    mChildren.remove(index - 1);
                } else {
                    // TODO: 29/05/2018 remove container
                }
            } else {
                mChildren.set(index, new Blank());
            }
        }
    }

    @Override
    protected boolean isNewOpValid(int index, Operation op) {
        if (mChildren.get(index) instanceof Operator) {
            return op instanceof Operator;
        } else {
            return op.returnsValue();
        }
    }

    @Override
    public void display(TextView container) {
        for (Operation operation : mChildren) {
            operation.display(container);
            container.append(" ");
        }
    }
}
