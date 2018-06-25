package tcd.android.com.codecombatmobile.data.game;

public class Session {
    private String mLevelName;
    private String mOriginal;
    private long mTimeChanged;
    private int mTotalScore;
    private int mPlaytime;
    private boolean mIsCompleted;
    private String mCreator;

    public Session(boolean isCompleted) {
        mIsCompleted = isCompleted;
        mTotalScore = 0;
    }

    public void setLevelName(String levelName) {
        mLevelName = levelName;
    }

    public void setOriginal(String original) {
        mOriginal = original;
    }

    public void setTimeChanged(long timeChanged) {
        mTimeChanged = timeChanged;
    }

    public void setPlaytime(int playtime) {
        mPlaytime = playtime;
    }

    public void setTotalScore(int totalScore) {
        mTotalScore = totalScore;
    }

    public void setCreator(String creator) {
        mCreator = creator;
    }

    public String getLevelName() {
        return mLevelName;
    }

    public String getOriginal() {
        return mOriginal;
    }

    public long getTimeChanged() {
        return mTimeChanged;
    }

    public int getTotalScore() {
        return mTotalScore;
    }

    public int getPlaytime() {
        return mPlaytime;
    }

    public boolean isCompleted() {
        return mIsCompleted;
    }

    public String getCreator() {
        return mCreator;
    }
}
