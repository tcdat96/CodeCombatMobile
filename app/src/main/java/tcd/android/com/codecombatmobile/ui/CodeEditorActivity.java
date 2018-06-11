package tcd.android.com.codecombatmobile.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.CodeEditor;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.OperationFactory;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.UserInput;
import tcd.android.com.codecombatmobile.ui.widget.SyntaxButton;
import tcd.android.com.codecombatmobile.util.CCRequestManager;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_ASSIGNMENT;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_DECLARATION;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_FLOW_CONTROL;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_FUNCTION;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_OPERATOR;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_VALUE;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_VARIABLE;

public class CodeEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CodeEditorActivity.class.getSimpleName();
    private static final long DEFAULT_JS_CODE_INTERVAL = TimeUnit.SECONDS.toMillis(2);

    private ViewGroup mRootLayout;
    private FloatingActionButton mRunFab;
    private LinearLayout mKeyboardLayout;
    private ScrollView mEditorScrollView;
    private CodeEditor mCodeEditor;
    private WebView mGameLevelWebView;

    @Nullable
    private Drawable mKeyboardDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_editor);

        getKeyboardDrawable();
        initUiComponents();

        initCookie();
        initWebView();

        String path = "/play/level/dungeons-of-kithgard?course=560f1a9f22961295f9427742&course-instance=5b018b55137ddc2dc00c2407";
        String url = CCRequestManager.getInstance(this).getRequestUrl(path);
        mGameLevelWebView.loadUrl(url);
    }

    private void getKeyboardDrawable() {
        mKeyboardDrawable = ContextCompat.getDrawable(this, R.drawable.ic_keyboard_white_24);
        if (mKeyboardDrawable != null) {
            mKeyboardDrawable = DrawableCompat.wrap(mKeyboardDrawable);
            DrawableCompat.setTint(mKeyboardDrawable, ContextCompat.getColor(this, R.color.keyboard_button_color));
        }
    }

    private void initUiComponents() {
        mRootLayout = findViewById(R.id.ll_root);
        mKeyboardLayout = findViewById(R.id.ll_keyboard_layout);
        mEditorScrollView = findViewById(R.id.sv_editor_container);

        mCodeEditor = findViewById(R.id.code_editor);
        mCodeEditor.setOnClickListener(this);

        mRunFab = findViewById(R.id.fab_run);
        mRunFab.setOnClickListener(this);

        findViewById(R.id.iv_hide_keyboard_button).setOnClickListener(this);
        findViewById(R.id.iv_reset_button).setOnClickListener(this);
        findViewById(R.id.iv_undo_button).setOnClickListener(this);
        findViewById(R.id.iv_redo_button).setOnClickListener(this);
        findViewById(R.id.iv_backspace_button).setOnClickListener(this);

        initVirtualKeyboard();
    }

    private void initCookie() {
        CookieManager.getInstance().setAcceptCookie(true);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
    }

    private void initWebView() {
        mGameLevelWebView = findViewById(R.id.wv_game_level);
        final CCWebClient ccWebClient = new CCWebClient();
        mGameLevelWebView.setWebViewClient(ccWebClient);
        mGameLevelWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (consoleMessage.message().contains("PlayLevelView: level started")) {
                    ccWebClient.setPageFinished(true);
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });

        // for getting values from javascript
        mGameLevelWebView.addJavascriptInterface(this, TAG);

        initWebSettings();
    }

    private void initWebSettings() {
        WebSettings webSettings = mGameLevelWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
    }

    @JavascriptInterface
    public void resizeLevelWebView(final float height) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // resize level WebView
                CardView container = findViewById(R.id.cv_level_container);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) container.getLayoutParams();
                params.height = (int) height;
                container.setLayoutParams(params);
                // show it
                findViewById(R.id.ll_loading_message).setVisibility(View.GONE);
                mGameLevelWebView.setVisibility(View.VISIBLE);
                mRunFab.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initVirtualKeyboard() {
        final LinearLayout buttonContainer = findViewById(R.id.ll_button_container);

        List<Pair<Integer, String>> opTypes = new ArrayList<>();
        opTypes.add(new Pair<>(TYPE_FLOW_CONTROL, "if"));
        opTypes.add(new Pair<>(TYPE_FLOW_CONTROL, "for"));
        opTypes.add(new Pair<>(TYPE_FLOW_CONTROL, "return"));
        opTypes.add(new Pair<>(TYPE_DECLARATION, "func"));
        opTypes.add(new Pair<>(TYPE_VARIABLE, "abc"));
        opTypes.add(new Pair<>(TYPE_FUNCTION, "drawBox()_0"));
        opTypes.add(new Pair<>(TYPE_FUNCTION, "drawBoxes()_3"));
        opTypes.add(new Pair<>(TYPE_VALUE, "True"));
        opTypes.add(new Pair<>(TYPE_VALUE, "False"));
        opTypes.add(new Pair<>(TYPE_VALUE, "null"));
        opTypes.add(new Pair<>(TYPE_VALUE, "___"));
        opTypes.add(new Pair<>(TYPE_ASSIGNMENT, "="));
        opTypes.add(new Pair<>(TYPE_ASSIGNMENT, "+="));
        opTypes.add(new Pair<>(TYPE_ASSIGNMENT, "-="));
        opTypes.add(new Pair<>(TYPE_ASSIGNMENT, "*="));
        opTypes.add(new Pair<>(TYPE_ASSIGNMENT, "/="));
        opTypes.add(new Pair<>(TYPE_OPERATOR, "+"));
        opTypes.add(new Pair<>(TYPE_OPERATOR, "-"));
        opTypes.add(new Pair<>(TYPE_OPERATOR, "*"));
        opTypes.add(new Pair<>(TYPE_OPERATOR, "/"));
        final OperationFactory factory = new OperationFactory();

        final SyntaxButton[] buttons = new SyntaxButton[opTypes.size()];
        LinearLayout columnLayout = new LinearLayout(this);
        for (int i = 0; i < opTypes.size(); i++) {
            if (i % 4 == 0) {
                columnLayout = DisplayUtil.getVerticalLinearLayout(this);
                buttonContainer.addView(columnLayout);
            }

            final Pair<Integer, String> pair = opTypes.get(i);
            final Operation operation = factory.getOperation(pair);
            if (operation != null) {
                operation.init(this);
                SyntaxButton button = new SyntaxButton(this);
                button.setText(operation.getButtonName());
                button.setOperation(operation);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Operation newOp = factory.getOperation(pair);
                        if (newOp != null) {
                            mCodeEditor.addOperation(newOp);
                        }
                    }
                });

                if (operation instanceof UserInput) {
                    button.setCompoundDrawablesWithIntrinsicBounds(null, null, mKeyboardDrawable, null);
                }

                buttons[i] = button;
                columnLayout.addView(button);
            }
        }

        setOnOperationChangedListener(buttons);
    }

    private void setOnOperationChangedListener(final SyntaxButton[] buttons) {
        mCodeEditor.setOnOperationChangedListener(new CodeEditor.OnOperationChangedListener() {
            @Override
            public void onOperationChangedListener(@Nullable Operation operation) {
                if (operation == null || operation.getContainer() == null) {
                    for (SyntaxButton button : buttons) {
                        button.setEnabled(true);
                    }
                } else {
                    Operation container = operation.getContainer();
                    for (SyntaxButton button : buttons) {
                        int index = container.getReplacementIndex(operation, button.getOperation());
                        button.setEnabled(index >= 0);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mKeyboardLayout.getVisibility() == View.VISIBLE) {
            setVirtualKeyboardVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    public void setVirtualKeyboardVisibility(int visibility) {
        TransitionManager.beginDelayedTransition(mRootLayout);
        mKeyboardLayout.setVisibility(visibility);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.code_editor:
                if (mKeyboardLayout.getVisibility() == View.GONE) {
                    setVirtualKeyboardVisibility(View.VISIBLE);
                    mKeyboardLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEditorScrollView.smoothScrollTo(0, mEditorScrollView.getHeight());
                        }
                    }, 500);
                }
                break;
            case R.id.fab_run:
                String runFunc = "document.getElementsByClassName('cast-button')[0].click()";
                runJsFunc(runFunc);
                break;
            case R.id.iv_hide_keyboard_button:
                TransitionManager.beginDelayedTransition(mRootLayout);
                mKeyboardLayout.setVisibility(View.GONE);
                break;
            case R.id.iv_reset_button:
                mCodeEditor.clear();
                break;
            case R.id.iv_undo_button:
                break;
            case R.id.iv_redo_button:
                break;
            case R.id.iv_backspace_button:
                mCodeEditor.removeOperation();
                break;
            default:
                break;
        }
    }

    private void runJsFunc(String function) {
        String url = "javascript:(function() { " + function + "})()";
        mGameLevelWebView.loadUrl(url);
    }

    private class CCWebClient extends WebViewClient {

        private boolean mIsPageFinished = false;

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
                                "document.getElementById('canvas-wrapper').style.width='100%';" +
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

                        // resize webview
                        String function = "document.getElementById('game-area').clientHeight";
                        url = String.format("javascript:%s.%s(%s)", TAG, "resizeLevelWebView", function);
                        mGameLevelWebView.loadUrl(url);

                        // show game buttons
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
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
