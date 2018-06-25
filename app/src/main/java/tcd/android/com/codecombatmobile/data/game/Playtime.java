package tcd.android.com.codecombatmobile.data.game;

import java.text.DecimalFormat;

public class Playtime {
    private float mValue;
    private String mUnit;

    public Playtime(float millis, String unit) {
        mValue = millis;
        mUnit = unit;
    }

    public float getValue() {
        return mValue;
    }

    public String getUnit() {
        return mUnit;
    }

    public String getCompactTime() {
        return new DecimalFormat("#.#").format(mValue) + mUnit.substring(0, 1);
    }
}
