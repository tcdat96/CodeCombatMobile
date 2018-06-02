package tcd.android.com.codecombatmobile.data.level;

import android.support.annotation.NonNull;

import java.util.List;

public class Goal {
    private String mName;
    private boolean mIsCompleted = false;
    private List<String> mCollectThangs;

    public Goal(String name) {
        mName = name;
    }

    public void setCompleteState(boolean isCompleted) {
        mIsCompleted = isCompleted;
    }

    public void setCollectThangs(@NonNull List<String> collectThangs) {
        mCollectThangs = collectThangs;
    }

    public String getName() {
        return mName;
    }

    public boolean isCompleted() {
        return mIsCompleted;
    }

    public List<String> getCollectThangs() {
        return mCollectThangs;
    }
}
