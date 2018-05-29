package tcd.android.com.codecombatmobile.data.syntax;

import android.support.annotation.Nullable;
import android.widget.TextView;

/**
 * Created by ADMIN on 23/04/2018.
 */

public class SimpleExpression extends Operation {

    private Operation mLhs, mRhs = null;
    @Nullable
    private Operator mOperator = null;

    public SimpleExpression() {
        super("Expression", TYPE_OPERATOR);
        mLhs = new Blank();

        mReturnsValue = true;
    }

    @Override
    public void display(TextView container) {
        mLhs.display(container);
        if (mOperator != null) {
            mOperator.display(container);
            mRhs.display(container);
        }
    }
}
