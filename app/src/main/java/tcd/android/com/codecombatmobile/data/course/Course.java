package tcd.android.com.codecombatmobile.data.course;

import android.support.annotation.NonNull;

import java.util.Map;

public class Course {
    private String mId;
    private String mName;
    private String mDescription;
    private Map<String, String> mLevels;

    public Course(String id, String name, String description) {
        this.mId = id;
        this.mName = name;
        this.mDescription = description;
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

    public Map<String, String> getLevels() {
        return mLevels;
    }

    public void setLevels(@NonNull Map<String, String> levels) {
        mLevels = levels;
    }
}
