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
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Display;
import android.widget.LinearLayout;

import tcd.android.com.codecombatmobile.data.syntax.Operation;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_ASSIGNMENT;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_BLANK;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_DECLARATION;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_FLOW_CONTROL;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_FUNCTION;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_METHOD;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_OPERATOR;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_VALUE;
import static tcd.android.com.codecombatmobile.data.syntax.Operation.TYPE_VARIABLE;

/**
 * Created by ADMIN on 22/04/2018.
 */

@SuppressWarnings("WeakerAccess")
public class DisplayUtil {

    public static String capitalize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static int getColor(@Operation.SyntaxType int type) {
        switch (type) {
            case TYPE_FLOW_CONTROL:
                return Color.parseColor("#FF647E");
            case TYPE_DECLARATION:
                return Color.parseColor("#657DFF");
            case TYPE_VARIABLE:
                return Color.parseColor("#73B3C1");
            case TYPE_FUNCTION:
                return Color.parseColor("#309CE8");
            case TYPE_METHOD:
                return Color.parseColor("#2AE48A");
            case TYPE_VALUE:
                return Color.parseColor("#F9A825");
            case TYPE_ASSIGNMENT:
            case TYPE_OPERATOR:
                return Color.parseColor("#F86AFF");
            case TYPE_BLANK:
                return Color.parseColor("#5E666A");
            default:
                throw new IllegalArgumentException("Unknown color type");
        }
    }

    public static int dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static Bitmap darkenBitmap(@NonNull Bitmap src) {
        Bitmap result = src.copy(src.getConfig(), true);
        Canvas canvas = new Canvas(result);
        Paint p = new Paint(Color.RED);

        ColorFilter filter = new LightingColorFilter(0xFF555555, 0x00000000);    // darken
        p.setColorFilter(filter);
        canvas.drawBitmap(result, new Matrix(), p);

        return result;
    }

    public static LinearLayout getVerticalLinearLayout(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.setMargins(0, 0, dpToPx(context, 8), 0);
        layout.setLayoutParams(params);

        return layout;
    }

    public static Point getScreenSize(Context context) {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
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
