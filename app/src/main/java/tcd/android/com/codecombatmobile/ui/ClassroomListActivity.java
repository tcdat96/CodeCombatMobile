package tcd.android.com.codecombatmobile.ui;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.os.Bundle;
import android.support.transition.Explode;
import android.support.transition.TransitionManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import tcd.android.com.codecombatmobile.R;

@SuppressLint("Registered")
public abstract class ClassroomListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = ClassroomListActivity.class.getSimpleName();

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
    }

    protected void configureActionBar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // this title does not suit the class name, but who cares
            actionBar.setTitle(R.string.class_list_activity_title);

            int elevation = (int) getResources().getDimension(R.dimen.search_toolbar_elevation);
            ViewCompat.setElevation(mToolbar, elevation);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager != null ? searchManager.getSearchableInfo(getComponentName()) : null);
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_search:
                TransitionManager.beginDelayedTransition(mToolbar, new Explode().setDuration(50));
                item.expandActionView();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit: " + query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange: " + newText);
        return false;
    }
}
