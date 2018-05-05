package tcd.android.com.codecombatmobile.data;

import java.io.Serializable;

/**
 * Created by ADMIN on 02/05/2018.
 */

public class ClassStudent implements Serializable {
    private String mName;
    private String mEmail;
    private int mProgress;
    private String mLatestLevel;

    public ClassStudent(String name, String email, int progress, String latestLevel) {
        mName = name;
        mEmail = email;
        mProgress = progress;
        mLatestLevel = latestLevel;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public int getProgress() {
        return mProgress;
    }

    public String getLatestLevel() { return mLatestLevel; }
}
