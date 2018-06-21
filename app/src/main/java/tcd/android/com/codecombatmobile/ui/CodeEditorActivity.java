package tcd.android.com.codecombatmobile.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.CodeEditor;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Function;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Object;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.OperationFactory;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.OperationFactory.OperationType;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.UserInput;
import tcd.android.com.codecombatmobile.ui.widget.SyntaxButton;
import tcd.android.com.codecombatmobile.util.CCRequestManager;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_ASSIGNMENT;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_DECLARATION;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_FLOW_CONTROL;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_FUNCTION;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_METHOD;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_OPERATOR;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_VALUE;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_VARIABLE;


public class CodeEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CodeEditorActivity.class.getSimpleName();
    private static final long DEFAULT_JS_CODE_INTERVAL = TimeUnit.SECONDS.toMillis(2);
    public static final String ARG_LEVEL_ID_DATA = "argLevelId";
    public static final String ARG_COURSE_ID_DATA = "argCourseId";
    public static final String ARG_INSTANCE_ID_DATA = "argInstanceId";

    private static OperationFactory mFactory = new OperationFactory();

    private ViewGroup mRootLayout;
    private FloatingActionButton mRunFab;
    private LinearLayout mKeyboardLayout;
    private ScrollView mEditorScrollView;
    private CodeEditor mCodeEditor;
    private WebView mGameLevelWebView;

    private LinearLayout mButtonContainer;
    @NonNull
    private List<SyntaxButton> mSyntaxButtons = new ArrayList<>();
    @NonNull
    private List<OperationType> mOpTypes = new ArrayList<>();

    @Nullable
    private Drawable mKeyboardDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_editor);

        // get level data
//        Intent data = getIntent();
//        String levelId = data != null && data.hasExtra(ARG_LEVEL_ID_DATA) ? data.getStringExtra(ARG_LEVEL_ID_DATA) : null;
//        String courseId = data != null && data.hasExtra(ARG_COURSE_ID_DATA) ? data.getStringExtra(ARG_COURSE_ID_DATA) : null;
//        String instanceId = data != null && data.hasExtra(ARG_INSTANCE_ID_DATA) ? data.getStringExtra(ARG_INSTANCE_ID_DATA) : null;
//        if (levelId == null || courseId == null || instanceId == null) {
//            Toast.makeText(this, R.string.error_get_level_message, Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }

        getKeyboardDrawable();
        initUiComponents();

        initCookie();
        initWebView();
//        String path = String.format("/play/level/%s?course=%s&course-instance=%s", levelId, courseId, instanceId);
        String path = "/play/level/gems-in-the-deep?course=560f1a9f22961295f9427742&course-instance=5b018b55137ddc2dc00c2407";
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
        mButtonContainer = findViewById(R.id.ll_button_container);

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
                String msg = consoleMessage.message();
                if (msg.contains("PlayLevelView: level started")) {
                    ccWebClient.setPageFinished();
                } else if (msg.contains("PlayLevelView: loaded session")) {
                    findViewById(R.id.ll_loading_message).setVisibility(View.GONE);
                    mGameLevelWebView.setVisibility(View.VISIBLE);
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
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
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
                // show run FAB
                mRunFab.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initVirtualKeyboard() {
        mOpTypes = new ArrayList<>();
        mOpTypes.add(new OperationType(TYPE_FLOW_CONTROL, "if"));
        mOpTypes.add(new OperationType(TYPE_FLOW_CONTROL, "for"));
        mOpTypes.add(new OperationType(TYPE_FLOW_CONTROL, "return"));
        mOpTypes.add(new OperationType(TYPE_DECLARATION, "var"));
        mOpTypes.add(new OperationType(TYPE_DECLARATION, "func"));
        mOpTypes.add(new OperationType(TYPE_VARIABLE, "hero"));
        mOpTypes.add(new OperationType(TYPE_FUNCTION, "drawBox()", new java.lang.Object[]{0}));
        mOpTypes.add(new OperationType(TYPE_FUNCTION, "drawBoxes()", new java.lang.Object[]{3}));
        mOpTypes.add(new OperationType(TYPE_METHOD, ".moveRight()", new java.lang.Object[]{0}));
        mOpTypes.add(new OperationType(TYPE_VALUE, "True"));
        mOpTypes.add(new OperationType(TYPE_VALUE, "False"));
        mOpTypes.add(new OperationType(TYPE_VALUE, "null"));
        mOpTypes.add(new OperationType(TYPE_VALUE, "___"));
        mOpTypes.add(new OperationType(TYPE_ASSIGNMENT, "="));
        mOpTypes.add(new OperationType(TYPE_ASSIGNMENT, "+="));
        mOpTypes.add(new OperationType(TYPE_ASSIGNMENT, "-="));
        mOpTypes.add(new OperationType(TYPE_ASSIGNMENT, "*="));
        mOpTypes.add(new OperationType(TYPE_ASSIGNMENT, "/="));
        mOpTypes.add(new OperationType(TYPE_OPERATOR, "+"));
        mOpTypes.add(new OperationType(TYPE_OPERATOR, "-"));
        mOpTypes.add(new OperationType(TYPE_OPERATOR, "*"));
        mOpTypes.add(new OperationType(TYPE_OPERATOR, "/"));
        mOpTypes.add(new OperationType(TYPE_OPERATOR, "=="));
        mOpTypes.add(new OperationType(TYPE_OPERATOR, "!="));
        mOpTypes.add(new OperationType(TYPE_OPERATOR, ">"));
        mOpTypes.add(new OperationType(TYPE_OPERATOR, "<"));
        mOpTypes.add(new OperationType(TYPE_OPERATOR, ">="));
        mOpTypes.add(new OperationType(TYPE_OPERATOR, "<="));
        mOpTypes.add(new OperationType(TYPE_OPERATOR, "&&"));
        mOpTypes.add(new OperationType(TYPE_OPERATOR, "||"));

        displayButtons();

        setOnOperationChangedListener();
    }

    private void displayButtons() {
        mButtonContainer.removeAllViews();

        mSyntaxButtons = new ArrayList<>(mOpTypes.size());
        for (OperationType type : mOpTypes) {
            addNewButton(type);
        }

        showOnlyStatementButtons();
    }

    private Object mObject;

    private void addNewButton(OperationType opType) {
        LinearLayout columnLayout = getLastColumn();
        if (columnLayout != null) {
            Operation operation = mFactory.getOperation(opType);
            if (operation != null) {
                // add new button
                SyntaxButton button = createNewButton(operation, opType);
                columnLayout.addView(button);
                mSyntaxButtons.add(button);

                // TODO: 13/06/2018 temporary workaround for debug purpose
                if (operation instanceof Object) {
                    mObject = (Object) operation;
                } else if (operation instanceof Function && operation.getButtonName().startsWith(".")) {
                    if (mObject != null) {
                        Function function = (Function) operation;
                        mObject.addMethods(Collections.singletonList(function));
                    }
                }
            }
        }
    }

    @Nullable
    private LinearLayout getLastColumn() {
        LinearLayout lastColumn = null;
        int columnTotal = mButtonContainer.getChildCount();
        if (columnTotal == 0) {
            lastColumn = DisplayUtil.getVerticalLinearLayout(this);
            mButtonContainer.addView(lastColumn);
        } else {
            View lastView = mButtonContainer.getChildAt(columnTotal - 1);
            if (lastView instanceof LinearLayout) {
                lastColumn = (LinearLayout) lastView;
                if (lastColumn.getChildCount() >= 4) {
                    lastColumn = DisplayUtil.getVerticalLinearLayout(this);
                    mButtonContainer.addView(lastColumn);
                }
            }
        }
        return lastColumn;
    }

    @NonNull
    private SyntaxButton createNewButton(@NonNull Operation operation, final OperationType opType) {
        operation.init(this);
        final SyntaxButton button = new SyntaxButton(this);
        button.setText(operation.getButtonName());
        button.setOperation(operation);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Operation newOp = mFactory.getOperation(opType);
                if (newOp != null) {
                    mCodeEditor.addOperation(newOp);
                }

                Operation source = button.getOperation();
                if (source != null && source instanceof Object && newOp != null) {
                    List<Function> methods = ((Object) source).getMethods();
                    ((Object) newOp).addMethods(methods);
                }
            }
        });

        if (operation instanceof UserInput) {
            button.setCompoundDrawablesWithIntrinsicBounds(null, null, mKeyboardDrawable, null);
        }
        return button;
    }

    /**
     * adjust variable state to the current selecting operation
     * this method should be called when user select an operation
     */
    private void setOnOperationChangedListener() {
        mCodeEditor.setOnOperationChangedListener(new CodeEditor.OnOperationChangedListener() {
            @Override
            public void onOperationChangedListener(@Nullable Operation operation) {
                if (operation == null || operation.getContainer() == null) {
                    showOnlyStatementButtons();
                } else {
                    for (SyntaxButton button : mSyntaxButtons) {
                        boolean isReplaceable = operation.isReplaceableWith(button.getOperation());
                        button.setEnabled(isReplaceable);
                    }
                }
            }
        });
    }

    /**
     * disable non-statement syntax buttons, including pure values, methods and operators
     */
    private void showOnlyStatementButtons() {
        for (SyntaxButton button : mSyntaxButtons) {
            Operation newOp = button.getOperation();
            boolean isInsertable = newOp != null && newOp.isStatement();
            button.setEnabled(isInsertable);
        }
    }

    /**
     * add new variable button, or replacing an old one
     * this method should be called after user finishes editing a variable declaration operation
     *
     * @param oldVarName the previous variable name, this could be null or empty in case of new variable
     * @param inputOp    the UserInput operation (the whole operation must be included for resetting purpose)
     */
    public void updateVariableButton(String oldVarName, UserInput inputOp) {
        // create new variable button
        String newVarName = inputOp.getButtonName();
        OperationType newVarType = new OperationType(TYPE_VARIABLE, newVarName);
        newVarType.setBuiltIn(false);

        // updating old one
        if (!TextUtils.isEmpty(oldVarName)) {
            for (int index = 0; index < mOpTypes.size(); index++) {
                OperationType opType = mOpTypes.get(index);
                if (opType.getSyntaxType() == TYPE_VARIABLE && opType.getName().equals(oldVarName)) {
                    mOpTypes.set(index, newVarType);
                    break;
                }
            }
        } else {
            // or adding new one
            for (int index = 0; index < mOpTypes.size(); index++) {
                OperationType opType = mOpTypes.get(index);
                int type = opType.getSyntaxType();
                // if it is already defined
                if (type == TYPE_VARIABLE) {
                    if (opType.getName().equals(newVarName)) {
                        String duplicateMsg = String.format(getString(R.string.error_duplicate_variable), newVarName);
                        Toast.makeText(this, duplicateMsg, Toast.LENGTH_LONG).show();
                        inputOp.reset();
                        break;
                    }
                } else if (type == TYPE_FUNCTION || type == TYPE_VALUE) {
                    // valid variable
                    mOpTypes.add(index, newVarType);
                    break;
                }
            }
        }

        displayButtons();
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
                fillCodeEditor();
                break;
            case R.id.iv_hide_keyboard_button:
                setVirtualKeyboardVisibility(View.GONE);
                break;
            case R.id.iv_reset_button:
                mCodeEditor.clear();
                removeUserDefinedOperations();
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

    private void removeUserDefinedOperations() {
        for (int i = 0; i < mOpTypes.size(); i++) {
            if (!mOpTypes.get(i).isBuiltIn()) {
                mOpTypes.remove(i);
                i--;
            }
        }
        displayButtons();
    }

    private List<String> mPrevCode = null;
    private void fillCodeEditor() {
        final List<String> code = mCodeEditor.getCode();
        if (code == mPrevCode) {
            String runFunc = "document.getElementsByClassName('cast-button')[0].click()";
            runJsFunc(runFunc);
        } else {
            // click restart button
            String restartFunc = "document.getElementsByClassName(\"reload-code\")[0].click()";
            runJsFunc(restartFunc);
            final Handler handler = new Handler();

            int delayMillis = 250;
            // then confirm restart all
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String confirmFunc = "document.getElementById(\"restart-level-confirm-button\").click()";
                    runJsFunc(confirmFunc);
                    String clickEditorFunc = "document.getElementById('code-area').click()";
                    runJsFunc(clickEditorFunc);
                }
            }, delayMillis);

            // hide keyboard
            for (int i = 1; i < 8; i++) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hidePhysicalKeyboard();
                        dispatchKeyStroke(KeyEvent.KEYCODE_DPAD_RIGHT);
                    }
                }, delayMillis * i);
            }

            // then...
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // deselect current code
                    dispatchKeyStroke(KeyEvent.KEYCODE_DPAD_RIGHT);

                    // then send user code to editor
                    KeyCharacterMap charMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
                    for (String line : code) {
                        if (line != null) {
                            char[] szRes = line.toCharArray();
                            KeyEvent[] events = charMap.getEvents(szRes);
                            for (KeyEvent event : events) {
                                mGameLevelWebView.dispatchKeyEvent(event);
                            }
                        } else {
                            dispatchKeyStroke(KeyEvent.KEYCODE_DEL);
                        }
                    }


                }
            }, delayMillis * 5);

            // run the code
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String runFunc = "document.getElementsByClassName('cast-button')[0].click()";
                    runJsFunc(runFunc);
                }
            }, delayMillis * 6);
        }

        mPrevCode = code;
    }

    private void hidePhysicalKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(mGameLevelWebView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private void dispatchKeyStroke(final int code) {
        mGameLevelWebView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, code));
        mGameLevelWebView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, code));
    }

    private void runJsFunc(String function) {
        String url = "javascript:(function() { " + function + "})()";
        mGameLevelWebView.loadUrl(url);
    }

    private class CCWebClient extends WebViewClient {

        private boolean mIsPageFinished = false;

        void setPageFinished() {
            mIsPageFinished = true;
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
                                "document.getElementById('code-area').style.opacity='0';" +
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
