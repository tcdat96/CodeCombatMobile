package tcd.android.com.codecombatmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import tcd.android.com.codecombatmobile.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout rootContainer = findViewById(R.id.ll_root);
        Class[] activities = new Class[]{
                LoginActivity.class,
                MoreInfoTeacherActivity.class,
                StudentClassActivity.class,
                TeacherClassActivity.class,
                ClassDetailActivity.class,
                CodeEditorActivity.class,
                SettingsActivity.class,
                GameActivity.class
        };
        for (final Class activity : activities) {
            Button button = new Button(this);
            String name = activity.getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2");
            button.setText(name);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, activity));
                }
            });
            rootContainer.addView(button);
        }

        startActivity(new Intent(MainActivity.this, TeacherClassActivity.class));
    }
}
