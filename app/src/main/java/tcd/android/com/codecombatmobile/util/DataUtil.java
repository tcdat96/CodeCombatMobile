package tcd.android.com.codecombatmobile.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

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
import tcd.android.com.codecombatmobile.data.User.Student;
import tcd.android.com.codecombatmobile.data.User.Teacher;
import tcd.android.com.codecombatmobile.data.User.User;

/**
 * Created by ADMIN on 22/04/2018.
 */

public class DataUtil {
    private static final String TAG = DataUtil.class.getSimpleName();

    private static int[] sPythonCovers = new int[] {R.drawable.cover_python_1, R.drawable.cover_py_2};
    private static int[] sJavascriptCovers = new int[] {R.drawable.cover_js_1, R.drawable.cover_js_2, R.drawable.cover_js_3};
    @DrawableRes
    public static int getLanguageCoverRes(TeacherClass teacherClass) {
        int hashCode = Math.abs(teacherClass.getClassCode().hashCode());
        switch (teacherClass.getLanguage().toLowerCase()) {
            case "python":
                return sPythonCovers[hashCode % sPythonCovers.length];
            case "js": case "javascript":
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


    // debug helper methods
    @SuppressWarnings("SameParameterValue")
    public static List<StudentClass> getDebugStudentClassList(int total) {
        Random random = new Random();
        String[] languages = new String[] {"python", "javascript", "python"};
        String[] classNames = new String[] {"Python Introduction", "Python Syntax", "Introduction to Python"};
        String[] teachers = new String[] {"Michael Keaton", "Alex Garland", "Emma Stone"};

        List<StudentClass> classes = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            int idx = i % classNames.length;
            StudentClass newClass = new StudentClass(String.valueOf(idx), languages[idx], classNames[idx], teachers[idx]);
            newClass.setCourseName("Introduction to Computer Science");
            newClass.setProgress(random.nextInt(Integer.MAX_VALUE) % 100);
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
            TeacherClass newClass = new TeacherClass(languages[idx], classNames[idx], "FunnyNameMore", progress, studentTotal,
                    "4/16/2018", playtimeTotal, levelTotal);
            newClass.setStudentList(students);
            classes.add(newClass);
        }
        return classes;
    }
}
