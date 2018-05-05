package tcd.android.com.codecombatmobile.util;

import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.ClassStudent;
import tcd.android.com.codecombatmobile.data.StudentClass;
import tcd.android.com.codecombatmobile.data.TeacherClass;

/**
 * Created by ADMIN on 22/04/2018.
 */

public class DataUtil {
    private static final String TAG = DataUtil.class.getSimpleName();

    public static final int LANGUAGE_PYTHON = 0, LANGUAGE_JAVASCRIPT = 1;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LANGUAGE_PYTHON, LANGUAGE_JAVASCRIPT})
    public @interface LanguageType {}
    private static int[] sPythonCovers = new int[] {R.drawable.cover_python_1, R.drawable.cover_py_2};
    private static int[] sJavascriptCovers = new int[] {R.drawable.cover_js_1, R.drawable.cover_js_2, R.drawable.cover_js_3};

    @LanguageType
    public static int getLanguageType(@NonNull String language) {
        switch (language.trim().toLowerCase()) {
            case "python": return LANGUAGE_PYTHON;
            case "javascript":
            case "js":
                return LANGUAGE_JAVASCRIPT;
            default:
                throw new IllegalArgumentException("Not supported language");
        }
    }

    @DrawableRes
    public static int getLanguageCoverRes(@LanguageType int type) {
        Random random = new Random();
        switch (type) {
            case LANGUAGE_PYTHON: return sPythonCovers[random.nextInt(sPythonCovers.length)];
            case LANGUAGE_JAVASCRIPT: return sJavascriptCovers[random.nextInt(sJavascriptCovers.length)];
            default:
                throw new IllegalArgumentException("Not supported language");
        }
    }

    @SuppressWarnings("SameParameterValue")
    public static List<StudentClass> getDebugStudentClassList(int total) {
        Random random = new Random();
        String[] languages = new String[] {"python", "javascript", "python"};
        String[] classNames = new String[] {"Python Introduction", "Python Syntax", "Introduction to Python"};
        String[] teachers = new String[] {"Michael Keaton", "Alex Garland", "Emma Stone"};

        List<StudentClass> classes = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            int idx = i % classNames.length;
            int progress = random.nextInt(Integer.MAX_VALUE) % 100;
            StudentClass newClass = new StudentClass(languages[idx], classNames[idx], teachers[idx],
                    "Introduction to Computer Science", progress);
            classes.add(newClass);
        }
        return classes;
    }

    @SuppressWarnings("SameParameterValue")
    public static List<TeacherClass> getDebugTeacherClassList(int total) {
        Random random = new Random();
        String[] languages = new String[] {"python", "javascript", "python"};
        String[] classNames = new String[] {"Python Introduction", "Python Syntax", "Introduction to Python"};
        List<ClassStudent> students = new ArrayList<>(Arrays.asList(
                new ClassStudent("Thai Dat", "tcdat96@gmail.com", 59, "Introduction to Computer Science: Level20"),
                new ClassStudent("Cao Dung", "caodung@gmail.com", 76, "Introduction to Computer Science: Level20"),
                new ClassStudent("dc vh", "dungchoviechoc@gmail.com", 23, "Introduction to Computer Science: Level20")
        ));
        students.addAll(new ArrayList<>(students));
        students.addAll(new ArrayList<>(students));

        List<TeacherClass> classes = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            int idx = i % classNames.length;
            int progress = random.nextInt(Integer.MAX_VALUE) % 100;
            int studentTotal = random.nextInt(100);
            int playtimeTotal = random.nextInt(180);
            int levelTotal = random.nextInt(12);
            TeacherClass newClass = new TeacherClass(languages[idx], classNames[idx], progress, "4/16/2018",
                    studentTotal, playtimeTotal, levelTotal);
            newClass.setStudentList(students);
            classes.add(newClass);
        }
        return classes;
    }
}
