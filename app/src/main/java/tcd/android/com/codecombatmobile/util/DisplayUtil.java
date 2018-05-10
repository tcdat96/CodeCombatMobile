package tcd.android.com.codecombatmobile.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.widget.LinearLayout;

import tcd.android.com.codecombatmobile.data.Syntax.Operation;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static tcd.android.com.codecombatmobile.data.Syntax.Operation.TYPE_BLANK;
import static tcd.android.com.codecombatmobile.data.Syntax.Operation.TYPE_FLOW_CONTROL;
import static tcd.android.com.codecombatmobile.data.Syntax.Operation.TYPE_DECLARATION;
import static tcd.android.com.codecombatmobile.data.Syntax.Operation.TYPE_FUNCTION;
import static tcd.android.com.codecombatmobile.data.Syntax.Operation.TYPE_METHOD;
import static tcd.android.com.codecombatmobile.data.Syntax.Operation.TYPE_OPERATOR;
import static tcd.android.com.codecombatmobile.data.Syntax.Operation.TYPE_VALUE;
import static tcd.android.com.codecombatmobile.data.Syntax.Operation.TYPE_VARIABLE;

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
}
