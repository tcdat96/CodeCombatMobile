package tcd.android.com.codecombatmobile.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.style.CharacterStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.course.TClassroom;
import tcd.android.com.codecombatmobile.data.user.Student;
import tcd.android.com.codecombatmobile.data.user.Teacher;
import tcd.android.com.codecombatmobile.data.user.User;

/**
 * Created by ADMIN on 22/04/2018.
 */

public class DataUtil {
    private static int[] sPythonCovers = new int[]{R.drawable.cover_python_1, R.drawable.cover_py_2};
    private static int[] sJavascriptCovers = new int[]{R.drawable.cover_js_1, R.drawable.cover_js_2, R.drawable.cover_js_3};

    @DrawableRes
    public static int getLanguageCoverRes(TClassroom teacherClass) {
        int hashCode = Math.abs(teacherClass.getClassCode().hashCode());
        switch (teacherClass.getLanguage().toLowerCase()) {
            case "python":
                return sPythonCovers[hashCode % sPythonCovers.length];
            case "js":
            case "javascript":
                return sJavascriptCovers[hashCode % sJavascriptCovers.length];
            default:
                throw new IllegalArgumentException("Not supported language");
        }
    }


    // save and get data using shared preferences
    public static void saveUserData(Context context, @NonNull User user) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.pref_user_id_key), user.getId());
        editor.putString(context.getString(R.string.pref_email_key), user.getEmail());
        if (user instanceof Teacher) {
            Teacher teacher = (Teacher) user;
            editor.putString(context.getString(R.string.pref_user_role_key), Teacher.class.getSimpleName());
            editor.putString(context.getString(R.string.pref_first_name_key), teacher.getFirstName());
            editor.putString(context.getString(R.string.pref_last_name_key), teacher.getLastName());
        } else {
            Student student = (Student) user;
            editor.putString(context.getString(R.string.pref_user_role_key), Student.class.getSimpleName());
            editor.putString(context.getString(R.string.pref_username_key), student.getUsername());
        }
        editor.apply();
    }

    @Nullable
    public static User getUserData(Context context) {
        User user = null;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String uid = sharedPref.getString(context.getString(R.string.pref_user_id_key), "");
        String email = sharedPref.getString(context.getString(R.string.pref_email_key), "");
        String role = sharedPref.getString(context.getString(R.string.pref_user_role_key), "");
        if (role.equalsIgnoreCase(Teacher.class.getSimpleName())) {
            // if it is teacher
            String firstName = sharedPref.getString(context.getString(R.string.pref_first_name_key), "");
            String lastName = sharedPref.getString(context.getString(R.string.pref_last_name_key), "");
            user = new Teacher(email, firstName, lastName);
            user.setId(uid);
        } else if (role.equalsIgnoreCase(Student.class.getSimpleName())) {
            // or a student
            String username = sharedPref.getString(context.getString(R.string.pref_username_key), "");
            user = new Student(email, username);
            user.setId(uid);
        }
        return user;
    }

    @SuppressLint("ApplySharedPref")
    public static void removeUserData(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.remove(context.getString(R.string.pref_user_id_key));
        editor.remove(context.getString(R.string.pref_email_key));

        String roleKey = context.getString(R.string.pref_user_role_key);
        String role = sharedPref.getString(roleKey, "");
        editor.remove(roleKey);

        if (role.equalsIgnoreCase(Teacher.class.getSimpleName())) {
            editor.remove(context.getString(R.string.pref_first_name_key));
            editor.remove(context.getString(R.string.pref_last_name_key));
        } else if (role.equalsIgnoreCase(Student.class.getSimpleName())) {
            editor.remove(context.getString(R.string.pref_username_key));
        }

        // commit is required to take effect immediately
        editor.commit();
    }


    // misc
    public static void removeAllSpans(Spannable spannable) {
        Object spansToRemove[] = spannable.getSpans(0, spannable.length(), Object.class);
        for (Object span : spansToRemove) {
            if (span instanceof CharacterStyle) {
                spannable.removeSpan(span);
            }
        }
    }

    public static boolean isVarNameValid(String name) {
        // first character
        char firstChar = name.charAt(0);
        if (!Character.isLetter(firstChar) && firstChar != '_') {
            return false;
        }

        // other characters
        for (int i = 1; i < name.length(); i++) {
            char character = name.charAt(i);
            if (!Character.isLetterOrDigit(character) && character != '_') {
                return false;
            }
        }

        // TODO: 20/06/2018 support for other languages
        // reserved keywords
        List<String> reservedKeywords = new ArrayList<>(Arrays.asList(
                "False", "class", "finally", "is", "return", "None", "continue", "for", "lambda", "try", "True", "def", "from", "nonlocal",
                "while", "and", "del", "global", "not", "with", "as", "elif", "if", "or", "yield", "assert", "else", "import", "pass",
                "break", "except", "in", "raise"));
        return !reservedKeywords.contains(name);
    }
}
