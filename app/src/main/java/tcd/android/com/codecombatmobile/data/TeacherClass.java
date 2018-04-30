package tcd.android.com.codecombatmobile.data;

/**
 * Created by ADMIN on 30/04/2018.
 */

public class TeacherClass {
    private String mLanguage;
    private String mClassName;
    private int mProgress;
    private int mStudentTotal;
    private int mPlaytimeTotal;
    private int mLevelTotal;

    public TeacherClass(String language, String className, int progress,
                        int studentTotal, int playtimeTotal, int levelTotal) {
        mLanguage = language;
        mClassName = className;
        mProgress = progress;
        mStudentTotal = studentTotal;
        mPlaytimeTotal = playtimeTotal;
        mLevelTotal = levelTotal;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public String getClassName() {
        return mClassName;
    }

    public int getProgress() {
        return mProgress;
    }

    public int getStudentTotal() {
        return mStudentTotal;
    }

    public int getPlaytimeTotal() {
        return mPlaytimeTotal;
    }

    public int getLevelTotal() {
        return mLevelTotal;
    }
}
