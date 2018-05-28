package tcd.android.com.codecombatmobile.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tcd.android.com.codecombatmobile.data.Thang;
import tcd.android.com.codecombatmobile.data.ThangType;
import tcd.android.com.codecombatmobile.data.course.Level;
import tcd.android.com.codecombatmobile.data.course.Position;

public class CCDataUtil {

    public static List<String> getLevelSessions(@NonNull JSONArray sessionArr) throws JSONException {
        List<String> sessions = new ArrayList<>();
        for (int i = 0; i < sessionArr.length(); i++) {
            JSONObject sessionObj = sessionArr.getJSONObject(i);
            String original = sessionObj.getJSONObject("level").getString("original");
            sessions.add(original);

            boolean isComplete = sessionObj.getJSONObject("state").getBoolean("complete");
            if (!isComplete) {
                break;
            }
        }
        return sessions;
    }

    @NonNull
    public static List<Thang> parseThangArr(@NonNull JSONArray thangArr) throws JSONException {
        List<Thang> thangs = new ArrayList<>(thangArr.length());
        for (int i = 0; i < thangArr.length(); i++) {
            JSONObject thangObj = thangArr.getJSONObject(i);
            String thangType = thangObj.getString("thangType");

            JSONArray compArr = thangObj.getJSONArray("components");
            JSONObject configObj = compArr.getJSONObject(1).getJSONObject("config");
            // position
            JSONObject posObj = configObj.getJSONObject("pos");
            float x = (float)posObj.getDouble("x");
            float y = (float)posObj.getDouble("y");
            Position pos = new Position(x, y);

            Thang thang = new Thang(thangType, pos);

            // width
            if (configObj.has("width")) {
                int width = configObj.getInt("width");
                thang.setWidth(width);
            }
            // height
            if (configObj.has("height")) {
                int height = configObj.getInt("height");
                thang.setHeight(height);
            }
            // rotation
            if (configObj.has("rotation")) {
                float rotation = (float) configObj.getDouble("rotation");
                thang.setRotation(rotation);
            }

            thangs.add(thang);
        }
        return thangs;
    }

    @NonNull
    public static Set<String> getThangTypeSet(@NonNull List<Thang> thangs) {
        Set<String> types = new HashSet<>(thangs.size());
        for (Thang thang : thangs) {
            types.add(thang.getThangType());
        }
        return types;
    }

    public static List<String> getThangTypeIds(@NonNull JSONArray thangTypeArr) throws JSONException {
        List<String> typeIds = new ArrayList<>(thangTypeArr.length());
        for (int i = 0; i < thangTypeArr.length(); i++) {
            String id = thangTypeArr.getJSONObject(i).getString("_id");
            typeIds.add(id);
        }
        return typeIds;
    }

    public static ThangType getThangType(@NonNull JSONObject thangTypeObj) throws JSONException {
        String original = thangTypeObj.getString("original");
        String kind = thangTypeObj.getString("kind");
        ThangType thangType = new ThangType(original, kind);

        // image (if exists)
        if (thangTypeObj.has("raster")) {
            String image = thangTypeObj.getString("raster");
            thangType.setImage(image);
        } else if (thangTypeObj.has("prerenderedSpriteSheetData")) {
            JSONObject prerenderedData = thangTypeObj.getJSONArray("prerenderedSpriteSheetData").getJSONObject(0);
            if (prerenderedData.has("image")) {
                String image = thangTypeObj.getString("image");
                thangType.setImage(image);
            }
        }

        // TODO: 28/05/2018 get raw

        return thangType;
    }
}
