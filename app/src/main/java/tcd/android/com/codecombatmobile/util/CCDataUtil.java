package tcd.android.com.codecombatmobile.util;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tcd.android.com.codecombatmobile.data.Position;
import tcd.android.com.codecombatmobile.data.course.Course;
import tcd.android.com.codecombatmobile.data.game.Achievement;
import tcd.android.com.codecombatmobile.data.game.Session;
import tcd.android.com.codecombatmobile.data.course.TClassroom;
import tcd.android.com.codecombatmobile.data.game.Level;
import tcd.android.com.codecombatmobile.data.user.ProfileGeneral;

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
    public static List<Session> parseLevelSessions(@NonNull JSONArray sessionArr) throws JSONException {
        List<Session> sessions = new ArrayList<>();
        for (int i = 0; i < sessionArr.length(); i++) {
            JSONObject sessionObj = sessionArr.getJSONObject(i);
            boolean isCompleted = sessionObj.getJSONObject("state").getBoolean("complete");
            Session session = new Session(isCompleted);

            if (sessionObj.has("level")) {
                // normal
                String original = sessionObj.getJSONObject("level").getString("original");
                session.setOriginal(original);
                String levelId = sessionObj.getString("levelID");
                session.setLevelId(levelId);
            } else {
                // profile
                if (!sessionObj.has("levelName") || !sessionObj.has("changed")) {
                    continue;
                }

                String levelName = sessionObj.getString("levelName");
                session.setLevelName(levelName);
                String changed = sessionObj.getString("changed");
                long timeChanged = TimeUtil.convertCcTimeToLong(changed);
                session.setTimeChanged(timeChanged);
                int playtime = sessionObj.getInt("playtime");
                session.setPlaytime(playtime);

                if (sessionObj.has("totalScore")) {
                    int totalScore = (int) sessionObj.getDouble("totalScore");
                    session.setTotalScore(totalScore);
                }
            }

            sessions.add(session);
        }
        return sessions;
    }

    // profile
    @NonNull
    public static List<Achievement> parseAchievements(@NonNull JSONArray acmArr) throws JSONException {
        List<Achievement> achievements = new ArrayList<>();
        for (int i = 0; i < acmArr.length(); i++) {
            JSONObject acmObj = acmArr.getJSONObject(i);

            String name = acmObj.getString("achievementName");
            int earnedGems = acmObj.getInt("earnedGems");

            String changed = acmObj.getString("changed");
            long lastEarned = TimeUtil.convertCcTimeToLong(changed);

            Achievement achievement = new Achievement(name, lastEarned, earnedGems);

            achievements.add(achievement);
        }
        return achievements;
    }

    @NonNull
    public static ProfileGeneral parseProfileGeneral(@NonNull JSONObject profileObj) throws JSONException {
        String name = profileObj.getString("name");
        int spent = profileObj.has("spent") ? profileObj.getInt("spent") : 0;
        int courseTotal = profileObj.getJSONArray("courseInstances").length();
        // TODO: 22/06/2018 missing user level
        return new ProfileGeneral(name, 0, spent, courseTotal);
    }
}
