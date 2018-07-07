package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ADMIN on 23/04/2018.
 */

public class Expression extends Operation {

    public Expression(Operation operation) {
        // TODO: 28/05/2018 why value type?
        super("Expression", TYPE_VALUE);
        mChildren.add(operation);
    }

    public Expression(List<Operation> operands) {
        super("Expression", TYPE_VALUE);
        mChildren.addAll(operands);
    }

    private void add(int index, Operator operator) {
        mChildren.add(index, operator);
        mChildren.add(index + 1, new Blank());
    }

    @Override
    public boolean replaceOperation(Operation oldOp, Operation newOp) {
        int index = getReplacementIndex(oldOp, newOp);
        if (index >= 0) {
            if (newOp instanceof Operator && oldOp.returnsValue()) {
                mChildren.set(index, oldOp);
                add(index + 1, (Operator) newOp);
                return true;
            } else {
                mChildren.set(index, newOp);
                return true;
            }
        }
        return false;
    }

    @Override
    public int getReplacementIndex(Operation oldOp, Operation newOp) {
        int index = mChildren.indexOf(oldOp);
        if (newOp instanceof Operator) {
            return oldOp.returnsValue() || oldOp instanceof Operator ? index : -1;
        } else {
            return !(oldOp instanceof Operator) && newOp.returnsValue() ? index : -1;
        }
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
                    removeFromContainer();
                }
            } else {
                mChildren.set(index, new Blank());
            }
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
