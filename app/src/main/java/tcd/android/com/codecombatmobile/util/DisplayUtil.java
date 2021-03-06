package tcd.android.com.codecombatmobile.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_ASSIGNMENT;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_BLANK;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_COMMENT;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_DECLARATION;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_FLOW_CONTROL;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_FUNCTION;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_METHOD;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_OPERATOR;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_VALUE;
import static tcd.android.com.codecombatmobile.ui.widget.CodeEditor.syntax.Operation.TYPE_VARIABLE;

/**
 * Created by ADMIN on 22/04/2018.
 */

@SuppressWarnings("WeakerAccess")
public class DisplayUtil {

    public static String capitalize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static int dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    @NonNull
    public static Bitmap darkenBitmap(@NonNull Bitmap src) {
        Bitmap result = src.copy(src.getConfig(), true);
        Canvas canvas = new Canvas(result);
        Paint p = new Paint(Color.RED);

        ColorFilter filter = new LightingColorFilter(0xFF555555, 0x00000000);    // darken
        p.setColorFilter(filter);
        canvas.drawBitmap(result, new Matrix(), p);

        return result;
    }

    @Nullable
    public static Drawable getDrawable(Context context, @DrawableRes int drawableResId, int tintColor) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, tintColor);
        }
        return drawable;
    }

    public static LinearLayout getVerticalLinearLayout(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.setMargins(0, 0, dpToPx(context, 8), 0);
        layout.setLayoutParams(params);

        return layout;
    }


    // show/hide view helper
    public static void hideActionBar(@NonNull AppCompatActivity activity) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public static void hidePhysicalKeyboard(@NonNull View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showPhysicalKeyboard(@NonNull View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }


    // syntax buttons helper
    public static int getButtonColor(Context context, @Operation.SyntaxType int type) {
        return getColor(context, type, true);
    }

    public static int getCodeColor(Context context, @Operation.SyntaxType int type) {
        return getColor(context, type, false);
    }

    private static int getColor(Context context, @Operation.SyntaxType int type, boolean isButtonColor) {
        int colorResId;
        switch (type) {
            case TYPE_FLOW_CONTROL:
                colorResId = isButtonColor ? R.color.flow_control_button_color : R.color.flow_control_editor_color;
                break;
            case TYPE_DECLARATION:
                colorResId = isButtonColor ? R.color.declaration_button_color : R.color.declaration_editor_color;
                break;
            case TYPE_VARIABLE:
                colorResId = isButtonColor ? R.color.variable_button_color : R.color.variable_editor_color;
                break;
            case TYPE_FUNCTION:
                colorResId = isButtonColor ? R.color.function_button_color : R.color.function_editor_color;
                break;
            case TYPE_METHOD:
                colorResId = isButtonColor ? R.color.method_button_color : R.color.method_editor_color;
                break;
            case TYPE_VALUE:
                colorResId = isButtonColor ? R.color.value_button_color : R.color.value_editor_color;
                break;
            case TYPE_ASSIGNMENT:
            case TYPE_OPERATOR:
                colorResId = isButtonColor ? R.color.operator_button_color : R.color.operator_editor_color;
                break;
            case TYPE_BLANK:
            case TYPE_COMMENT:
                colorResId = R.color.keyboard_button_color;
                break;
            default:
                throw new IllegalArgumentException("Unknown color type");
        }
        return ContextCompat.getColor(context, colorResId);
    }


    // screen's size helper
    public static Point getScreenSize(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static int getStatusBarHeight(Resources resources) {
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getNavBarHeight(Resources resources) {
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
