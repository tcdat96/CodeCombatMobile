package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

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
}
