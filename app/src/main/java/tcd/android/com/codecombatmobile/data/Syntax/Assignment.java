package tcd.android.com.codecombatmobile.data.syntax;

import android.text.SpannableString;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ADMIN on 23/04/2018.
 */

public class Assignment extends Operation {

    public Assignment(String assignment) {
        super(assignment, TYPE_ASSIGNMENT);
        mSpannable = new SpannableString(assignment);

        mChildren = new ArrayList<>(2);
        mChildren.add(new Blank());
        mChildren.add(new Blank());
    }

    public void modifyAssignment(String assignment) {
        mName = assignment;
        mSpannable = new SpannableString(assignment);
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
            case 0: return newOp instanceof Variable ? index : -1;
            case 1: return newOp.returnsValue() ? index : -1;
        }
        return super.getReplacementIndex(oldOp, newOp);
    }

    @Override
    public void display(TextView container) {
        // variable
        mChildren.get(0).display(container);
        // assignment
        container.append(" ");
        container.append(mSpannable);
        container.append(" ");
        // value
        mChildren.get(1).display(container);
    }
}
