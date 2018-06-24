package tcd.android.com.codecombatmobile.data.game;

public class Achievement {
    private String mName;
    private long mLastEarned;
    private int mEarnedGems;

    public Achievement(String name, long lastEarned, int earnedGems) {
        mName = name;
        mLastEarned = lastEarned;
        mEarnedGems = earnedGems;
    }

    public String getName() {
        return mName;
    }

    public long getLastEarned() {
        return mLastEarned;
    }

    public int getEarnedGems() {
        return mEarnedGems;
    }
}
