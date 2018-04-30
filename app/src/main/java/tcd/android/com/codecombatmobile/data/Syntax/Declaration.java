package tcd.android.com.codecombatmobile.data.Syntax;

import java.util.ArrayList;

/**
 * Created by ADMIN on 23/04/2018.
 */

public class Declaration extends Operation {

    private static final String TAG = Declaration.class.getSimpleName();

    public Declaration() {
        super("var", TYPE_DECLARATION);
        mChildren = new ArrayList<>(3);
        mChildren.add(new Blank());
        mChildren.add(new Assignment("="));
        mChildren.add(new Blank());
    }

    @Override
    protected boolean isNewOpValid(int index, Operation op) {
        switch (index) {
            case 0: return op instanceof Variable;
            case 1: return op instanceof Assignment;
            case 2: return op instanceof SimpleExpression;
        }
        return super.isNewOpValid(index, op);
    }
}
