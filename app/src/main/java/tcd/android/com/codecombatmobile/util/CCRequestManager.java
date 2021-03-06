package tcd.android.com.codecombatmobile.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import tcd.android.com.codecombatmobile.data.user.Student;
import tcd.android.com.codecombatmobile.data.user.Teacher;
import tcd.android.com.codecombatmobile.data.user.User;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static com.android.volley.Request.Method.PUT;

public class CCRequestManager {
    private static final String TAG = CCRequestManager.class.getSimpleName();

    private static CCRequestManager mInstance = null;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    // singleton pattern
    private CCRequestManager(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();

        CookieUtil.getCookieUtil(context).initCookieHandler();
    }

    public static synchronized CCRequestManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CCRequestManager(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(), new HurlStack());
        }
        return mRequestQueue;
    }


    // fundamental methods
    public String getRequestUrl(String path) {
        String protocol = "http://";
        String domainName = "codecombat.eastus.cloudapp.azure.com:3000";
//        String domainName = getIpAddress() + ":3000";
        return protocol + domainName + path;
    }

    private String getIpAddress() {
        if (Build.MANUFACTURER.contains("Genymotion")) {
            return "10.0.3.2";
        } else if (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)) {
            return "10.0.2.2";
        } else {
            return "127.0.0.1";
        }
    }


    public String getFileUrl(String path) {
        return getRequestUrl("/file/" + path);
    }


    private RequestFuture<JSONObject> sendRequestSync(int method, String path, JSONObject jsonRequest) {
        String reqUrl = getRequestUrl(path);
        Log.d(TAG, "sendRequestSync: " + reqUrl);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(method, reqUrl, jsonRequest, future, future);
        getRequestQueue().add(request);
        return future;
    }

    private RequestFuture<JSONArray> sendRequestSync(int method, String path, JSONArray jsonRequest) {
        String reqUrl = getRequestUrl(path);
        Log.d(TAG, "sendRequestSync: " + reqUrl);
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(method, reqUrl, jsonRequest, future, future);
        getRequestQueue().add(request);
        return future;
    }

    @Nullable
    private <T> T getResponse(RequestFuture<T> future) {
        T response = null;
        try {
            response = future.get();
        } catch (InterruptedException | ExecutionException e) {
            if (VolleyError.class.isAssignableFrom(e.getCause().getClass())) {
                VolleyError ve = (VolleyError) e.getCause();
                if (ve.networkResponse != null) {
                    Log.d(TAG, "statusCode: " + ve.networkResponse.statusCode);
                }
            } else {
                e.printStackTrace();
            }
        }
        return response;
    }

    private void sendRequestAsync(int method, String path, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        String url = getRequestUrl(path);
        JsonArrayRequest request = new JsonArrayRequest(method, url, null, listener,
                errorListener != null ? errorListener : new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                    }
                });
        getRequestQueue().add(request);
    }

    // main activity
    public String getAboutUrl() {
        return getRequestUrl("/about");
    }

    // sign in / sign up
    @Nullable
    public JSONObject logInSync(@NonNull String username, @NonNull String password) {
        // construct request data
        JSONObject jsonReq = new JSONObject();
        try {
            jsonReq.put("username", username);
            jsonReq.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestFuture<JSONObject> future = sendRequestSync(POST, "/auth/login", jsonReq);
        return getResponse(future);
    }

    public JSONObject validateEmail(@NonNull String email) {
        JSONObject result = null;
        try {
            String encodedEmail = URLEncoder.encode(email, "UTF-8");
            String path = String.format(Locale.getDefault(), "/auth/email/%s?_=%d", encodedEmail, System.currentTimeMillis());
            RequestFuture<JSONObject> future = sendRequestSync(GET, path, new JSONObject());
            result = getResponse(future);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject validateUsername(@NonNull String username) {
        JSONObject result = null;
        try {
            String encodedUsername = URLEncoder.encode(username, "UTF-8");
            String path = String.format(Locale.getDefault(), "/auth/name/%s?_=%d", encodedUsername, System.currentTimeMillis());
            RequestFuture<JSONObject> future = sendRequestSync(GET, path, new JSONObject());
            result = getResponse(future);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Nullable
    public JSONObject signUpSync(@NonNull User user, @NonNull String password) {
        // get user ID
        String getIdPath = "/auth/whoami?_=" + System.currentTimeMillis();
        RequestFuture<JSONObject> future = sendRequestSync(GET, getIdPath, new JSONObject());
        JSONObject idObj = getResponse(future);
        try {
            if (idObj == null || TextUtils.isEmpty(idObj.getString("_id"))) {
                return null;
            }
            String uid = idObj.getString("_id");
            // send basic account information
            String updateInfoPath = "/db/user/" + uid;
            JSONObject accInfoJsonReq = getAccountInfoJsonRequest(idObj, user);
            future = sendRequestSync(PUT, updateInfoPath, accInfoJsonReq);
            JSONObject accInfoObj = getResponse(future);
            if (accInfoObj != null) {
                // trial request
                if (user instanceof Teacher) {
                    sendTrialRequest((Teacher) user);
                }
                // sign up with password
                JSONObject jsonReq = getSignUpJsonRequest(user, password);
                String signUpPath = String.format("/db/user/%s/signup-with-password", uid);
                future = sendRequestSync(POST, signUpPath, jsonReq);
                return getResponse(future);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getAccountInfoJsonRequest(JSONObject idObj, User user) throws JSONException {
//        idObj = new JSONObject(idObj.toString());
        idObj.put("birthday", "2000-01-01T00:00:00.000Z");
        JSONObject generalNewsObj = new JSONObject().put("enabled", true);
        JSONObject emailsObj = new JSONObject().put("generalNews", generalNewsObj);
        idObj.put("emails", emailsObj);
        if (user instanceof Student) {
            idObj.put("role", "student");
        } else {
            Teacher teacher = (Teacher) user;
            idObj.put("role", "teacher");
            idObj.put("firstName", teacher.getFirstName());
            idObj.put("lastName", teacher.getLastName());
        }
        return idObj;
    }

    private JSONObject sendTrialRequest(Teacher teacher) throws JSONException {
        JSONObject properties = new JSONObject();
        properties.put("email", teacher.getEmail());
        properties.put("firstName", teacher.getFirstName());
        properties.put("lastName", teacher.getLastName());
        properties.put("organization", teacher.getOrganization());
        properties.put("country", teacher.getCountry());
        properties.put("phoneNumber", teacher.getPhoneNumber());
        properties.put("role", teacher.getRole());
        properties.put("numStudents", teacher.getEstimatedStudent());
        JSONObject jsonReq = new JSONObject();
        jsonReq.put("properties", properties);
        jsonReq.put("type", "course");

        RequestFuture<JSONObject> future = sendRequestSync(POST, "/db/trial.request", jsonReq);
        return getResponse(future);
    }

    private JSONObject getSignUpJsonRequest(User user, String password) throws JSONException {
        JSONObject jsonReq = new JSONObject();
        jsonReq.put("email", user.getEmail());
        jsonReq.put("password", password);
        if (user instanceof Student) {
            jsonReq.put("name", ((Student) user).getUsername());
        }
        return jsonReq;
    }

    // teacher classroom list
    public void requestTeacherClassList(String teacherId, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        String path = "/db/classroom?ownerID=" + teacherId;
        sendRequestAsync(GET, path, listener, errorListener);
    }

    // student classroom list
    public JSONArray requestStudentClassListSync(String studentId) {
        String path = String.format(Locale.getDefault(), "/db/classroom?memberID=%s&_=%d", studentId, System.currentTimeMillis());
        RequestFuture<JSONArray> future = sendRequestSync(GET, path, new JSONArray());
        return getResponse(future);
    }

    public JSONArray requestCoursesSync() {
        String path = "/db/course";
        RequestFuture<JSONArray> future = sendRequestSync(GET, path, new JSONArray());
        return getResponse(future);
    }

    public JSONArray requestCourseInstancesSync(String studentId) {
        String path = String.format(Locale.getDefault(), "/db/user/%s/course_instances?_=%d", studentId, System.currentTimeMillis());
        RequestFuture<JSONArray> future = sendRequestSync(GET, path, new JSONArray());
        return getResponse(future);
    }

    public JSONObject requestNamesSync(@NonNull String userId) {
        JSONObject result = null;
        try {
            JSONObject jsonReq = new JSONObject();
            jsonReq.put("ids[]", userId);
            String path = "/db/user/x/names";
            RequestFuture<JSONObject> future = sendRequestSync(POST, path, jsonReq);
            result = getResponse(future);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONArray requestCourseLevelSessionsSync(String instanceId, String userId) {
        String path = String.format(
                "/db/course_instance/%s/course-level-sessions/%s?project=state.complete%%2Clevel.original",
                instanceId,
                userId
        );
        RequestFuture<JSONArray> future = sendRequestSync(GET, path, new JSONArray());
        return getResponse(future);
    }

    // join class
    public void addSClassroom(@NonNull final String code) {
        try {
            String path = "/db/classroom/~/members";
            JSONObject jsonReq = new JSONObject();
            jsonReq.put("code", code);
            sendRequestSync(POST, path, jsonReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // teacher classroom detail
    public JSONArray requestMemberSessionsSync(String classroomId) {
        String path = String.format("/db/classroom/%s/member-sessions?memberLimit=10&memberSkip=0", classroomId);
        RequestFuture<JSONArray> future = sendRequestSync(GET, path, new JSONArray());
        return getResponse(future);
    }

    public JSONArray requestMembersSync(String classroomId) {
        String path = String.format("/db/classroom/%s/members?project=name%%2Cemail%%2C&memberLimit=10&memberSkip=0", classroomId);
        RequestFuture<JSONArray> future = sendRequestSync(GET, path, new JSONArray());
        return getResponse(future);
    }

    public JSONArray requestClassroomLevels(String classroomId) {
        String path = String.format("/db/classroom/%s/levels?project=original%%2Cname%%2Cpractice%%2Ci18n%%2Cassessment", classroomId);
        RequestFuture<JSONArray> future = sendRequestSync(GET, path, new JSONArray());
        return getResponse(future);
    }

    // game map
    public JSONObject requestCampaignSync(String campaignId) {
        String path = "/db/campaign/" + campaignId;
        RequestFuture<JSONObject> future = sendRequestSync(GET, path, new JSONObject());
        return getResponse(future);
    }

    // game level
    public JSONObject requestLevelSessionSync(String levelId, String courseId, String instanceId) {
        String path = String.format(Locale.getDefault(), "/db/level/%s/session?course=%s&courseInstance=%s&_=%d",
                levelId,
                courseId,
                instanceId,
                System.currentTimeMillis());
        RequestFuture<JSONObject> future = sendRequestSync(GET, path, new JSONObject());
        return getResponse(future);
    }

    // profile
    public JSONArray requestLevelSessionsSync(String userId) {
        String path = String.format(Locale.getDefault(),
                "/db/user/%s/level.sessions?project=state.complete,levelName,changed,playtime,totalScore&order=-1&_=%d",
                userId,
                System.currentTimeMillis()
        );
        RequestFuture<JSONArray> future = sendRequestSync(GET, path, new JSONArray());
        return getResponse(future);
    }

    public JSONArray requestAchievementsSync(String userId) {
        String path = String.format(Locale.getDefault(), "/db/user/%s/achievements?_=%d", userId, System.currentTimeMillis());
        RequestFuture<JSONArray> future = sendRequestSync(GET, path, new JSONArray());
        return getResponse(future);
    }

    public JSONObject requestUserProfile(String userId) {
        String path = String.format(Locale.getDefault(), "/db/user/%s?_=%d", userId, System.currentTimeMillis());
        RequestFuture<JSONObject> future = sendRequestSync(GET, path, new JSONObject());
        return getResponse(future);
    }

    public String getUserAvatar(String userId) {
        String path = String.format("/db/user/%s/avatar?s=80", userId);
        return getRequestUrl(path);
    }
}
