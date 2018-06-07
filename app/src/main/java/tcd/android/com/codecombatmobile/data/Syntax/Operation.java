package tcd.android.com.codecombatmobile.data.syntax;

import android.content.Context;
import android.graphics.Color;
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
    private int mSyntaxType;
    protected Spannable mSpannable;
    protected boolean mReturnsValue = false;
    private int mButtonColor = Color.BLACK;
    private int mCodeColor = Color.BLACK;

    @NonNull
    protected List<Operation> mChildren;
    private CodeEditor mCodeEditor;
    protected Operation mContainer;



    public Operation(String name, @SyntaxType int syntaxType) {
        mName = name;
        mSyntaxType = syntaxType;
        mChildren = new ArrayList<>();
    }

    public void init(Context context) {
        mButtonColor = DisplayUtil.getButtonColor(context, mSyntaxType);
        mCodeColor = DisplayUtil.getCodeColor(context, mSyntaxType);
        setSpannableColor();
    }

    protected void setSpannableColor() {
        if (mSpannable != null) {
            mSpannable.setSpan(new ForegroundColorSpan(mCodeColor), 0, mSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }



    public String getButtonName() {
        return mName;
    }

    public Operation getContainer() {
        return mContainer;
    }

    public Operation getRoot() {
        Operation iterator = this;
        while (iterator.mContainer != null) {
            iterator = iterator.mContainer;
        }
        return iterator;
    }

    public boolean returnsValue() {
        return mReturnsValue;
    }

    public int getButtonColor() {
        return mButtonColor;
    }

    public int getCodeColor() {
        return mCodeColor;
    }



    public void setOnClickListener(CodeEditor codeEditor) {
        mCodeEditor = codeEditor;
        init(codeEditor.getContext());

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

    public boolean replaceOperation(Operation oldOp, Operation newOp) {
        int index = getReplacementIndex(oldOp, newOp);
        if (index >= 0) {
            mChildren.set(index, newOp);
            return true;
        }
        return false;
    }

    public int getReplacementIndex(Operation oldOp, Operation newOp) {
        int index = mChildren.indexOf(oldOp);
        if (index >= 0) {
            if (oldOp.getClass().equals(newOp.getClass())) {
                return index;
            }
        }
        return -1;
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

    public void display(TextView container) {
        for (Operation child : mChildren) {
            child.display(container);
        }
    }
}
