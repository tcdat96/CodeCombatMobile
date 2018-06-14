package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

import android.support.annotation.NonNull;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Object extends Variable {

    @NonNull
    private List<Function> mMethods = new ArrayList<>();

    public Object(@NonNull String name) {
        super(name);
        mChildren.add(new Variable(name));
    }


    public void addMethods(@NonNull List<Function> methods) {
        mMethods.addAll(new ArrayList<>(methods));
    }

    @NonNull
    public List<Function> getMethods() {
        return mMethods;
    }

    private void setMethod(Function method) {
        if (mChildren.size() > 1) {
            mChildren.remove(1);
        }
        mChildren.add(method);
    }


    @Override
    public boolean replaceOperation(Operation oldOp, Operation newOp) {
        int index = getReplacementIndex(oldOp, newOp);
        if (index >= 0 && newOp instanceof Function) {
            setMethod((Function) newOp);
            return true;
        }
        return false;
    }

    @Override
    public int getReplacementIndex(Operation oldOp, Operation newOp) {
        // TODO: 13/06/2018 should not depend on container type
        if (newOp instanceof Function && !(mContainer instanceof Assignment)) {
            int index = mChildren.indexOf(oldOp);
            for (Function method : mMethods) {
                if (newOp.equals(method)) {
                    return index;
                }
            }
        }
        return -1;
    }

    @Override
    public void display(TextView container) {
        mChildren.get(0).display(container);
        if (mChildren.size() > 1) {
            mChildren.get(1).display(container);
        }
    }
}
