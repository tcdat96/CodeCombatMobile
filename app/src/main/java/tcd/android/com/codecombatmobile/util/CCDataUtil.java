package tcd.android.com.codecombatmobile.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import tcd.android.com.codecombatmobile.data.Position;
import tcd.android.com.codecombatmobile.data.course.Course;
import tcd.android.com.codecombatmobile.data.course.MemberProgress;
import tcd.android.com.codecombatmobile.data.course.TClassroom;
import tcd.android.com.codecombatmobile.data.game.Achievement;
import tcd.android.com.codecombatmobile.data.game.Level;
import tcd.android.com.codecombatmobile.data.game.Session;
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
        String id = classObj.getString("_id");
        String name = classObj.getString("name");
        String code = classObj.getString("codeCamel");

        // language
        JSONObject aceConfig = classObj.getJSONObject("aceConfig");
        String language = aceConfig != null ? aceConfig.getString("language") : "python";

        // number of members
        JSONArray memberArr = classObj.getJSONArray("members");
        int members = memberArr != null ? memberArr.length() : 0;

        // date created
        JSONArray courseArr = classObj.has("courses") ? classObj.getJSONArray("courses") : null;
        String updated = courseArr != null && courseArr.length() > 0 ?
                classObj.getJSONArray("courses").getJSONObject(0).getString("updated") :
                "";
        long dateCreated = !TextUtils.isEmpty(updated) ? TimeUtil.convertCcTimeToLong(updated) : 0;

        // TODO: 20/05/2018 do something with this
        int progress = new Random().nextInt(100);

        return new TClassroom(id, language, name, code, members, dateCreated, progress);
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
                && (levelObj.has("type") && !levelObj.getString("type").equals("course-ladder"));
    }

    // teacher classroom detail
    public static List<Session> parseMemberSessions(@NonNull JSONArray sessionArr) throws JSONException {
        List<Session> sessions = new ArrayList<>();
        for (int i = 0; i < sessionArr.length(); i++) {
            JSONObject sessionObj = sessionArr.getJSONObject(i);
            boolean isCompleted = sessionObj.getJSONObject("state").getBoolean("complete");
            Session session = new Session(isCompleted);

            String original = sessionObj.getJSONObject("level").getString("original");
            session.setOriginal(original);
            String creator = sessionObj.getString("creator");
            session.setCreator(creator);
            int playtime = sessionObj.has("playtime") ? sessionObj.getInt("playtime") : 0;
            session.setPlaytime(playtime);

            sessions.add(session);
        }
        return sessions;
    }

    public static MemberProgress[] parseTClassroomMembers(@NonNull JSONArray memberArr) throws JSONException {
        MemberProgress[] members = new MemberProgress[memberArr.length()];
        for (int i = 0; i < memberArr.length(); i++) {
            JSONObject memberObj = memberArr.getJSONObject(i);
            String id = memberObj.getString("_id");
            String name = memberObj.getString("name");
            String email = memberObj.getString("email");
            members[i] = new MemberProgress(id, name, email);
        }
        return members;
    }

    public static void updateMembersWithSessions(@NonNull MemberProgress[] members, @NonNull List<Session> sessions) {
        for (Session session : sessions) {
            if (!session.isCompleted()) {
                continue;
            }

            // find the correct member
            MemberProgress member = null;
            for (MemberProgress member1 : members) {
                if (session.getCreator().equals(member1.getId())) {
                    member = member1;
                    break;
                }
            }
            // increase the completed level
            if (member != null) {
                int prevCompletedLevels = member.getCompletedLevels();
                member.setCompletedLevels(prevCompletedLevels + 1);
            }
        }
    }

    public static int sumPlaytimeTotal(@NonNull List<Session> sessions) {
        int playtime = 0;
        for (Session session : sessions) {
            playtime += session.getPlaytime();
        }
        return playtime;
    }

    public static int countCompletedLevels(@NonNull List<Session> sessions) {
        Set<String> ids = new HashSet<>();
        for (Session session : sessions) {
            if (session.isCompleted()) {
                ids.add(session.getOriginal());
            }
        }
        return ids.size();
    }

    // game map
    public static Level getLevel(JSONObject levelObj) throws JSONException {
        String id = levelObj.getString("original");
        String name = levelObj.getString("name");
        String slug = levelObj.getString("slug");
        String description = levelObj.getString("description");
        int campaignIndex = levelObj.getInt("campaignIndex");
        // practice
        boolean isPrimary = isPrimaryLevel(levelObj);
        // position
        JSONObject posObj = levelObj.getJSONObject("position");
        float x = (float) posObj.getDouble("x");
        float y = (float) posObj.getDouble("y");
        Position position = new Position(x, y);

        return new Level(id, name, slug, description, campaignIndex, isPrimary, position);
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

    // game level
    @Nullable
    public static String getInitialCodeSnippet(@NonNull JSONObject session) throws JSONException {
        if (session.has("code")) {
            JSONObject code = session.getJSONObject("code");
            if (code.has("hero-placeholder")) {
                JSONObject placeholder = code.getJSONObject("hero-placeholder");
                if (placeholder.has("plan")) {
                    return placeholder.getString("plan");
                }
            }
        }
        return null;
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
