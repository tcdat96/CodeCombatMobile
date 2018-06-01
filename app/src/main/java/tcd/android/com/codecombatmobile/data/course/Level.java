package tcd.android.com.codecombatmobile.data.course;

import android.support.annotation.IntDef;

import java.io.Serializable;

public class Level implements Serializable {
    private String mOriginal;
    private String mName;
    private String mDescription;
    private int mCampaignIndex;
    private Position mPosition;

    public static final int STATE_COMPLETE = 0;
    public static final int STATE_INCOMPLETE = 1;
    public static final int STATE_LOCKED = 2;
    @IntDef ({STATE_COMPLETE, STATE_INCOMPLETE, STATE_LOCKED})
    public @interface LevelState {}
    @LevelState
    private int mLevelState = STATE_LOCKED;

    public Level(String original, String name, String description, int campaignIndex, Position position) {
        mOriginal = original;
        mName = name;
        mDescription = description;
        mCampaignIndex = campaignIndex;
        mPosition = position;
    }

    public String getOriginal() {
        return mOriginal;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getCampaignIndex() {
        return mCampaignIndex;
    }

    public Position getPosition() {
        return mPosition;
    }

    @LevelState
    public int getLevelState() {
        return mLevelState;
    }

    public void setLevelState(@LevelState int state) {
        mLevelState = state;
    }

}