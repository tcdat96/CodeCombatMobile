package tcd.android.com.codecombatmobile.data.syntax;

import android.support.v4.content.ContextCompat;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.View;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor;

/**
 * Created by ADMIN on 25/04/2018.
 */

public class ClickableElement extends ClickableSpan {

    private static BackgroundColorSpan sBgrColorSpan = null;

    private CodeEditor mCodeEditor;
    private Operation mOperation;

    public ClickableElement(CodeEditor editor, Operation operation) {
        mCodeEditor = editor;
        mOperation = operation;

        if (sBgrColorSpan == null) {
            int bgrColor = ContextCompat.getColor(editor.getContext(), R.color.select_background_color);
            sBgrColorSpan = new BackgroundColorSpan(bgrColor);
        }
    }

    @Override
    public void onClick(View widget) {
        // remove background selection
        Operation selectedOperation = mCodeEditor.getSelectedOperation();
        if (selectedOperation != null) {
            selectedOperation.mSpannable.removeSpan(sBgrColorSpan);
        }

        // set new selected operation
        mOperation.mSpannable.setSpan(sBgrColorSpan, 0, mOperation.mSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mCodeEditor.setSelectedOperation(mOperation);
        mCodeEditor.display();
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(mOperation.getColor());
        ds.setUnderlineText(false);
    }
}
