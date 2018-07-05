package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

import android.text.SpannableString;
import android.widget.TextView;

public class Comment extends Operation {
    public Comment() {
        super("Comment", TYPE_COMMENT);

        mSpannable = new SpannableString("#");
        mChildren.add(new UserInput());
    }

    @Override
    public void display(TextView container) {
        container.append(mSpannable);
        container.append(" ");
        mChildren.get(0).display(container);
    }
}
