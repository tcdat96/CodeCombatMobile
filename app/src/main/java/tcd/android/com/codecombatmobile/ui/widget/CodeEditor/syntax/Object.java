package tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax;

import android.support.annotation.NonNull;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Object extends Variable {

    @NonNull
    private List<String> mMethods = new ArrayList<>();

    public Object(@NonNull String name) {
        super(name);
        mChildren.add(new Variable(name));
    }

    public Object(@NonNull String name, @NonNull Method method) {
        this(name);
        mChildren.add(method);
    }

    public void addMethodSignature(@NonNull List<String> methods) {
        mMethods.addAll(new ArrayList<>(methods));
    }

    public void setCurrentMethod(Function method) {
        if (mChildren.size() > 1) {
            mChildren.remove(1);
        }
        mChildren.add(method);
    }

    public Method getCurrentMethod() {
        return (Method) mChildren.get(0);
    }

    @Override
    public boolean replaceOperation(Operation oldOp, Operation newOp) {
        int index = getReplacementIndex(oldOp, newOp);
        if (index >= 0 && newOp instanceof Function) {
            setCurrentMethod((Function) newOp);
            return true;
        }
        return false;
    }

    @Override
    public int getReplacementIndex(Operation oldOp, Operation newOp) {
        // TODO: 13/06/2018 should not depend on container type
        if (newOp instanceof Function && !(mContainer instanceof Assignment)) {
            int index = mChildren.indexOf(oldOp);
            for (String method : mMethods) {
                String[] parts = method.split("/");
                boolean isSameName = newOp.getButtonName().equals(parts[0]);
                boolean isSameParam = ((Function)newOp).getParamTotal() == Integer.valueOf(parts[1]);
                if (isSameName && isSameParam) {
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
