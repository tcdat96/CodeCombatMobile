package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

public class Method extends Function {
    public Method(String name, int paramTotal) {
        super(name, paramTotal);
        init();
    }

    public Method(String name, Operation[] parameters) {
        super(name, parameters);
        init();
    }

    private void init() {
        setSyntaxType(TYPE_METHOD);

        mReturnsValue = false;
        mIsStatement = false;
    }
}
