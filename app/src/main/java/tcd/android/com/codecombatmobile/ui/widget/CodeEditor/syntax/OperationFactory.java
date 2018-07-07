package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                Object object = new Object(name);
                if (type.getData() != null) {
                    List<String> methods = new ArrayList<>();
                    for (java.lang.Object method : type.getData()) {
                        methods.add((String) method);
                    }
                    object.addMethods(methods);
                }
                return object;
            case Operation.TYPE_FUNCTION:
            case Operation.TYPE_METHOD:
                int paramTotal = (int) (type.getData().get(0));
                return type.getSyntaxType() == Operation.TYPE_FUNCTION ?
                        new Function(name, paramTotal) :
                        new Method(name, paramTotal);
            case Operation.TYPE_VALUE:
                return name.equals(Operation.BLANK_VALUE) ? new UserInput() : new Value(name);
            case Operation.TYPE_ASSIGNMENT:
                return new Assignment(name);
            case Operation.TYPE_OPERATOR:
                return new Operator(name);
            case Operation.TYPE_COMMENT:
                return new Comment();
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
        private List<java.lang.Object> mData;
        private boolean mIsBuiltIn = true;

        public OperationType(@Operation.SyntaxType int syntaxType, String name) {
            mSyntaxType = syntaxType;
            mName = name;
        }

        public OperationType(@Operation.SyntaxType int syntaxType, String name, java.lang.Object data) {
            this(syntaxType, name);
            setData(new ArrayList<>(Collections.singletonList(data)));
        }

        public void setData(@NonNull List<java.lang.Object> data) {
            mData = data;
        }

        public void addData(@NonNull java.lang.Object data) {
            mData.add(data);
        }

        public int getSyntaxType() {
            return mSyntaxType;
        }

        public String getName() {
            return mName;
        }

        public List<java.lang.Object> getData() {
            return mData != null ? new ArrayList<>(mData) : new ArrayList<>();
        }

        public boolean isBuiltIn() {
            return mIsBuiltIn;
        }

        public void setBuiltIn(boolean isBuiltIn) {
            mIsBuiltIn = isBuiltIn;
        }
    }
}
