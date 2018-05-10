package tcd.android.com.codecombatmobile.data;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import tcd.android.com.codecombatmobile.data.Syntax.CodeBlock;
import tcd.android.com.codecombatmobile.data.Syntax.FlowControl;
import tcd.android.com.codecombatmobile.data.Syntax.FuncDeclaration;
import tcd.android.com.codecombatmobile.data.Syntax.VarDeclaration;
import tcd.android.com.codecombatmobile.data.Syntax.Function;
import tcd.android.com.codecombatmobile.data.Syntax.Operation;
import tcd.android.com.codecombatmobile.data.Syntax.Return;
import tcd.android.com.codecombatmobile.data.Syntax.Value;
import tcd.android.com.codecombatmobile.data.Syntax.Variable;

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
                    case "var":
                        return new VarDeclaration();
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
                return new Value(name);
            case Operation.TYPE_OPERATOR:
                break;
            default:
                throw new IllegalArgumentException("Unknown syntax type");
        }
        return null;
    }
}
