package tcd.android.com.codecombatmobile.data;

/**
 * Created by ADMIN on 30/04/2018.
 */

public class StudentClass {
    private String mId;
    private String mLanguage;
    private String mClassName;
    private String mTeacher;
    private String mCourseName;
    private int mProgress;

    public StudentClass(String id, String language, String className, String teacher) {
        mId = id;
        mLanguage = language;
        mClassName = className;
        mTeacher = teacher;
    }

    public String getId() {
        return mId;
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

    public void setCourseName(String courseName) {
        mCourseName = courseName;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }
}
