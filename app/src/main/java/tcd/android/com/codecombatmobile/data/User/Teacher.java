package tcd.android.com.codecombatmobile.data.User;

public class Teacher extends User {
    private String mFirstName;
    private String mLastName;
    private String mOrganization;
    private String mCountry;
    private String mPhoneNumber;
    private String mRole;
    private String mEstimatedStudent;


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

    public String getOrganization() {
        return mOrganization;
    }

    public String getCountry() {
        return mCountry;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getRole() {
        return mRole;
    }

    public String getEstimatedStudent() {
        return mEstimatedStudent;
    }

    public void update(String organization, String country, String phoneNumber, String role, String estimatedStudents) {
        mOrganization = organization;
        mCountry = country;
        mPhoneNumber = phoneNumber;
        mRole = role;
        mEstimatedStudent = estimatedStudents;
    }
}
