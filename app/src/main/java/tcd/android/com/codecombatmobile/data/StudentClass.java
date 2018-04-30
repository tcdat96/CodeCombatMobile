package tcd.android.com.codecombatmobile.data;

/**
 * Created by ADMIN on 30/04/2018.
 */

public class StudentClass {
    private String mLanguage;
    private String mClassName;
    private String mTeacher;
    private String mCourseName;
    private int mProgress;

    public StudentClass(String language, String className, String teacher, String courseName, int progress) {
        mLanguage = language;
        mClassName = className;
        mTeacher = teacher;
        mCourseName = courseName;
        mProgress = progress;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public String getClassName() {
        return mClassName;
    }

    public String getTeacher() {
        return mTeacher;
    }

    public String getCourseName() {
        return mCourseName;
    }

    public int getProgress() {
        return mProgress;
    }
}
