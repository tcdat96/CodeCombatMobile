package tcd.android.com.codecombatmobile.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
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

    public void clear() {
        removeAllViews();
        mSelectedOperation = null;
        mOperations.clear();
        display();
    }

    public void addOperation(@NonNull Operation newOp) {
        newOp.setOnClickListener(this);

        if (mSelectedOperation == null) {
            mOperations.add(newOp);
        } else {
            Operation container = mSelectedOperation.getContainer();
            if (container == null) {
                int index = mOperations.indexOf(mSelectedOperation);
                mOperations.set(index, newOp);
            } else {
                boolean result = container.replaceOperation(mSelectedOperation, newOp);
                if (result) {
                    container.setOnClickListener(this);
                    // TODO: 25/04/2018 reconsider this
                    mSelectedOperation = null;
                } else {
                    Toast.makeText(getContext(), R.string.cannot_insert_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        display();
    }

    public void removeOperation() {
        if (mSelectedOperation == null) {
            return;
        }

        Operation container = mSelectedOperation.getContainer();
        if (container == null) {
            mOperations.remove(mSelectedOperation);
        } else {
            container.removeOperation(mSelectedOperation);
            container.setOnClickListener(this);
        }
        mSelectedOperation = null;
        display();
    }

    public void setSelectedOperation(Operation operation) {
        mSelectedOperation = operation;
    }

    public Operation getSelectedOperation() {
        return mSelectedOperation;
    }

    public void display() {
        // clear current content
        for (int i = 0; i < getChildCount(); i++) {
            ((TextView)getChildAt(i)).setText(null);
        }

        TextView container;
        for (int i = 0; i < mOperations.size(); i++) {
            if (i < getChildCount()) {
                container = (TextView) getChildAt(i);
                container.setText(null);
            } else {
                container = getEditorTextView();
                addView(container);
            }
            mOperations.get(i).display(container);
        }
    }

    private TextView getEditorTextView() {
        TextView textView = new TextView(getContext());
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
        TextViewCompat.setTextAppearance(textView, R.style.TextAppearance_AppCompat_Medium);
        return textView;
    }
}
