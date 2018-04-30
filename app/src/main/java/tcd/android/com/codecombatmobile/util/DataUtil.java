package tcd.android.com.codecombatmobile.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tcd.android.com.codecombatmobile.data.StudentClass;
import tcd.android.com.codecombatmobile.data.TeacherClass;

/**
 * Created by ADMIN on 22/04/2018.
 */

public class DataUtil {
    private static final String TAG = DataUtil.class.getSimpleName();

//    public static Operation getFunctionCodeLine(String funcName, int paramTotal) {
//        StringBuilder syntax = new StringBuilder(funcName.substring(0, funcName.length() - 1));
//        if (paramTotal > 0) {
//            for (int i = 0; i < paramTotal; i++) {
//                syntax.append("___").append(i < paramTotal - 1 ? ", " : ")");
//            }
//        } else {
//            syntax.append(")");
//        }
//        Spannable span = Spannable.Factory.getInstance().newSpannable(syntax);
//
//        if (paramTotal > 0) {
//            int start = funcName.length();
//            int leap = "___, ".length();
//            for (; start < syntax.length(); start += leap) {
//                ClickableSpan cs = new ClickableSpan() {
//                    @Override
//                    public void onClick(View widget) {
//                        Log.d(TAG, "onClick: ");
//                    }
//                };
//                span.setSpan(cs, start, start + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//        }
//
//        ArrayList<Object> operands = new ArrayList<>();
//        operands.add(span);
//        return new Operation(operands);
//    }

//    public static Operation getForCodeLine() {
//        return null;
//    }

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

        List<TeacherClass> classes = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            int idx = i % classNames.length;
            int progress = random.nextInt(Integer.MAX_VALUE) % 100;
            int studentTotal = random.nextInt(100);
            int playtimeTotal = random.nextInt(180);
            int levelTotal = random.nextInt(12);
            TeacherClass newClass = new TeacherClass(languages[idx], classNames[idx], progress,
                    studentTotal, playtimeTotal, levelTotal);
            classes.add(newClass);
        }
        return classes;
    }
}
