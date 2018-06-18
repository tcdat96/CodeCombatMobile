package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

public class Method extends Function {
    public Method(String name, int paramTotal) {
        super(name, paramTotal);
        setSyntaxType(TYPE_METHOD);

        mReturnsValue = false;
        mIsStatement = false;
    }
}
