package tcd.android.com.codecombatmobile.data.course;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 30/04/2018.
 */

public class TClassroom implements Serializable {
    private String mLanguage;
    private String mClassName;
    private String mCode;
    private int mProgress;
    private int mStudentTotal;
    private String mDateCreated;
    private int mPlaytimeTotal;
    private int mLevelTotal;
    @NonNull
    private List<CourseProgress> mStudents = new ArrayList<>();

    public TClassroom(String language, String className, String code, int studentTotal, int progress) {
        mLanguage = language;
        mClassName = className;
        mCode = code;
        mStudentTotal = studentTotal;
        mProgress = progress;
    }

    public TClassroom(String language, String className, String code, int progress, int studentTotal,
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
    public List<CourseProgress> getStudents() {
        return mStudents;
    }

    public void setStudentList(@NonNull List<CourseProgress> studentList) {
        mStudents = studentList;
    }
}
