package tcd.android.com.codecombatmobile.data.level;

import tcd.android.com.codecombatmobile.data.Position;

public class Thang {
    private String mId;
    private String mThangType;
    private Position mPosition;
    private float mWidth, mHeight;
    private float mRotation;

    public Thang(String id, String thangType, Position position) {
        mId = id;
        mThangType = thangType;
        mPosition = position;
    }

    public void setPosition(Position position) {
        mPosition = position;
    }

    public void setWidth(float width) {
        mWidth = width;
    }

    public void setHeight(float height) {
        mHeight = height;
    }

    public void setRotation(float rotation) {
        mRotation = rotation;
    }

    public String getId() {
        return mId;
    }

    public String getThangType() {
        return mThangType;
    }

    public Position getPosition() {
        return mPosition;
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public float getRotation() {
        return mRotation;
    }
}
