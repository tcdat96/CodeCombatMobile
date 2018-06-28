package tcd.android.com.codecombatmobile.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.user.Student;
import tcd.android.com.codecombatmobile.data.user.User;
import tcd.android.com.codecombatmobile.ui.fragment.ProfileFragment;
import tcd.android.com.codecombatmobile.ui.fragment.SClassroomListFragment;
import tcd.android.com.codecombatmobile.ui.fragment.TClassroomListFragment;
import tcd.android.com.codecombatmobile.util.CCRequestManager;
import tcd.android.com.codecombatmobile.util.DataUtil;
import tcd.android.com.codecombatmobile.util.DisplayUtil;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_LOGIN_ATTEMPT = 0;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    boolean mIsStudent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayUtil.hideActionBar(this);

        initUiComponents();

        boolean isDebugging = false;
        if (!isDebugging) {
            Intent intent = new Intent(this, SplashScreenActivity.class);
            startActivityForResult(intent, RC_LOGIN_ATTEMPT);
        } else {
            initDebugButtonList();
        }
    }

    private void initDebugButtonList() {
        LinearLayout rootContainer = findViewById(R.id.ll_root);
        Class[] activities = new Class[]{
                LoginActivity.class,
                MoreInfoTeacherActivity.class,
                SClassroomListFragment.class,
                TClassroomListFragment.class,
                ClassroomDetailActivity.class,
                CodeEditorActivity.class,
                SettingsActivity.class,
                GameMapActivity.class,
                ProfileFragment.class
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

//            startActivity(new Intent(this, ProfileFragment.class));
    }

    private void initUiComponents() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_LOGIN_ATTEMPT:
                if (resultCode == RESULT_OK) {
                    User user = DataUtil.getUserData(this);
                    mIsStudent = user instanceof Student;
                    replaceFragment(R.id.nav_classrooms);
                } else {
                    finish();
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.nav_classrooms:
            case R.id.nav_profile:
                replaceFragment(id);
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_about:
                String aboutUrl = CCRequestManager.getInstance(this).getAboutUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(aboutUrl));
                startActivity(i);
                break;
            default:
                throw new UnsupportedOperationException("Unknown selected item: " + item.getTitle());
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(int navItemId) {
        Fragment fragment;
        switch (navItemId) {
            case R.id.nav_classrooms:
                fragment = mIsStudent ?
                        SClassroomListFragment.newInstance() :
                        TClassroomListFragment.newInstance();
                break;
            case R.id.nav_profile:
                fragment = ProfileFragment.newInstance();
                break;
            default:
                throw new IllegalArgumentException("Unknown navigation item ID");
        }

        // highlight item
        mNavigationView.setCheckedItem(navItemId);

        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.fl_content, fragment)
                .commitAllowingStateLoss();
    }
}
