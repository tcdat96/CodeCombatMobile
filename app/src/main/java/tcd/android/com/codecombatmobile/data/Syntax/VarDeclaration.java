package tcd.android.com.codecombatmobile.data.syntax;

import java.util.ArrayList;

/**
 * Created by ADMIN on 23/04/2018.
 */

public class VarDeclaration extends Operation {

    public VarDeclaration() {
        super("var", TYPE_DECLARATION);
        mChildren = new ArrayList<>(3);
        mChildren.add(new Blank());
        mChildren.add(new Assignment("="));
        mChildren.add(new Blank());
    }

    @Override
    public int getReplacementIndex(Operation oldOp, Operation newOp) {
        boolean replaceable;
        int index = mChildren.indexOf(oldOp);
        switch (index) {
            case 0: replaceable = newOp instanceof Value; break;             // TODO: 29/05/2018 must be a string
            case 1: replaceable = newOp instanceof Assignment; break;
            case 2: replaceable = newOp.returnsValue(); break;
            default:
                return super.getReplacementIndex(oldOp, newOp);
        }
        return replaceable ? index : -1;
    }
}
