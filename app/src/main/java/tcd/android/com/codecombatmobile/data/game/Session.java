package tcd.android.com.codecombatmobile.data.game;

public class Session {
    private String mLevelName;
    private String mOriginal;
    private String mLevelId;
    private long mTimeChanged;
    private int mTotalScore;
    private int mPlaytime;
    private boolean mIsCompleted;

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

    public void setLevelId(String levelId) {
        mLevelId = levelId;
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

    public String getLevelName() {
        return mLevelName;
    }

    public String getOriginal() {
        return mOriginal;
    }

    public String getLevelId() {
        return mLevelId;
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
}
