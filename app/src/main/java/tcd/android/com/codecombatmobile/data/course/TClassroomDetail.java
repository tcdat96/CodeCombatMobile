package tcd.android.com.codecombatmobile.data.course;

import android.support.annotation.Nullable;

public class TClassroomDetail {
    @Nullable
    private MemberProgress[] mMembers;
    private int mPlaytimeTotal;
    private int mLevelTotal;
    private int mCompletedLevelTotal;
    private int mAvgCompletedLevelTotal;

    public TClassroomDetail(@Nullable MemberProgress[] members, int playtime, int levelTotal, int completedLevelTotal, int avgCompletedLevelTotal) {
        mMembers = members;
        mPlaytimeTotal = playtime;
        mLevelTotal = levelTotal;
        mCompletedLevelTotal = completedLevelTotal;
        mAvgCompletedLevelTotal = avgCompletedLevelTotal;
    }

    @Nullable
    public MemberProgress[] getMembers() {
        return mMembers;
    }

    public int getMemberTotal() {
        return mMembers != null ? mMembers.length : 0;
    }

    public int getPlaytime() {
        return mPlaytimeTotal;
    }

    public int getLevelTotal() {
        return mLevelTotal;
    }

    public int getCompletedLevelTotal() {
        return mCompletedLevelTotal;
    }

    public int getAvgCompletedLevelTotal() {
        return mAvgCompletedLevelTotal;
    }
}
