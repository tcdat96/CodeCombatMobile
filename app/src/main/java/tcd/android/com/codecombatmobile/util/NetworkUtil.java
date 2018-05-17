package tcd.android.com.codecombatmobile.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Header;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class NetworkUtil {
    private static final String TAG = NetworkUtil.class.getSimpleName();
    private static final String AVD_EMULATOR_IP_ADDRESS = "10.0.2.2";
    private static final String GENY_MOTION_IP_ADDRESS = "10.0.3.2";
    private static final String READ_DEVICE_IP_ADDRESS = "192.168.0.109";

    private static NetworkUtil mInstance = null;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private NetworkUtil(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> mCache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return mCache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        mCache.put(url, bitmap);
                    }
                });
    }

    public static synchronized NetworkUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkUtil(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(), new HurlStack());
        }
        return mRequestQueue;
    }

    private String getRequestUrl(String path) {
        String protocol = "http://";
        String domainName = getIpAddress() + ":3000";
        return protocol + domainName + path;
    }

    private String getIpAddress() {
        if (Build.MANUFACTURER.contains("Genymotion")) {
            return GENY_MOTION_IP_ADDRESS;
        } else if (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)) {
            return AVD_EMULATOR_IP_ADDRESS;
        } else {
            return READ_DEVICE_IP_ADDRESS;
        }
    }

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

        // post request
        String reqUrl = getRequestUrl("/auth/login");
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, reqUrl, jsonReq, future, future);
        getRequestQueue().add(request);

        // get response
        JSONObject response = null;
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
}
