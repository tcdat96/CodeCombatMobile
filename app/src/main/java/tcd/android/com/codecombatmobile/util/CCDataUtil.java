package tcd.android.com.codecombatmobile.util;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import tcd.android.com.codecombatmobile.data.course.Course;
import tcd.android.com.codecombatmobile.data.level.Level;
import tcd.android.com.codecombatmobile.data.Position;
import tcd.android.com.codecombatmobile.data.course.TClassroom;
import tcd.android.com.codecombatmobile.data.level.Goal;
import tcd.android.com.codecombatmobile.data.level.Thang;
import tcd.android.com.codecombatmobile.data.level.ThangType;

public class CCDataUtil {

    // teacher classroom list
    public static List<TClassroom> getTClassroomList(@NonNull JSONArray response) {
        List<TClassroom> classrooms = new ArrayList<>(response.length());
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject classObj = response.getJSONObject(i);
                TClassroom classroom = getTClassroom(classObj);
                classrooms.add(classroom);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return classrooms;
    }

    private static TClassroom getTClassroom(JSONObject classObj) throws JSONException {
        String name = classObj.getString("name");
        String code = classObj.getString("codeCamel");

        // language
        JSONObject aceConfig = classObj.getJSONObject("aceConfig");
        String language = aceConfig != null ? aceConfig.getString("language") : "python";

        // number of students
        JSONArray members = classObj.getJSONArray("members");
        int studentTotal = members != null ? members.length() : 0;

        // TODO: 20/05/2018 do something with this
        int progress = new Random().nextInt(100);

        return new TClassroom(language, name, code, studentTotal, progress);
    }

    // student classroom list
    public static List<Course> parseCourses(JSONArray coursesJsonArr) throws JSONException {
        int length = coursesJsonArr.length();
        List<Course> courses = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            JSONObject courseObj = coursesJsonArr.getJSONObject(i);
            String id = courseObj.getString("_id");
            String name = courseObj.getString("name");
            String description = courseObj.getString("description");
            String campaignId = courseObj.getString("campaignID");

            Course newCourse = new Course(id, name, description, campaignId);
            courses.add(newCourse);
        }
        return courses;
    }

    public static boolean isPrimaryLevel(JSONObject levelObj) throws JSONException {
        return !levelObj.has("assessment")
                && (!levelObj.has("practice") || !levelObj.getBoolean("practice"))
                && !levelObj.getString("type").equals("course-ladder");
    }

    // game map
    public static Level getLevel(JSONObject levelObj) throws JSONException {
        String id = levelObj.getString("original");
        String name = levelObj.getString("name");
        String description = levelObj.getString("description");
        int campaignIndex = levelObj.getInt("campaignIndex");
        // practice
        boolean isPrimary = isPrimaryLevel(levelObj);
        // position
        JSONObject posObj = levelObj.getJSONObject("position");
        float x = (float) posObj.getDouble("x");
        float y = (float) posObj.getDouble("y");
        Position position = new Position(x, y);

        return new Level(id, name, description, campaignIndex, isPrimary, position);
    }

    @NonNull
    public static Map<String, Boolean> getLevelSessions(@NonNull JSONArray sessionArr) throws JSONException {
        Map<String, Boolean>  sessions = new HashMap<>();
        for (int i = 0; i < sessionArr.length(); i++) {
            JSONObject sessionObj = sessionArr.getJSONObject(i);
            String original = sessionObj.getJSONObject("level").getString("original");
            boolean isComplete = sessionObj.getJSONObject("state").getBoolean("complete");
            sessions.put(original, isComplete);
        }
        return sessions;
    }

    // game level
    @NonNull
    public static List<Goal> parseGoalArr(@NonNull JSONArray goalArr) throws JSONException {
        List<Goal> goals = new ArrayList<>(goalArr.length());
        for (int i = 0; i < goalArr.length(); i++) {
            JSONObject goalObj = goalArr.getJSONObject(i);

            String name = goalObj.getString("name");
            Goal goal = new Goal(name);

            // collect thangs
            if (goalObj.has("collectThangs")) {
                JSONArray targetArr = goalObj.getJSONObject("collectThangs").getJSONArray("targets");
                List<String> targets = new ArrayList<>(targetArr.length());
                for (int j = 0; j < targetArr.length(); j++) {
                    targets.add(targetArr.getString(j));
                }
                goal.setCollectThangs(targets);
            }

            goals.add(goal);
        }
        return goals;
    }

    @NonNull
    public static List<Thang> parseThangArr(@NonNull JSONArray thangArr) throws JSONException {
        List<Thang> thangs = new ArrayList<>(thangArr.length());
        for (int i = 0; i < thangArr.length(); i++) {
            JSONObject thangObj = thangArr.getJSONObject(i);
            String id = thangObj.getString("id");
            String thangType = thangObj.getString("thangType");
            Thang thang = new Thang(id, thangType, null);

            JSONArray componentArr = thangObj.getJSONArray("components");
            for (int j = 0; j < componentArr.length(); j++) {
                JSONObject componentObj = componentArr.getJSONObject(j);
                if (!componentObj.has("config")) {
                    continue;
                }
                JSONObject configObj = componentObj.getJSONObject("config");
                // position
                if (configObj.has("pos")) {
                    JSONObject posObj = configObj.getJSONObject("pos");
                    float x = (float) posObj.getDouble("x");
                    float y = (float) posObj.getDouble("y");
                    thang.setPosition(new Position(x, y));
                }
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
                String image = prerenderedData.getString("image");
                thangType.setImage(image);
            }
        }

        // width and height
        JSONArray componentArr = thangTypeObj.getJSONArray("components");
        for (int j = 0; j < componentArr.length(); j++) {
            JSONObject componentObj = componentArr.getJSONObject(j);
            if (!componentObj.has("config")) {
                continue;
            }
            JSONObject configObj = componentObj.getJSONObject("config");
            // width
            if (configObj.has("width")) {
                int width = configObj.getInt("width");
                thangType.setWidth(width);
            }
            // height
            if (configObj.has("height")) {
                int height = configObj.getInt("height");
                thangType.setHeight(height);
            }
        }

        // TODO: 28/05/2018 get raw

        return thangType;
    }
}
