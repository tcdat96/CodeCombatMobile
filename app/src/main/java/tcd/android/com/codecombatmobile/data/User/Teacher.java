package tcd.android.com.codecombatmobile.data.User;

public class Teacher extends User {
    private String mFirstName;
    private String mLastName;

    public Teacher(String email, String firstName, String lastName) {
        super(email);
        mFirstName = firstName;
        mLastName = lastName;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }
}
