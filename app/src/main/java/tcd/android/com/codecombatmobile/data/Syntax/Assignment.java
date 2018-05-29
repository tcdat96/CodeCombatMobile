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
    protected boolean isNewOpValid(int index, Operation op) {
        switch (index) {
            case 0: return op instanceof Variable;
            case 1: return op.returnsValue();
        }
        return super.isNewOpValid(index, op);
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
