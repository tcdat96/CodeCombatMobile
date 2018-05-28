package tcd.android.com.codecombatmobile.data.thang;

import tcd.android.com.codecombatmobile.data.course.Position;

public class Thang {
    private String mThangType;
    private Position mPosition;
    private float mWidth, mHeight;
    private float mRotation;

    public Thang(String thangType, Position position) {
        mThangType = thangType;
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
