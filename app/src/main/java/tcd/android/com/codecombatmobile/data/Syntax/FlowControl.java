package tcd.android.com.codecombatmobile.data.Syntax;

public class FlowControl extends CodeBlock {
    public FlowControl(String name) {
        super(name, TYPE_FLOW_CONTROL, 1);
    }

    @Override
    protected boolean isNewOpValid(int index, Operation op) {
        if (index == 0) {
            return op instanceof Value || op instanceof Function || op instanceof Variable;
        }
        return true;
    }
}
