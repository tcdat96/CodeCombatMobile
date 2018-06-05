package tcd.android.com.codecombatmobile.data.syntax;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import tcd.android.com.codecombatmobile.ui.widget.CodeEditor;
import tcd.android.com.codecombatmobile.util.DataUtil;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

/**
 * Created by ADMIN on 22/04/2018.
 */

public abstract class Operation {

    protected static final String DEFAULT_INDENT = "    ";

    public static final int TYPE_FLOW_CONTROL = 0,
            TYPE_DECLARATION = 1,
            TYPE_VARIABLE = 2,
            TYPE_FUNCTION = 3,
            TYPE_METHOD = 4,
            TYPE_VALUE = 5,
            TYPE_ASSIGNMENT = 6,
            TYPE_OPERATOR = 7,
            TYPE_BLANK = 8;
    @IntDef({TYPE_FLOW_CONTROL, TYPE_DECLARATION, TYPE_VARIABLE, TYPE_FUNCTION, TYPE_METHOD, TYPE_VALUE, TYPE_ASSIGNMENT, TYPE_OPERATOR, TYPE_BLANK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SyntaxType {}

    protected String mName;
    @SyntaxType
    private int mSyntaxType = TYPE_VALUE;
    protected Spannable mSpannable;
    protected boolean mReturnsValue = false;

    @NonNull
    protected List<Operation> mChildren;
    private CodeEditor mCodeEditor;
    protected Operation mContainer;

    public Operation(String name, @SyntaxType int syntaxType) {
        mName = name;
        mSyntaxType = syntaxType;
        mChildren = new ArrayList<>();
    }

    public int getColor() {
        return DisplayUtil.getColor(mSyntaxType);
    }

    protected void setSpannableColor() {
        mSpannable.setSpan(new ForegroundColorSpan(getColor()), 0, mSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public Operation getContainer() {
        return mContainer;
    }

    public boolean returnsValue() {
        return mReturnsValue;
    }

    public void setOnClickListener(CodeEditor codeEditor) {
        mCodeEditor = codeEditor;

        if (mSpannable != null) {
            DataUtil.removeAllSpans(mSpannable);
            ClickableElement element = new ClickableElement(codeEditor, this);
            mSpannable.setSpan(element, 0, mSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            setSpannableColor();
        }

        for (Operation child : mChildren) {
            child.setOnClickListener(codeEditor);
            child.mContainer = this;
        }
    }

    public int findOperation(Operation operation) {
        return mChildren.indexOf(operation);
    }

    public boolean replaceOperation(Operation oldOp, Operation newOp) {
        int index = mChildren.indexOf(oldOp);
        if (index >= 0) {
            if (isNewOpValid(index, newOp)) {
                mChildren.set(index, newOp);
                return true;
            }
        }
        return false;
    }

    public boolean isNewOpValid(int index, Operation op) {
        return mChildren.get(index).getClass().equals(op.getClass());
    }

    public void removeOperation(Operation op) {
        int index = mChildren.indexOf(op);
        if (index >= 0) {
            if (mChildren.get(index) instanceof Blank) {
                removeFromContainer();
            } else {
                mChildren.set(index, new Blank());
                setOnClickListener(mCodeEditor);
            }
        }
    }

    protected void removeFromContainer() {
        if (mContainer != null) {
            mContainer.removeOperation(this);
        } else {
            mCodeEditor.setSelectedOperation(this);
            mCodeEditor.removeOperation();
        }
    }

    public String getButtonName() {
        return mName;
    }

    public void display(TextView container) {
        for (Operation child : mChildren) {
            child.display(container);
        }
    }
}
