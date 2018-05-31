package tcd.android.com.codecombatmobile.data.thang;

import android.graphics.Bitmap;

public class ThangType {
    private String mOriginal;
    private String mKind;
    private String mRaw;
    private String mImagePath;
    private int mWidth, mHeight;

    private Bitmap mBitmap;

    public ThangType(String original, String kind) {
        mOriginal = original;
        mKind = kind;
    }

    public void setImage(String imagePath) {
        mImagePath = imagePath;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public String getOriginal() {
        return mOriginal;
    }

    public String getKind() {
        return mKind;
    }

    public String getRaw() {
        return mRaw;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
