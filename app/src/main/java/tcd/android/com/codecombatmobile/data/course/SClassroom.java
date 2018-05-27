package tcd.android.com.codecombatmobile.data.course;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by ADMIN on 30/04/2018.
 */

public class SClassroom implements Serializable {
    private String mId;
    private String mLanguage;
    private String mClassName;
    private String mTeacher;
    private String mCourseName;
    private String mCampaignId;
    private String mInstanceId;
    private int mProgress;

    public SClassroom(String id, String language, String className, String teacher) {
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

    public String getCampaignId() {
        return mCampaignId;
    }

    public String getInstanceId() {
        return mInstanceId;
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

    public void setCampaignId(@NonNull String campaignId) {
        mCampaignId = campaignId;
    }

    public void setInstanceId(@NonNull String instanceId) {
        mInstanceId = instanceId;
    }
}
