package tcd.android.com.codecombatmobile;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Assignment;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Comment;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Expression;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Method;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Object;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.OperationFactory.OperationType;
import tcd.android.com.codecombatmobile.util.CodeConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_FLOW_CONTROL;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_FUNCTION;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_METHOD;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_VARIABLE;

public class CodeSnippetConverterTest {
    @Test
    public void convert_comment() {
        Operation operation = CodeConverter.getOperation("# abc");
        assertNotNull(operation);
        assertTrue(operation instanceof Comment);

        Comment comment = (Comment) operation;
        assertEquals(comment.getButtonName(), "abc");
    }

    @Test
    public void convert_methodWithNoParam() {
        Operation operation = CodeConverter.getOperation("hero.moveRight()");
        assertNotNull(operation);
        assertTrue(operation instanceof Object);

        Object object = (Object) operation;
        assertEquals(object.getButtonName(), "hero");
        assertEquals(object.getCurrentMethod().getButtonName(), ".moveRight()");
    }

    @Test
    public void convert_expression() {
        Operation operation = CodeConverter.getValue("hero - moveRight()");
        assertNotNull(operation);
        assertTrue(operation instanceof Expression);

        Expression expression = (Expression) operation;
//        assertEquals(object.getButtonName(), "hero");
//        assertEquals(object.getCurrentMethod().getButtonName(), ".moveRight()");
    }

    @Test
    public void convert_assignment() {
        Operation operation = CodeConverter.getOperation("hero *= 3");
        assertNotNull(operation);
        assertTrue(operation instanceof Assignment);

        Assignment assignment = (Assignment) operation;
//        assertEquals(expression.getButtonName(), "hero");
//        assertEquals(object.getCurrentMethod().getButtonName(), ".moveRight()");
    }
}