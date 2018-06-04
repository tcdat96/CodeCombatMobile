package tcd.android.com.codecombatmobile.ui;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.concurrent.TimeUnit;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.util.CCRequestManager;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

public class LevelWebViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LevelWebViewActivity.class.getSimpleName();
    private static final long DEFAULT_JS_CODE_INTERVAL = TimeUnit.SECONDS.toMillis(2);

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_web_view);

        initCookie();
        initWebView();

        initUiComponents();

        String path = "/play/level/dungeons-of-kithgard?course=560f1a9f22961295f9427742&course-instance=5b018b55137ddc2dc00c2407";
        String url = CCRequestManager.getInstance(this).getRequestUrl(path);
        mWebView.loadUrl(url);
    }

    private void initCookie() {
        CookieManager.getInstance().setAcceptCookie(true);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
    }

    private void initWebView() {
        mWebView = findViewById(R.id.web_view);
        final CCWebClient ccWebClient = new CCWebClient();
        mWebView.setWebViewClient(ccWebClient);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (consoleMessage.message().contains("PlayLevelView: level started")) {
                    ccWebClient.setPageFinished(true);
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });

        initWebSettings();
    }

    private void initWebSettings() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
    }

    private void initUiComponents() {
        findViewById(R.id.btn_map).setOnClickListener(this);
        findViewById(R.id.btn_hints).setOnClickListener(this);
        findViewById(R.id.btn_run).setOnClickListener(this);
    }

    private void showGameButton() {
        LinearLayout buttonsContainer = findViewById(R.id.ll_button_container);
        buttonsContainer.animate().alpha(1.0f);
        buttonsContainer.setVisibility(View.VISIBLE);
    }

    private void resizeButtonContainer(int containerWidth, int containerHeight) {
        LinearLayout buttonsContainer = findViewById(R.id.ll_button_container);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) buttonsContainer.getLayoutParams();
        params.width = containerWidth;
        params.height = containerHeight;
        buttonsContainer.setLayoutParams(params);
    }

    private void runCurrentCode() {
        String url = "javascript:(function() { " +
                "})()";
        mWebView.loadUrl(url);
    }

    private void runJsFunc(String function) {
        String url = "javascript:(function() { " + function + "})()";
        mWebView.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_map:
                // TODO: 04/06/2018 implement this
                break;
            case R.id.btn_hints:
                String clickHintsFunc = "document.getElementsByClassName('hints-button')[0].click();";
                runJsFunc(clickHintsFunc);
                break;
            case R.id.btn_run:
                String clickRunFunc = "document.getElementsByClassName('cast-button')[0].click();";
                runJsFunc(clickRunFunc);
                break;
        }
    }

    private class CCWebClient extends WebViewClient {

        private int mWidthPercent;
        private boolean mIsPageFinished = false;

        public CCWebClient() {
            Point scrSize = DisplayUtil.getScreenSize(LevelWebViewActivity.this);
            int gameHeight = (int) (scrSize.y * 0.7f);
            int gameWidth = (int) (gameHeight * 1.575f);
            mWidthPercent = gameWidth * 100 / scrSize.x;

            resizeButtonContainer(scrSize.x - gameWidth, gameHeight);
        }

        public void setPageFinished(boolean finished) {
            mIsPageFinished = finished;
        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (!mIsPageFinished) {
                        handler.postDelayed(this, DEFAULT_JS_CODE_INTERVAL);
                    } else {
                        String url = "javascript:(function() { " +
                                // hide unnecessary parts
                                "document.getElementById('control-bar-view').style.display='none';" +
                                "document.getElementById('code-area').style.display='none';" +
                                "document.getElementById('spell-palette-view').style.display='none';" +
                                "document.getElementsByClassName('toggle-fullscreen')[0].style.display='none';" +
                                // resize game part
                                "document.getElementById('canvas-wrapper').style.top='0';" +
                                "document.getElementById('canvas-wrapper').style.width='" + mWidthPercent + "%';" +
                                "document.getElementById('normal-surface').style.width='100%';" +
                                "document.getElementById('webgl-surface').style.width='100%';" +
                                // resize control bar
                                "document.getElementById('playback-view').style.marginTop='0';" +
                                "document.getElementById('playback-view').style.width='100%';" +
                                "document.getElementById('thang-hud').style.width='100%';" +
                                // run the game
                                "document.getElementsByClassName('cast-button')[0].click();" +
                                "})()";
                        view.loadUrl(url);

                        // show game buttons
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showGameButton();
                                // sometimes the spell panel doesn't hide in the first time
                                String function = "document.getElementById('spell-palette-view').style.display='none';";
                                runJsFunc(function);
                            }
                        }, DEFAULT_JS_CODE_INTERVAL);
                    }
                }
            };
            handler.post(runnable);
        }
    }
}