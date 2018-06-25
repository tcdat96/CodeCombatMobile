package tcd.android.com.codecombatmobile.data.course;

import java.io.Serializable;

/**
 * Created by ADMIN on 30/04/2018.
 */

public class TClassroom implements Serializable {
    private String mId;
    private String mLanguage;
    private String mClassName;
    private String mCode;
    private int mMemberTotal;
    private long mDateCreated;
    private int mProgress;

    public TClassroom(String id, String language, String className, String code, int memberTotal, long dateCreated, int progress) {
        mId = id;
        mLanguage = language;
        mClassName = className;
        mCode = code;
        mMemberTotal = memberTotal;
        mDateCreated = dateCreated;
        mProgress = progress;
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

    public String getClassCode() {
        return mCode;
    }

    public int getMemberTotal() {
        return mMemberTotal;
    }

    public long getDateCreated() {
        return mDateCreated;
    }

    public int getProgress() {
        return mProgress;
    }
}
