package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

import android.content.Context;
import android.text.SpannableString;
import android.widget.TextView;

public class UserInput extends Operation {

    private int mTop, mLeft;

    public UserInput() {
        super(BLANK_VALUE, TYPE_VALUE);
        mSpannable = new SpannableString(mName);

        mIsStatement = false;
        mReturnsValue = true;
    }

    @Override
    public String getButtonName() {
        return super.getButtonName().equals(BLANK_VALUE) ? "" : super.getButtonName();
    }

    @Override
    public void init(Context context) {
        super.init(context);
        if (mContainer != null) {
            mCodeColor = mContainer.getCodeColor();
        }
        setSpannableColor();
    }

    public void setName(String name) {
        mName = name;
        mSpannable = new SpannableString(name);
        setSpannableColor();
        setOnClickListener(mCodeEditor);
    }

    public boolean isVarDeclaration() {
        return mContainer != null && mContainer instanceof VarDeclaration && mContainer.mChildren.indexOf(this) == 0;
    }

    public void reset() {
        setName(BLANK_VALUE);
    }

    public int getTop() {
        return mTop;
    }

    public int getLeft() {
        return mLeft;
    }

    @Override
    public void display(TextView container) {
        if (mTop == 0) {
            mTop = container.getTop();
            mLeft = container.getLeft() + (int)container.getPaint().measureText(container.getText().toString());
        }

        container.append(mSpannable);
    }
}
