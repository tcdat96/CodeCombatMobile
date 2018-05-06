package tcd.android.com.codecombatmobile.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import tcd.android.com.codecombatmobile.data.Syntax.Operation;
import tcd.android.com.codecombatmobile.data.OperationFactory;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor;
import tcd.android.com.codecombatmobile.ui.widget.MainButton;
import tcd.android.com.codecombatmobile.util.DisplayUtil;
import tcd.android.com.codecombatmobile.R;

import static tcd.android.com.codecombatmobile.data.Syntax.Operation.TYPE_CONDITION;
import static tcd.android.com.codecombatmobile.data.Syntax.Operation.TYPE_DECLARATION;
import static tcd.android.com.codecombatmobile.data.Syntax.Operation.TYPE_FUNCTION;
import static tcd.android.com.codecombatmobile.data.Syntax.Operation.TYPE_VALUE;
import static tcd.android.com.codecombatmobile.data.Syntax.Operation.TYPE_VARIABLE;

public class CodeEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CodeEditorActivity.class.getSimpleName();

    private CodeEditor mCodeEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_editor);

        initUiComponents();
    }

    private void initUiComponents() {
        mCodeEditor = findViewById(R.id.code_editor);

        findViewById(R.id.iv_hide_keyboard_button).setOnClickListener(this);
        findViewById(R.id.iv_reset_button).setOnClickListener(this);
        findViewById(R.id.iv_undo_button).setOnClickListener(this);
        findViewById(R.id.iv_redo_button).setOnClickListener(this);
        findViewById(R.id.iv_backspace_button).setOnClickListener(this);

        initVirtualKeyboard();
    }

    private void initVirtualKeyboard() {
        LinearLayout rootContainer = findViewById(R.id.ll_virtual_keyboard);

        List<Pair<Integer, String>> opTypes = new ArrayList<>();
        opTypes.add(new Pair<>(TYPE_CONDITION, "if"));
        opTypes.add(new Pair<>(TYPE_CONDITION, "for"));
        opTypes.add(new Pair<>(TYPE_CONDITION, "return"));
        opTypes.add(new Pair<>(TYPE_DECLARATION, "var"));
        opTypes.add(new Pair<>(TYPE_VARIABLE, "abc"));
        opTypes.add(new Pair<>(TYPE_FUNCTION, "drawBox()_0"));
        opTypes.add(new Pair<>(TYPE_FUNCTION, "drawBoxes()_3"));
        opTypes.add(new Pair<>(TYPE_VALUE, "True"));
        opTypes.add(new Pair<>(TYPE_VALUE, "False"));
        opTypes.add(new Pair<>(TYPE_VALUE, "null"));
        final OperationFactory factory = new OperationFactory();

        LinearLayout columnLayout = new LinearLayout(this);
        for (int i = 0; i < opTypes.size(); i++) {
            if (i % 4 == 0) {
                columnLayout = DisplayUtil.getVerticalLinearLayout(this);
                rootContainer.addView(columnLayout);
            }

            final Pair<Integer, String> pair = opTypes.get(i);
            final Operation operation = factory.getOperation(pair);
            if (operation != null) {
                MainButton button = new MainButton(this);
                button.setText(operation.getButtonName());
                button.setOperation(operation);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Operation newOp = factory.getOperation(pair);
                        if (newOp != null) {
                            mCodeEditor.addOperation(newOp);
                        }
                    }
                });

                columnLayout.addView(button);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_hide_keyboard_button:
                break;
            case R.id.iv_reset_button:
                mCodeEditor.clear();
                break;
            case R.id.iv_undo_button:
                break;
            case R.id.iv_redo_button:
                break;
            case R.id.iv_backspace_button:
                mCodeEditor.removeOperation();
                break;
            default:
                break;
        }
    }
}
