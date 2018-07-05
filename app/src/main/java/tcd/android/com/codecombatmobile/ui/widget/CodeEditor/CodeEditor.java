package tcd.android.com.codecombatmobile.ui.widget.CodeEditor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.TextViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.ui.CodeEditorActivity;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Blank;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.UserInput;
import tcd.android.com.codecombatmobile.util.DataUtil;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by ADMIN on 21/04/2018.
 */

public class CodeEditor extends FrameLayout {

    private List<Operation> mOperations = new ArrayList<>();
    private Operation mSelectedOperation;

    @Nullable
    private CodeEditorActivity mActivity = null;
    private List<String> mCode;

    @NonNull
    private LinearLayout mCodeLines = new LinearLayout(getContext());
    private EditText mUserInputEditText;
    private UserInputTextWatcher mTextWatcher;

    /**
     * an interface to update subscribers about the change of current selected operation
     */
    public interface OnOperationChangedListener {
        void onOperationChangedListener(@Nullable Operation operation);
    }

    @Nullable
    private OnOperationChangedListener mOnOperationChangedListener = null;

    // for keeping track of touch event
    private boolean mIsPressing = false;


    public CodeEditor(Context context) {
        super(context);
        init(null, 0);
    }

    public CodeEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CodeEditor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // save activity
        Activity activity = (Activity) getContext();
        if (activity instanceof CodeEditorActivity) {
            mActivity = (CodeEditorActivity) activity;
        }

        initUiComponents();
    }

    private void initUiComponents() {
        mCodeLines = new LinearLayout(getContext());
        mCodeLines.setOrientation(LinearLayout.VERTICAL);
        addView(mCodeLines);

        mUserInputEditText = new EditText(getContext());
        mUserInputEditText.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        mUserInputEditText.setTextColor(Color.TRANSPARENT);
        mUserInputEditText.setPadding(0, 0, 0, 0);
        mUserInputEditText.setBackgroundResource(android.R.color.transparent);
        addView(mUserInputEditText);
        mUserInputEditText.setVisibility(GONE);
    }


    public void setOnOperationChangedListener(OnOperationChangedListener listener) {
        mOnOperationChangedListener = listener;
    }

    public void setSelectedOperation(Operation operation) {
        mSelectedOperation = operation;
        if (mOnOperationChangedListener != null) {
            mOnOperationChangedListener.onOperationChangedListener(operation);
        }
    }

    public Operation getSelectedOperation() {
        return mSelectedOperation;
    }


    /**
     * get displaying code to send to the editor
     * all indentation and blank will be trimmed off
     *
     * @return a list of code lines, with null indicating that the current line should be unindent-ed,
     * or null if code hasn't changed
     */
    @NonNull
    public List<String> getCode() {
        if (mCode != null) {
            return mCode;
        }

        mCode = new ArrayList<>(mOperations.size());
        int childCount = mCodeLines.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView codeLine = (TextView) mCodeLines.getChildAt(i);
            String code = codeLine.getText().toString();
            if (i < childCount - 1) {
                code += "\n";
            }

            code = code.replace(Operation.BLANK_VALUE, "");

            // if it is CodeBlock
            if (code.contains(Operation.INDENT_VALUE)) {
                code = code.replace(Operation.INDENT_VALUE, "");
                mCode.add(code);
                mCode.add(null);
            } else {
                mCode.add(code);
            }
        }
        return mCode;
    }


    // CRUD operations
    public void addOperation(@NonNull Operation newOp) {
        addOperation(mOperations.size(), newOp);
    }

    private void addOperation(int index, @NonNull Operation newOp) {
        mCode = null;
        newOp.setOnClickListener(this);

        if (mSelectedOperation == null) {
            mOperations.add(index, newOp);
            display();
        } else {
            // get operation index for later update
            Operation root = mSelectedOperation.getRoot();
            int curOpIndex = mOperations.indexOf(root);
            // replace current operation
            Operation container = mSelectedOperation.getContainer();
            if (container == null) {
                mOperations.set(curOpIndex, newOp);
                setSelectedOperation(null);
            } else {
                boolean result = container.replaceOperation(mSelectedOperation, newOp);
                if (result) {
                    container.setOnClickListener(this);
                    setSelectedOperation(null);

                    if (newOp instanceof UserInput) {
                        showUserInputDelayed(newOp);
                    }
                } else {
                    Toast.makeText(getContext(), R.string.error_cannot_insert, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            updateOperationAt(curOpIndex);
        }
    }

    private void showUserInputDelayed(final Operation userInput) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setSelectedOperation(userInput);
                displayUserInput(userInput);
            }
        }, 200);
    }

    public void removeOperation() {
        if (mSelectedOperation == null) {
            return;
        }
        mCode = null;

        // get operation index for later update
        Operation root = mSelectedOperation.getRoot();
        int index = mOperations.indexOf(root);

        // remove the selected operation
        Operation container = mSelectedOperation.getContainer();
        if (container == null) {
            mOperations.remove(mSelectedOperation);
            mCodeLines.removeViewAt(index);
        } else {
            container.removeOperation(mSelectedOperation);
            container.setOnClickListener(this);
            updateOperationAt(index);
        }
        setSelectedOperation(null);
    }


    // update operations on screen
    public void clear() {
        mCodeLines.removeAllViews();
        setSelectedOperation(null);
        mOperations.clear();
        display();
    }

    public void display() {
        for (int i = 0; i < mOperations.size(); i++) {
            updateOperationAt(i);
        }
    }

    private void removeCurrentSelection() {
        if (mSelectedOperation != null) {
            Operation root = mSelectedOperation.getRoot();
            root.setOnClickListener(this);
            setSelectedOperation(null);

            int index = mOperations.indexOf(root);
            updateOperationAt(index);
        }
    }

    public void updateClickedOperation(@Nullable Operation prevOp, @NonNull Operation curOp) {
        if (prevOp != null) {
            Operation root = prevOp.getRoot();
            int index = mOperations.indexOf(root);
            updateOperationAt(index);

            // get out of input mode
            if (prevOp instanceof UserInput) {
                requestUserInputFocus(false);
            }
        }

        Operation root = curOp.getRoot();
        int index = mOperations.indexOf(root);
        updateOperationAt(index);

        if (curOp instanceof UserInput) {
            displayUserInput(curOp);
        }
    }

    private void updateOperationAt(int index) {
        if (index < 0 || index >= mOperations.size()) {
            return;
        }

        TextView container;
        if (index < mCodeLines.getChildCount()) {
            container = (TextView) mCodeLines.getChildAt(index);
            container.setText(null);
        } else {
            container = getEditorTextView();
            mCodeLines.addView(container);
        }

        mOperations.get(index).display(container);
    }

    private TextView getEditorTextView() {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
        TextViewCompat.setTextAppearance(textView, R.style.TextAppearance_AppCompat_Medium);
        return textView;
    }

    private void insertBlankOperationAt(int index) {
        TextView container = getEditorTextView();
        mCodeLines.addView(container, index);

        Blank blank = new Blank();
        addOperation(index, blank);
    }


    // user input operation
    private void displayUserInput(Operation operation) {
        if (operation instanceof UserInput) {
            final UserInput inputOp = (UserInput) operation;
            final Operation root = operation.getRoot();
            final int index = mOperations.indexOf(root);

            // position the EditText
            mUserInputEditText.setX(inputOp.getLeft());
            mUserInputEditText.setY(inputOp.getTop());

            requestUserInputFocus(true);
            mUserInputEditText.setVisibility(VISIBLE);

            mUserInputEditText.removeTextChangedListener(mTextWatcher);
            mTextWatcher = new UserInputTextWatcher(inputOp, index);
            mUserInputEditText.addTextChangedListener(mTextWatcher);

            final String oldVarName = inputOp.getButtonName();
            // done editing
            mUserInputEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(final View v, boolean hasFocus) {
                    if (!hasFocus) {
                        display();
                        mUserInputEditText.removeTextChangedListener(mTextWatcher);
                        mUserInputEditText.setText(null);
                        mUserInputEditText.setVisibility(GONE);

                        if (inputOp.isVarDeclaration()) {
                            // validate variable name
                            String newVarName = inputOp.getButtonName();
                            if (!validateVariableName(newVarName)) {
                                return;
                            }

                            // update the variable list
                            if (mActivity != null) {
                                mActivity.updateVariableButton(oldVarName, inputOp);
                            }
                        }
                    }
                }

                private boolean validateVariableName(String varName) {
                    if (DataUtil.isVarNameValid(varName)) {
                        return true;
                    }

                    String invalidNameMsg = String.format(getContext().getString(R.string.error_invalid_variable_name), varName);
                    Toast.makeText(getContext(), invalidNameMsg, Toast.LENGTH_LONG).show();
                    inputOp.reset();
                    return true;
                }
            });
        }
    }

    private void requestUserInputFocus(boolean focus) {
        if (focus) {
            DisplayUtil.showPhysicalKeyboard(mUserInputEditText);
            // hide virtual keyboard
            if (mActivity != null) {
                mActivity.setVirtualKeyboardVisibility(View.GONE);
            }
        } else {
            DisplayUtil.hidePhysicalKeyboard(mUserInputEditText);
            if (mActivity != null) {
                mActivity.showVirtualKeyboard();
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsPressing = true;
                return true;
            case MotionEvent.ACTION_UP:
                if (mIsPressing) {
                    requestUserInputFocus(false);

                    // if user tap outside code
                    int lineIndex = getUserTouchLineIndex(event);
                    if (lineIndex >= 0) {
                        if (!(mSelectedOperation instanceof UserInput)) {
                            insertBlankOperationAt(lineIndex + 1);
                        }
                        removeCurrentSelection();
                    }

                    mIsPressing = false;
                }
                return false;
            case MotionEvent.ACTION_CANCEL:
                mIsPressing = false;
                return false;
            default:
        }
        return super.onTouchEvent(event);
    }

    private int getUserTouchLineIndex(MotionEvent event) {
        Rect bounds = new Rect();
        int y = (int) event.getY();
        // TODO: 08/06/2018 should implement binary search instead of sequential search
        for (int lineIndex = 0; lineIndex < mCodeLines.getChildCount(); lineIndex++) {
            View view = mCodeLines.getChildAt(lineIndex);
            view.getHitRect(bounds);
            if (y > bounds.top && y < bounds.bottom) {
                return lineIndex;
            }
        }
        return -1;
    }

    private class UserInputTextWatcher implements TextWatcher {

        private UserInput mUserInputOp;
        private int mOperationIndex;

        UserInputTextWatcher(UserInput operation, int index) {
            mUserInputOp = operation;
            mOperationIndex = index;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mUserInputOp.setName(s.toString());
            updateOperationAt(mOperationIndex);
        }

        @Override
        public void afterTextChanged(Editable s) {
            // do nothing
        }
    }
}
