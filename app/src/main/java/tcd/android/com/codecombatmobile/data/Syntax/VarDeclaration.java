package tcd.android.com.codecombatmobile.data.syntax;

import java.util.ArrayList;

/**
 * Created by ADMIN on 23/04/2018.
 */

public class VarDeclaration extends Operation {

    private static final String TAG = VarDeclaration.class.getSimpleName();

    public VarDeclaration() {
        super("var", TYPE_DECLARATION);
        mChildren = new ArrayList<>(3);
        mChildren.add(new Blank());
        mChildren.add(new Assignment("="));
        mChildren.add(new Blank());
    }

    @Override
    protected boolean isNewOpValid(int index, Operation op) {
        switch (index) {
            case 0: return op instanceof Value;             // TODO: 29/05/2018 must be a string
            case 1: return op instanceof Assignment;
            case 2: return op.returnsValue();
        }
        return super.isNewOpValid(index, op);
    }
}
