package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by ADMIN on 25/04/2018.
 */

public class OperationFactory {

    @Nullable
    public Operation getOperation(OperationType type) {
        String name = type.getName();
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        switch (type.getSyntaxType()) {
            case Operation.TYPE_FLOW_CONTROL:
                switch (name) {
                    case "if":
                        return new FlowControl("If");
                    case "for":
                        return new FlowControl("For");
                    case "return":
                        return new Return();
                    default:
                        throw new IllegalArgumentException("Unknown syntax name");
                }
            case Operation.TYPE_DECLARATION:
                switch (name) {
                    case "var":
                        return new VarDeclaration();
                    case "func":
                        return new FuncDeclaration();
                }
            case Operation.TYPE_VARIABLE:
                return new Object(name);
            case Operation.TYPE_FUNCTION:
            case Operation.TYPE_METHOD:
                int paramTotal = (int) type.getData()[0];
                return type.getSyntaxType() == Operation.TYPE_FUNCTION ?
                        new Function(name, paramTotal) :
                        new Method(name, paramTotal);
            case Operation.TYPE_VALUE:
                return name.equals(Operation.BLANK_VALUE) ? new UserInput() : new Value(name);
            case Operation.TYPE_ASSIGNMENT:
                return new Assignment(name);
            case Operation.TYPE_OPERATOR:
                return new Operator(name);
            case Operation.TYPE_BLANK:
                throw new IllegalArgumentException("Blank type should not be used here");
            default:
                throw new IllegalArgumentException("Unknown syntax type");
        }
    }

    public static class OperationType {
        @Operation.SyntaxType
        private int mSyntaxType;
        private String mName;
        private java.lang.Object[] mData;
        private boolean mIsBuiltIn = true;

        public OperationType(@Operation.SyntaxType int syntaxType, String name) {
            mSyntaxType = syntaxType;
            mName = name;
        }

        public OperationType(@Operation.SyntaxType int syntaxType, String name, @NonNull java.lang.Object[] data) {
            this(syntaxType, name);
            mData = data;
        }

        public int getSyntaxType() {
            return mSyntaxType;
        }

        public String getName() {
            return mName;
        }

        java.lang.Object[] getData() {
            return mData;
        }

        public boolean isBuiltIn() {
            return mIsBuiltIn;
        }

        public void setBuiltIn(boolean isBuiltIn) {
            mIsBuiltIn = isBuiltIn;
        }
    }
}
