package tcd.android.com.codecombatmobile.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Assignment;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Comment;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Expression;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Function;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Method;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Object;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.OperationFactory.OperationType;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operator;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.UserInput;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Value;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Variable;

import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_FLOW_CONTROL;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_VARIABLE;

public class CodeConverter {
    // code spells
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

    // code snippet
    @Nullable
    public static List<Operation> getOperationsFromString(String[] lines) {
        List<Operation> types = new ArrayList<>(lines.length);
        for (String line : lines) {
            if (TextUtils.isEmpty(line)) {
                continue;
            }

            Operation operation = getOperation(line);
            if (operation == null) {
                return null;
            }
            types.add(operation);
        }
        return types;
    }

    @Nullable
    public static Operation getOperation(String line) {
        // comment
        if (line.startsWith("#")) {
            return new Comment(line.substring(1).trim());
        }

        if (isMethod(line)) {
            return getMethodCall(line);
        }

        if (isAssignment(line)) {
            return getAssignment(line);
        }

        return null;
    }

    private static boolean isMethod(String line) {
        // TODO: 07/07/2018 not an accurate approach, for example a = ".("
        return DataUtil.containsAll(line, ".", "(", ")");
    }

    private static boolean isExpression(String line) {
        return DataUtil.containsAtLeast(line, Operator.OPERATORS);
    }

    private static boolean isAssignment(String line) {
        return DataUtil.containsAtLeast(line, Assignment.ASSIGMENT_OPERATORS);
    }

    private static Operation getMethodCall(String line) {
        List<String> parts = DataUtil.split(line, "[.(,) ]");

        boolean isMethod = line.contains(".");
        String methodName = String.format(".%s()", parts.get(isMethod ? 1 : 0));
        int paramTotal = parts.size() - (isMethod ? 2 : 1);

        // parameters
        Operation[] parameters = new Operation[paramTotal];
        for (int i = 0; i < paramTotal; i++) {
            int partIndex = i + (isMethod ? 2 : 1);
            parameters[i] = getValue(parts.get(partIndex));
        }

        if (isMethod) {
            Method method = new Method(methodName, parameters);
            return new Object(parts.get(0), method);
        } else {
            return new Function(methodName, parameters);
        }
    }

    private static Operation getAssignment(String line) {
        int index = DataUtil.indexOfAny(line, Assignment.ASSIGMENT_OPERATORS);
        if (index > -1) {
            String varName = line.substring(0, index).trim();
            int equalIndex = line.indexOf("=");
            String assignment = line.substring(index, equalIndex + 1);
            Expression expression = getExpression(line.substring(equalIndex + 1).trim());
            return new Assignment(assignment, new Variable(varName), expression);
        }
        return null;
    }

    private static Expression getExpression(String line) {
        // TODO: 07/07/2018 not working, missing operators
        List<String> parts = DataUtil.split(line.trim(), Operator.OPERATORS);
        List<Operation> operands = new ArrayList<>(parts.size());
        for (String part : parts) {
            Operation operand = getValue(part);
            operands.add(operand);
        }
        return new Expression(operands);
    }

    public static Operation getValue(String value) {
        // expression
        if (isExpression(value)) {
            return getExpression(value);
        }

        if (isMethod(value)) {
            return getMethodCall(value);
        }

        if (DataUtil.containsAtLeast(value, "\"", "'")) {
            UserInput userInput = new UserInput();
            userInput.setName(value);
        }

        try {
            float floatVal = Float.parseFloat(value);
            return new Value(value);
        } catch (NumberFormatException nfe) {
            return new Object(value);
        }
    }
}
