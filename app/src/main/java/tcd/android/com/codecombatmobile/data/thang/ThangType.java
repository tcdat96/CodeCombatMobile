package tcd.android.com.codecombatmobile.data.thang;

import android.graphics.Bitmap;

public class ThangType {
    private String mOriginal;
    private String mKind;
    private String mRaw;
    private String mImagePath;

    private Bitmap mBitmap;

    public ThangType(String original, String kind) {
        this.mOriginal = original;
        this.mKind = kind;
    }

    public void setImage(String imagePath) {
        this.mImagePath = imagePath;
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

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
