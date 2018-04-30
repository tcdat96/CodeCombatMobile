package tcd.android.com.codecombatmobile.data.Syntax;

import android.support.annotation.IntDef;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import tcd.android.com.codecombatmobile.data.OperationFactory;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

/**
 * Created by ADMIN on 22/04/2018.
 */

public abstract class Operation {

    public static final int TYPE_CONDITION = 0,
            TYPE_DECLARATION = 1,
            TYPE_VARIABLE = 2,
            TYPE_FUNCTION = 3,
            TYPE_METHOD = 4,
            TYPE_VALUE = 5,
            TYPE_OPERATOR = 6,
            TYPE_BLANK = 7;
    @IntDef({TYPE_CONDITION, TYPE_DECLARATION, TYPE_VARIABLE, TYPE_FUNCTION, TYPE_METHOD, TYPE_VALUE, TYPE_OPERATOR, TYPE_BLANK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SyntaxType {}
    private static OperationFactory mOperationFactory = new OperationFactory();

    protected String mName;
    @SyntaxType
    private int mSyntaxType = TYPE_VALUE;
    protected Spannable mSpannable;

    protected List<Operation> mChildren;
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

    public void setOnClickListener(CodeEditor codeEditor) {

        if (mSpannable != null) {
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
        int index = mChildren.indexOf(oldOp);
        if (index >= 0) {
            if (isNewOpValid(index, newOp)) {
                mChildren.set(index, newOp);
                return true;
            }
        }
        return false;
    }

    protected boolean isNewOpValid(int index, Operation op) {
        return mChildren.get(index).getClass().equals(op.getClass());
    }

    public void removeOperation(Operation op) {
        int index = mChildren.indexOf(op);
        if (index > 0) {
            if (mChildren.get(index) instanceof Blank) {
                // TODO: 28/04/2018 remove container
            } else {
                mChildren.set(index, new Blank());
            }
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
