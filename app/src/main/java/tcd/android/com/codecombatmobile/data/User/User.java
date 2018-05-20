package tcd.android.com.codecombatmobile.data.User;

import java.io.Serializable;

public class User implements Serializable {
    private String mId;
    private String mEmail;

    public User(String email) {
        mEmail = email;
    }

    public String getId() {
        return mId;
    }

    public String getEmail() {
        return mEmail;
    }
}
