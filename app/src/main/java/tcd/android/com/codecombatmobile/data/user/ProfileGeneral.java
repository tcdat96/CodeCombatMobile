package tcd.android.com.codecombatmobile.data.user;

import java.io.Serializable;

public class ProfileGeneral implements Serializable{
    private String mHeroPictureUrl;
    private String mName;
    private int mLevel;
    private int mPlaytime;
    private int mCourseTotal;
    private int mAchievementTotal;
    private int mSingleplayer, mMultiplayer;

    public ProfileGeneral(String name, int level, int totalPlaytime, int courseTotal) {
        mName = name;
        mLevel = level;
        mPlaytime = totalPlaytime;
        mCourseTotal = courseTotal;
    }

    public void setHeroPictureUrl(String url) {
        mHeroPictureUrl = url;
    }
    
    public void setCompletedLevels(int singleplayerCount, int multiplayerCount) {
        mSingleplayer = singleplayerCount;
        mMultiplayer = multiplayerCount;
    }

    public void setAchievementTotal(int achievementTotal) {
        mAchievementTotal = achievementTotal;
    }

    public void setPlaytime(int playtime) {
        mPlaytime = playtime;
    }

    public String getHeroPictureUrl() {
        return mHeroPictureUrl;
    }

    public String getUsername() {
        return mName;
    }

    public int getLevel() {
        return mLevel;
    }

    public int getPlaytime() {
        return mPlaytime;
    }

    public int getCourseTotal() {
        return mCourseTotal;
    }

    public int getAchievementTotal() {
        return mAchievementTotal;
    }

    public int getSingleplayer() {
        return mSingleplayer;
    }

    public int getMultiplayer() {
        return mMultiplayer;
    }
}
