package tcd.android.com.codecombatmobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ADMIN on 30/04/2018.
 */

public class TeacherClass implements Serializable {
    private String mLanguage;
    private String mClassName;
    private int mProgress;
    private String mDateCreated;
    private int mStudentTotal;
    private int mPlaytimeTotal;
    private int mLevelTotal;
    private List<ClassStudent> mStudents;

    public TeacherClass(String language, String className, int progress, String dateCreated,
                        int studentTotal, int playtimeTotal, int levelTotal) {
        mLanguage = language;
        mClassName = className;
        mProgress = progress;
        mDateCreated = dateCreated;
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

    public String getDateCreated() {
        return mDateCreated;
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

    @Nullable
    public List<ClassStudent> getStudents() {
        return mStudents;
    }

    public void setStudentList(@NonNull List<ClassStudent> studentList) {
        mStudents = studentList;
    }
}
