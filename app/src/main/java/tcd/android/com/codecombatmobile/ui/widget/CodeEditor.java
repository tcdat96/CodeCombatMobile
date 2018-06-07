package tcd.android.com.codecombatmobile.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.TextViewCompat;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.syntax.Operation;

/**
 * Created by ADMIN on 21/04/2018.
 */

public class CodeEditor extends LinearLayout {

    private List<Operation> mOperations = new ArrayList<>();
    private Operation mSelectedOperation;

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

        setOrientation(VERTICAL);
    }

    public void setOnOperationChangedListener(OnOperationChangedListener listener) {
        mOnOperationChangedListener = listener;
    }

    public void clear() {
        removeAllViews();
        setSelectedOperation(null);
        mOperations.clear();
        display();
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

    public void addOperation(@NonNull Operation newOp) {
        newOp.setOnClickListener(this);

        if (mSelectedOperation == null) {
            mOperations.add(newOp);
            display();
        } else {
            // get operation index for later update
            Operation root = mSelectedOperation.getRoot();
            int index = mOperations.indexOf(root);
            // replace current operation
            Operation container = mSelectedOperation.getContainer();
            if (container == null) {
                mOperations.set(index, newOp);
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
            updateOperationAt(index);
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
        // clear current content
        for (int i = 0; i < getChildCount(); i++) {
            ((TextView)getChildAt(i)).setText(null);
        }

        for (int i = 0; i < mOperations.size(); i++) {
            updateOperationAt(i);
        }
    }

    public void updateOperationChanged(Operation operation) {
        Operation root = operation.getRoot();
        int index = mOperations.indexOf(root);
        if (index >= 0) {
            updateOperationAt(index);
        }
    }

    private void updateOperationAt(int index) {
        TextView container;
        if (index < getChildCount()) {
            container = (TextView) getChildAt(index);
            container.setText(null);
        } else {
            container = getEditorTextView();
            addView(container);
        }

        mOperations.get(index).display(container);
    }

    private TextView getEditorTextView() {
        TextView textView = new TextView(getContext());
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
        TextViewCompat.setTextAppearance(textView, R.style.TextAppearance_AppCompat_Medium);
        return textView;
    }
}
