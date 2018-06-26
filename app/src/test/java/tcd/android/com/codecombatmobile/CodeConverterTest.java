package tcd.android.com.codecombatmobile;

import org.junit.Test;

import java.util.List;

import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.OperationFactory.OperationType;
import tcd.android.com.codecombatmobile.util.CodeConverter;

import static org.junit.Assert.assertEquals;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_FUNCTION;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CodeConverterTest {
    @Test
    public void convert_withoutHero() {
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
    public void convert_withHero() {
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
        OperationType[] expectValues = new OperationType[]{
                new OperationType(TYPE_FUNCTION, "buildXY", 3),
                new OperationType(Operation.TYPE_FLOW_CONTROL, "for"),
                new OperationType(TYPE_FUNCTION, "moveDown", 0),
                new OperationType(TYPE_FUNCTION, "moveLeft", 0),
                new OperationType(TYPE_FUNCTION, "moveRight", 0),
                new OperationType(TYPE_FUNCTION, "moveUp", 0),
                new OperationType(TYPE_FUNCTION, "findNearestEnemy", 0),
                new OperationType(TYPE_FUNCTION, "say", 1),
        };

        List<OperationType> types = CodeConverter.getTypesFromString(lines);
        assertEquals(types.size(), expectValues.length);
        for (int i = 0; i < types.size(); i++) {
            OperationType type = types.get(i);
            OperationType expectVal = expectValues[i];

            assertEquals(type.getSyntaxType(), expectVal.getSyntaxType());
            assertEquals(type.getName(), expectVal.getName());

            if (type.getSyntaxType() == TYPE_FUNCTION) {
                assertEquals((int) type.getData().get(0), (int) expectVal.getData().get(0));
            }
        }
    }
}