package tcd.android.com.codecombatmobile.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.OperationFactory.OperationType;

import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_FLOW_CONTROL;

public class CodeConverter {
    @NonNull
    public static List<OperationType> getTypesFromString(String[] lines) {
        List<OperationType> types = new ArrayList<>(lines.length);
        for (String line : lines) {
            OperationType type = getTypeFromString(line);
            if (type != null) {
                types.add(type);
            }
        }
        return types;
    }

    @Nullable
    private static OperationType getTypeFromString(String line) {
        OperationType type = null;
        if (line.startsWith("while")) {
            type = new OperationType(TYPE_FLOW_CONTROL, "for");
        } else if (line.startsWith("if")) {
            type = new OperationType(TYPE_FLOW_CONTROL, "if");
        } else if (line.contains("(")) {
            type = getMethodType(line);
        }
        return type;
    }

    @NonNull
    private static OperationType getMethodType(String line) {
        String[] tokens = line.split("[.(,) ]");
        List<String> parts = new ArrayList<>(Arrays.asList(tokens));
        parts.removeAll(Collections.singleton(""));

        String name = String.format(".%s()", line.contains(".") ? parts.get(1) : parts.get(0));
        int paraTotal = parts.size() - (line.contains(".") ? 2 : 1);
        return new OperationType(Operation.TYPE_METHOD, name, paraTotal);
    }
}
