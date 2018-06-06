package tcd.android.com.codecombatmobile.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.syntax.Operation;
import tcd.android.com.codecombatmobile.data.syntax.OperationFactory;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor;
import tcd.android.com.codecombatmobile.ui.widget.SyntaxButton;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_ASSIGNMENT;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_DECLARATION;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_FLOW_CONTROL;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_FUNCTION;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_OPERATOR;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_VALUE;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_VARIABLE;

public class CodeEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CodeEditorActivity.class.getSimpleName();

    private LinearLayout mRootLayout, mKeyboardLayout;
    private ScrollView mEditorScrollView;
    private CodeEditor mCodeEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_editor);

        initUiComponents();
    }

    private void initUiComponents() {
        mRootLayout = findViewById(R.id.ll_root);
        mKeyboardLayout = findViewById(R.id.ll_keyboard_layout);
        mEditorScrollView = findViewById(R.id.sv_editor_container);

        mCodeEditor = findViewById(R.id.code_editor);
        mCodeEditor.setOnClickListener(this);

        findViewById(R.id.iv_hide_keyboard_button).setOnClickListener(this);
        findViewById(R.id.iv_reset_button).setOnClickListener(this);
        findViewById(R.id.iv_undo_button).setOnClickListener(this);
        findViewById(R.id.iv_redo_button).setOnClickListener(this);
        findViewById(R.id.iv_backspace_button).setOnClickListener(this);

        initVirtualKeyboard();
    }

    private void initVirtualKeyboard() {
        final LinearLayout buttonContainer = findViewById(R.id.ll_button_container);

        List<Pair<Integer, String>> opTypes = new ArrayList<>();
        opTypes.add(new Pair<>(TYPE_FLOW_CONTROL, "if"));
        opTypes.add(new Pair<>(TYPE_FLOW_CONTROL, "for"));
        opTypes.add(new Pair<>(TYPE_FLOW_CONTROL, "return"));
        opTypes.add(new Pair<>(TYPE_DECLARATION, "func"));
        opTypes.add(new Pair<>(TYPE_DECLARATION, "var"));
        opTypes.add(new Pair<>(TYPE_VARIABLE, "abc"));
        opTypes.add(new Pair<>(TYPE_FUNCTION, "drawBox()_0"));
        opTypes.add(new Pair<>(TYPE_FUNCTION, "drawBoxes()_3"));
        opTypes.add(new Pair<>(TYPE_VALUE, "True"));
        opTypes.add(new Pair<>(TYPE_VALUE, "False"));
        opTypes.add(new Pair<>(TYPE_VALUE, "null"));
        opTypes.add(new Pair<>(TYPE_ASSIGNMENT, "="));
        opTypes.add(new Pair<>(TYPE_ASSIGNMENT, "+="));
        opTypes.add(new Pair<>(TYPE_ASSIGNMENT, "-="));
        opTypes.add(new Pair<>(TYPE_ASSIGNMENT, "*="));
        opTypes.add(new Pair<>(TYPE_OPERATOR, "+"));
        opTypes.add(new Pair<>(TYPE_ASSIGNMENT, "/="));
        opTypes.add(new Pair<>(TYPE_OPERATOR, "-"));
        opTypes.add(new Pair<>(TYPE_OPERATOR, "*"));
        opTypes.add(new Pair<>(TYPE_OPERATOR, "/"));
        final OperationFactory factory = new OperationFactory();

        final SyntaxButton[] buttons = new SyntaxButton[opTypes.size()];
        LinearLayout columnLayout = new LinearLayout(this);
        for (int i = 0; i < opTypes.size(); i++) {
            if (i % 4 == 0) {
                columnLayout = DisplayUtil.getVerticalLinearLayout(this);
                buttonContainer.addView(columnLayout);
            }

            final Pair<Integer, String> pair = opTypes.get(i);
            final Operation operation = factory.getOperation(pair);
            if (operation != null) {
                SyntaxButton button = new SyntaxButton(this);
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

                buttons[i] = button;
                columnLayout.addView(button);
            }
        }

        setOnOperationChangedListener(buttons);
    }

    private void setOnOperationChangedListener(final SyntaxButton[] buttons) {
        mCodeEditor.setOnOperationChangedListener(new CodeEditor.OnOperationChangedListener() {
            @Override
            public void onOperationChangedListener(@Nullable Operation operation) {
                if (operation == null || operation.getContainer() == null) {
                    for (SyntaxButton button : buttons) {
                        button.setEnabled(true);
                    }
                } else {
                    Operation container = operation.getContainer();
                    for (SyntaxButton button : buttons) {
                        int index = container.getReplacementIndex(operation, button.getOperation());
                        button.setEnabled(index >= 0);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mKeyboardLayout.getVisibility() == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(mRootLayout);
            mKeyboardLayout.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.code_editor:
                if (mKeyboardLayout.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(mRootLayout);
                    mKeyboardLayout.setVisibility(View.VISIBLE);
                    mKeyboardLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEditorScrollView.smoothScrollTo(0, mEditorScrollView.getHeight());
                        }
                    }, 500);
                }
                break;
            case R.id.iv_hide_keyboard_button:
                TransitionManager.beginDelayedTransition(mRootLayout);
                mKeyboardLayout.setVisibility(View.GONE);
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
