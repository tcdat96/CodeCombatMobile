package tcd.android.com.codecombatmobile.ui.widget.CodeEditor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.TextViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.VarDeclaration;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by ADMIN on 21/04/2018.
 */

public class CodeEditor extends FrameLayout {

    private List<Operation> mOperations = new ArrayList<>();
    private Operation mSelectedOperation;

    @Nullable
    private CodeEditorActivity mActivity = null;

    @NonNull
    private LinearLayout mCodeLines = new LinearLayout(getContext());
    private EditText mUserInputEditText;
    private UserInputTextWatcher mTextWatcher;

    public interface OnOperationChangedListener {
        void onOperationChangedListener(@Nullable Operation operation);
    }

    @Nullable
    private OnOperationChangedListener mOnOperationChangedListener = null;

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


    public void clear() {
        mCodeLines.removeAllViews();
        setSelectedOperation(null);
        mOperations.clear();
        display();
    }

    public void addOperation(@NonNull Operation newOp) {
        addOperation(mOperations.size(), newOp);
    }

    private void addOperation(int index, @NonNull Operation newOp) {
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
                } else {
                    Toast.makeText(getContext(), R.string.cannot_insert_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            updateOperationAt(curOpIndex);
        }
    }

    public void removeOperation() {
        if (mSelectedOperation == null) {
            return;
        }

        // get operation index for later update
        Operation root = mSelectedOperation.getRoot();
        int index = mOperations.indexOf(root);

        // remove the selected operation
        Operation container = mSelectedOperation.getContainer();
        if (container == null) {
            mOperations.remove(mSelectedOperation);
            removeViewAt(index);
        } else {
            container.removeOperation(mSelectedOperation);
            container.setOnClickListener(this);
            updateOperationAt(index);
        }
        setSelectedOperation(null);
    }

    public void display() {
        for (int i = 0; i < mOperations.size(); i++) {
            updateOperationAt(i);
        }
    }

    private void removeSelection() {
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
        }

        Operation root = curOp.getRoot();
        int index = mOperations.indexOf(root);
        updateOperationAt(index);

        if (curOp instanceof UserInput) {
            displayUserInput(curOp);
        }
    }

    private void updateOperationAt(int index) {
        if (index < 0) {
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

    private void insertBlankOperationAt(int index) {
        TextView container = getEditorTextView();
        mCodeLines.addView(container, index);

        Blank blank = new Blank();
        addOperation(index, blank);
    }

    private TextView getEditorTextView() {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
        TextViewCompat.setTextAppearance(textView, R.style.TextAppearance_AppCompat_Medium);
        return textView;
    }


    private void displayUserInput(Operation operation) {
        if (operation instanceof UserInput) {
            final UserInput inputOp = (UserInput) operation;
            final Operation root = operation.getRoot();
            final int index = mOperations.indexOf(root);

            // position the EditText
            mUserInputEditText.setX(inputOp.getLeft());
            mUserInputEditText.setY(inputOp.getTop());

            focusUserInput();
            mUserInputEditText.setVisibility(VISIBLE);

            mUserInputEditText.removeTextChangedListener(mTextWatcher);
            mTextWatcher = new UserInputTextWatcher(inputOp, index);
            mUserInputEditText.addTextChangedListener(mTextWatcher);

            // done editing
            mUserInputEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(final View v, boolean hasFocus) {
                    if (!hasFocus) {
                        display();
                        mUserInputEditText.removeTextChangedListener(mTextWatcher);
                        mUserInputEditText.setText(null);
                        mUserInputEditText.setVisibility(GONE);

                        // add new variable
                        if (root instanceof VarDeclaration) {
                            if (mActivity != null) {
                                mActivity.addVariableButton(inputOp.getButtonName());
                            }
                        }
                    }
                }
            });
        }
    }

    private void focusUserInput() {
        // show physical keyboard
        mUserInputEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        // hide virtual keyboard
        if (mActivity != null) {
            mActivity.setVirtualKeyboardVisibility(View.GONE);
        }
    }

    private boolean mIsPressing = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsPressing = true;
                return true;
            case MotionEvent.ACTION_UP:
                if (mIsPressing) {
                    // hide physical keyboard
                    mUserInputEditText.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(mUserInputEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    // pass click event to parent view
                    if (getContext() instanceof OnClickListener) {
                        ((OnClickListener) getContext()).onClick(this);
                    }

                    // if user tap outside code
                    Rect bounds = new Rect();
                    int y = (int) event.getY();
                    // TODO: 08/06/2018 should implement binary search instead of sequential search
                    for (int i = 0; i < mCodeLines.getChildCount(); i++) {
                        View view = mCodeLines.getChildAt(i);
                        view.getHitRect(bounds);
                        if (y > bounds.top && y < bounds.bottom) {
                            // add new line
                            removeSelection();
                            insertBlankOperationAt(i + 1);
                        }
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
