package tcd.android.com.codecombatmobile.util;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcd.android.com.codecombatmobile.data.course.Level;

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
}
