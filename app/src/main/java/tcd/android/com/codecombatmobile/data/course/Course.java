package tcd.android.com.codecombatmobile.data.course;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

public class Course {
    private String mId;
    private String mName;
    private String mDescription;
    private String mCampaignId;

    // String for level ID and boolean for level type (true for primary and false for challenge or practice level)
    @Nullable
    private Map<String, Boolean> mLevelTypes;

    public Course(String id, String name, String description, String campaignId) {
        mId = id;
        mName = name;
        mDescription = description;
        mCampaignId = campaignId;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getCampaignId() {
        return mCampaignId;
    }

    @Nullable
    public Map<String, Boolean> getLevelTypes() {
        return mLevelTypes;
    }

    public void setLevelTypes(@NonNull Map<String, Boolean> levelType) {
        mLevelTypes = levelType;
    }
}
