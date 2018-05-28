package tcd.android.com.codecombatmobile.data.course;

import java.io.Serializable;

public class Level implements Serializable {
    private String mId;
    private String mName;
    private String mDescription;
    private String mSlug;
    private String[] mConcepts;
    private Position mPosition;
    private boolean mIsComplete;

    public Level(String id, String name, String description, String slug, String[] concepts, Position position) {
        mId = id;
        mName = name;
        mDescription = description;
        mSlug = slug;
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

    public String getSlug() {
        return mSlug;
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