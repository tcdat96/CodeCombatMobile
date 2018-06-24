package tcd.android.com.codecombatmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.user.Student;
import tcd.android.com.codecombatmobile.data.user.User;
import tcd.android.com.codecombatmobile.util.DataUtil;

import static tcd.android.com.codecombatmobile.ui.AccountRequestActivity.RESULT_FAILED;

public class MainActivity extends AppCompatActivity {

    private static final int RC_LOGIN_ATTEMPT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isDebugging = true;
        if (!isDebugging) {
            startActivityForResult(new Intent(this, LoginActivity.class), RC_LOGIN_ATTEMPT);
        } else {
            LinearLayout rootContainer = findViewById(R.id.ll_root);
            Class[] activities = new Class[]{
                    LoginActivity.class,
                    MoreInfoTeacherActivity.class,
                    SClassroomListActivity.class,
                    TClassroomListActivity.class,
                    ClassroomDetailActivity.class,
                    CodeEditorActivity.class,
                    SettingsActivity.class,
                    GameMapActivity.class,
                    ProfileActivity.class
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

//            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_LOGIN_ATTEMPT:
                if (resultCode == RESULT_OK) {
                    User user = DataUtil.getUserData(this);
                    Intent intent = new Intent(this,
                            user instanceof Student ? SClassroomListActivity.class : TClassroomListActivity.class);
                    startActivity(intent);
                } else if (resultCode == RESULT_FAILED) {
                    Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
