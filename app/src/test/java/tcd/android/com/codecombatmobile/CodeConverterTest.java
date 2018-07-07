package tcd.android.com.codecombatmobile;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.OperationFactory.OperationType;
import tcd.android.com.codecombatmobile.util.CodeConverter;

import static org.junit.Assert.assertEquals;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_FLOW_CONTROL;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_FUNCTION;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_METHOD;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_VARIABLE;

public class CodeConverterTest {
    @Test
    public void convert_withoutVar() {
        String lines[] = new String[]{
                "buildXY(buildType, x, y)",
                "while-true loop",
                "moveDown()",
                "moveLeft()",
                "moveRight()",
                "moveUp()",
                "findNearestEnemy()",
                "say(message)"
        };
        convert(lines);
    }

    @Test
    public void convert_withVar() {
        String lines[] = new String[]{
                "hero.buildXY(buildType, x, y)",
                "while-true loop",
                "hero.moveDown()",
                "hero.moveLeft()",
                "hero.moveRight()",
                "hero.moveUp()",
                "hero.findNearestEnemy()",
                "hero.say(message)"
        };
        convert(lines);
    }

    private void convert(String[] lines) {
        List<Object> methods = new ArrayList<Object>(Arrays.asList(".buildXY()/3", ".moveDown()/0", ".moveLeft()/0", ".moveRight()/0",
                ".moveUp()/0", ".findNearestEnemy()/0", ".say()/1"));
        OperationType hero = new OperationType(TYPE_VARIABLE, "hero");
        hero.setData(methods);
        OperationType[] expected = new OperationType[]{
                hero,
                new OperationType(TYPE_METHOD, ".buildXY()", 3),
                new OperationType(TYPE_FLOW_CONTROL, "for"),
                new OperationType(TYPE_METHOD, ".moveDown()", 0),
                new OperationType(TYPE_METHOD, ".moveLeft()", 0),
                new OperationType(TYPE_METHOD, ".moveRight()", 0),
                new OperationType(TYPE_METHOD, ".moveUp()", 0),
                new OperationType(TYPE_METHOD, ".findNearestEnemy()", 0),
                new OperationType(TYPE_METHOD, ".say()", 1),
        };

        List<OperationType> types = CodeConverter.getTypesFromString(lines);
        assertEquals(types.size(), expected.length);
        for (int i = 0; i < types.size(); i++) {
            OperationType type = types.get(i);
            OperationType expectVal = expected[i];

            assertEquals(type.getSyntaxType(), expectVal.getSyntaxType());
            assertEquals(type.getName(), expectVal.getName());

            if (type.getSyntaxType() == TYPE_FUNCTION) {
                assertEquals((int) type.getData().get(0), (int) expectVal.getData().get(0));
            }
        }
    }
}