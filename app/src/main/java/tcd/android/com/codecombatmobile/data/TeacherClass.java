package tcd.android.com.codecombatmobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 30/04/2018.
 */

public class TeacherClass implements Serializable {
    private String mLanguage;
    private String mClassName;
    private String mCode;
    private int mProgress;
    private int mStudentTotal;
    private String mDateCreated;
    private int mPlaytimeTotal;
    private int mLevelTotal;
    @NonNull
    private List<ClassStudent> mStudents = new ArrayList<>();

    public TeacherClass(String language, String className, String code, int studentTotal, int progress) {
        mLanguage = language;
        mClassName = className;
        mCode = code;
        mStudentTotal = studentTotal;
        mProgress = progress;
    }

    public TeacherClass(String language, String className, String code, int progress, int studentTotal,
                        String dateCreated, int playtimeTotal, int levelTotal) {
        this(language, className, code, studentTotal, progress);
        mDateCreated = dateCreated;
        mPlaytimeTotal = playtimeTotal;
        mLevelTotal = levelTotal;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public String getClassName() {
        return mClassName;
    }

    public String getClassCode() {
        return mCode;
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

    @NonNull
    public List<ClassStudent> getStudents() {
        return mStudents;
    }

    public void setStudentList(@NonNull List<ClassStudent> studentList) {
        mStudents = studentList;
    }
}
