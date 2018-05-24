package tcd.android.com.codecombatmobile.data.user;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable {
    private String mId;
    private String mEmail;

    public User(String email) {
        mEmail = email;
    }

    public void setId(@NonNull String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public String getEmail() {
        return mEmail;
    }
}
