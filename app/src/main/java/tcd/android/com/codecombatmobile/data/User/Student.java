package tcd.android.com.codecombatmobile.data.user;

public class Student extends User {
    private String mUsername;

    public Student(String email, String username) {
        super(email);
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }
}
