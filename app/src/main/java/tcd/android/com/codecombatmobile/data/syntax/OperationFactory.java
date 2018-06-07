package tcd.android.com.codecombatmobile.data.syntax;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

/**
 * Created by ADMIN on 25/04/2018.
 */

public class OperationFactory {

    @Nullable
    public Operation getOperation(Pair<Integer, String> opInfo) {
        return getOperation(opInfo.first, opInfo.second);
    }

    @Nullable
    public Operation getOperation(@Operation.SyntaxType int type, @Nullable String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        name = name.toLowerCase();
        switch (type) {
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
                    case "func":
                        return new FuncDeclaration();
                }
            case Operation.TYPE_VARIABLE:
                return new Variable(name);
            case Operation.TYPE_FUNCTION:
                int lastUdsIdx = name.lastIndexOf("_");
                String funcName = name.substring(0, lastUdsIdx);
                int paramTotal = Integer.valueOf(name.substring(lastUdsIdx + 1));
                return new Function(funcName, paramTotal);
            case Operation.TYPE_METHOD:
                break;
            case Operation.TYPE_VALUE:
                return name.equals("___") ? new UserInput(name) : new Value(name);
            case Operation.TYPE_ASSIGNMENT:
                return new Assignment(name);
            case Operation.TYPE_OPERATOR:
                return new Operator(name);
            case Operation.TYPE_BLANK:
                throw new IllegalArgumentException("Blank type should not be used here");
            default:
                throw new IllegalArgumentException("Unknown syntax type");
        }
        return null;
    }
}
