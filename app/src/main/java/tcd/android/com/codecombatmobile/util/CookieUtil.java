package tcd.android.com.codecombatmobile.util;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.bumptech.glide.util.Util;

import java.net.CookieHandler;
import java.net.CookiePolicy;

public class CookieUtil {
    private CookieManager manager;
    private CookieStore_ cookieStore_;
    private CookieSyncManager syncManager;
    private static CookieUtil cookieUtil;
    private boolean isInitialed = false;
    
    private boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private CookieUtil(Context context) {
        manager = CookieManager.getInstance();

        if (!hasLollipop()) {
            syncManager = CookieSyncManager.createInstance(context);
        }
        cookieStore_ = new CookieStore_();
    }

    public void clearCookies() {
        if (manager.hasCookies()) {
            if (hasLollipop()) {
                manager.removeAllCookies(null);
            } else {
                manager.removeAllCookie();
            }
        }
        cookieStore_.clearCookies();
    }

    public static CookieUtil getCookieUtil(Context context) {
        if (cookieUtil == null)
            cookieUtil = new CookieUtil(context);
        return cookieUtil;
    }

    public void sync() {
        if (hasLollipop()) {
            manager.flush();
        } else {
            syncManager.sync();
        }
    }

    public void setThirdPartyCookieAcceptable(WebView webView) {
        if (hasLollipop()) {
            manager.setAcceptThirdPartyCookies(webView, true);
        }
    }

    public void initCookieHandler() {
        if (isInitialed)
            return;
        isInitialed = true;
        CookieHandler.setDefault(new java.net.CookieManager(cookieStore_, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
    }
}
