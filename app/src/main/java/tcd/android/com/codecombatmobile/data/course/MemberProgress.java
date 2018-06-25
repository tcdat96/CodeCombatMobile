package tcd.android.com.codecombatmobile.data.course;

import java.io.Serializable;

/**
 * Created by ADMIN on 02/05/2018.
 */

public class MemberProgress implements Serializable {
    private String mId;
    private String mName;
    private String mEmail;
    private int mCompletedLevels = 0;

    public MemberProgress(String id) {
        mId = id;
    }

    public MemberProgress(String id, String name, String email) {
        this(id);
        mName = name;
        mEmail = email;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void setCompletedLevels(int completedLevels) {
        mCompletedLevels = completedLevels;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public int getCompletedLevels() {
        return mCompletedLevels;
    }
}
