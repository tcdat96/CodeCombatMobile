package tcd.android.com.codecombatmobile.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.OperationFactory.OperationType;

import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_FLOW_CONTROL;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_VARIABLE;

public class CodeConverter {
    @NonNull
    public static List<OperationType> getTypesFromString(String[] lines) {
        List<OperationType> types = new ArrayList<>(lines.length);
        for (String line : lines) {
            addNewType(types, line);
        }
        return types;
    }

    private static void addNewType(List<OperationType> types, String line) {
        if (line.startsWith("while")) {
            types.add(new OperationType(TYPE_FLOW_CONTROL, "for"));
            return;
        }

        if (line.startsWith("if")) {
            types.add(new OperationType(TYPE_FLOW_CONTROL, "if"));
            return;
        }

        if (line.contains("(")) {
            // get method signature
            OperationType[] result = getMethodType(line);
            OperationType varType = result[0];
            OperationType methodType = result[1];

            // add method to existing variable
            int index = getVariableIndex(types, varType.getName());
            if (index >= 0) {
                types.get(index).addData(varType.getData().get(0));
            } else {
                // if it does not exist, add this variable
                types.add(varType);
            }

            types.add(methodType);
        }
    }

    @NonNull
    private static OperationType[] getMethodType(String line) {
        String[] tokens = line.split("[.(,) ]");
        List<String> parts = new ArrayList<>(Arrays.asList(tokens));
        parts.removeAll(Collections.singleton(""));

        int methodIndex = line.contains(".") ? 1 : 0;
        String methodName = String.format(".%s()", parts.get(methodIndex));
        int paramTotal = parts.size() - (methodIndex + 1);
        OperationType method = new OperationType(Operation.TYPE_METHOD, methodName, paramTotal);

        String varName = line.contains(".") ? parts.get(0) : "hero";
        String methodSignature = methodName + "/" + paramTotal;
        OperationType variable = new OperationType(TYPE_VARIABLE, varName, methodSignature);

        return new OperationType[]{variable, method};
    }

    private static int getVariableIndex(@NonNull List<OperationType> types, @NonNull String varName) {
        for (int index = 0; index < types.size(); index++) {
            OperationType type = types.get(index);
            if (type.getSyntaxType() == TYPE_VARIABLE) {
                if (type.getName().equals(varName)) {
                    return index;
                }
            }
        }
        return -1;
    }
}
