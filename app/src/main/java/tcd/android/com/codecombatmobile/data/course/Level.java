package tcd.android.com.codecombatmobile.data.course;

public class Level {
    private String mId;
    private String mName;
    private String mDescription;
    private String[] mConcepts;
    private Position mPosition;
    private boolean mIsComplete;

    public Level(String id, String name, String description, String[] concepts, Position position) {
        mId = id;
        mName = name;
        mDescription = description;
        mConcepts = concepts;
        mPosition = position;
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

    public String[] getConcepts() {
        return mConcepts;
    }

    public Position getPosition() {
        return mPosition;
    }

    public boolean isComplete() {
        return mIsComplete;
    }

    public void setComplete(boolean isComplete) {
        mIsComplete = isComplete;
    }

}