package tcd.android.com.codecombatmobile.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.transition.Explode;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import tcd.android.com.codecombatmobile.R;

@SuppressLint("Registered")
public abstract class ClassroomListFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = ClassroomListFragment.class.getSimpleName();

    private Toolbar mToolbar;

    protected void configureActionBar(View view) {
        Activity activity = (Activity) view.getContext();
        mToolbar = activity.findViewById(R.id.toolbar);
        if (mToolbar != null) {
            // this title does not suit the class name, but who cares
            mToolbar.setTitle(R.string.class_list_activity_title);

            int elevation = (int) getResources().getDimension(R.dimen.search_toolbar_elevation);
            ViewCompat.setElevation(mToolbar, elevation);
        }
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        menu.clear();
//        inflater.inflate(R.menu.menu_search, menu);
//
//        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
//        searchView.setSearchableInfo(searchManager != null ? searchManager.getSearchableInfo(getComponentName()) : null);
//        searchView.setOnQueryTextListener(this);
//        searchView.setIconifiedByDefault(false);
//    }

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
