package tcd.android.com.codecombatmobile.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.game.Achievement;
import tcd.android.com.codecombatmobile.data.game.Playtime;
import tcd.android.com.codecombatmobile.data.game.Session;
import tcd.android.com.codecombatmobile.data.user.ProfileGeneral;
import tcd.android.com.codecombatmobile.data.user.User;
import tcd.android.com.codecombatmobile.ui.adapter.ProfileAchievementAdapter;
import tcd.android.com.codecombatmobile.ui.adapter.ProfileSessionAdapter;
import tcd.android.com.codecombatmobile.ui.widget.CalendarGraph;
import tcd.android.com.codecombatmobile.util.CCDataUtil;
import tcd.android.com.codecombatmobile.util.CCRequestManager;
import tcd.android.com.codecombatmobile.util.DataUtil;
import tcd.android.com.codecombatmobile.util.TimeUtil;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final int CHART_ANIMATION_DURATION = (int) TimeUnit.SECONDS.toMillis(1);

    private NestedScrollView mRootNsv;
    private RecyclerView mAcmListRv;
    private RecyclerView mSessionListRv;
    private BarChart mChart;

    private Context mContext;
    
    @Nullable
    private GetProfileTask mInitProfileTask;

    private ProfileGeneral mProfileGeneral;
    @NonNull
    List<Session> mSessions = new ArrayList<>();
    @NonNull
    List<Achievement> mAchievements = new ArrayList<>();

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mContext = view.getContext();

        initUiComponents(view);

        mInitProfileTask = new GetProfileTask();
        mInitProfileTask.execute();

        return view;
    }

    private void initUiComponents(View view) {
        mRootNsv = view.findViewById(R.id.nsv_root);
        mChart = view.findViewById(R.id.bc_playtime);

        configureActionBar(view);
    }

    private void configureActionBar(View view) {
        Activity activity = (Activity) view.getContext();
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.profile_title);
        }
    }

    private void initProfileGeneral() {
        showHeroAvatar();

        // username
        TextView firstNameTextView = mRootNsv.findViewById(R.id.tv_user_first_name);
        firstNameTextView.setText(mProfileGeneral.getUsername());

        // current user level
        TextView levelTextView = mRootNsv.findViewById(R.id.tv_user_level);
        String level = String.format(getString(R.string.user_level), mProfileGeneral.getLevel());
        levelTextView.setText(level);

        // total completed levels
        TextView completedLevelsTextView = mRootNsv.findViewById(R.id.tv_completed_level_total);
        completedLevelsTextView.setOnClickListener(this);
        String completedLevelTotal = String.valueOf(mProfileGeneral.getSingleplayer() + mProfileGeneral.getMultiplayer());
        completedLevelsTextView.setText(completedLevelTotal);

        Playtime playtime = TimeUtil.getDisplayPlaytime(mContext, mProfileGeneral.getPlaytime());
        // total playtime
        TextView playtimeTextView = mRootNsv.findViewById(R.id.tv_playtime);
        playtimeTextView.setOnClickListener(this);
        String playtimeVal = new DecimalFormat("#.#").format(playtime.getValue());
        playtimeTextView.setText(playtimeVal);
        // playtime unit
        TextView unitTextView = mRootNsv.findViewById(R.id.tv_playtime_unit);
        unitTextView.setOnClickListener(this);
        unitTextView.setText(playtime.getUnit());

        // achievement total
        TextView acmTextView = mRootNsv.findViewById(R.id.tv_achievement_total);
        String acmTotal = String.valueOf(mProfileGeneral.getAchievementTotal());
        acmTextView.setText(acmTotal);

        // course total
        TextView courseTextView = mRootNsv.findViewById(R.id.tv_course_total);
        String courseTotal = String.valueOf(mProfileGeneral.getCourseTotal());
        courseTextView.setText(courseTotal);

        // singleplayer level count
        TextView spCountTextView = mRootNsv.findViewById(R.id.tv_singleplayer_level_count);
        String singleplayerCount = getResources().getQuantityString(R.plurals.level_total,
                mProfileGeneral.getSingleplayer(), mProfileGeneral.getSingleplayer());
        spCountTextView.setText(singleplayerCount);

        // multiplayer level count
        TextView mpCountTextView = mRootNsv.findViewById(R.id.tv_multiplayer_level_count);
        String multiplayerCount = getResources().getQuantityString(R.plurals.level_total,
                mProfileGeneral.getMultiplayer(), mProfileGeneral.getMultiplayer());
        mpCountTextView.setText(multiplayerCount);

        // onClickListener
        mRootNsv.findViewById(R.id.cv_achievement_total_container).setOnClickListener(this);
    }

    private void showHeroAvatar() {
        final ImageView heroPictureImageView = mRootNsv.findViewById(R.id.iv_hero_avatar);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder_hero_avatar)
                .error(R.drawable.placeholder_hero_avatar);

        Glide.with(this)
                .load(mProfileGeneral.getHeroPictureUrl())
                .apply(options)
                .into(heroPictureImageView);
    }

    private void initAchievementList() {
        mAcmListRv = mRootNsv.findViewById(R.id.rv_achievement_list);
        mAcmListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAcmListRv.setItemAnimator(new DefaultItemAnimator());
        mAcmListRv.setNestedScrollingEnabled(false);

        ProfileAchievementAdapter adapter = new ProfileAchievementAdapter(mAchievements);
        mAcmListRv.setAdapter(adapter);
    }

    private void initLevelSessionList() {
        mSessionListRv = mRootNsv.findViewById(R.id.rv_session_list);
        mSessionListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mSessionListRv.setItemAnimator(new DefaultItemAnimator());
        mSessionListRv.setNestedScrollingEnabled(false);

        ProfileSessionAdapter adapter = new ProfileSessionAdapter(mSessions);
        mSessionListRv.setAdapter(adapter);
    }

    private void initPlaytimeGraph() {
        initLineChart();
        populateLineChart();
    }

    private void initTrackRecordGraph() {
        CalendarGraph graph = mRootNsv.findViewById(R.id.cg_track_record);
        graph.setLevelSessions(mSessions);
    }

    private void initLineChart() {
        mChart.setViewPortOffsets(0, 0, 0, 0);

        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        mChart.setMaxHighlightDistance(300);

        mChart.setFitBars(true);

        YAxis y = mChart.getAxisLeft();
        y.setLabelCount(6, false);
        y.setTextColor(Color.BLACK);
        y.setTextSize(10);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(true);
        y.setAxisLineColor(Color.TRANSPARENT);

        // disable unused stuff
        mChart.getAxisRight().setEnabled(false);
        mChart.getDescription().setEnabled(false);
        mChart.getLegend().setEnabled(false);

        animateChartY(CHART_ANIMATION_DURATION);
    }

    private void populateLineChart() {
        Pair<List<BarEntry>, List<String>> result = getValueEntries();
        List<BarEntry> entries = result.first;
        final List<String> labels = result.second;

        BarDataSet dataSet = getDataSet(entries);

        BarData data = new BarData(dataSet);
        data.setValueTextSize(9f);
        data.setDrawValues(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get((int) value);
            }
        });

        mChart.setData(data);
        mChart.invalidate();
    }

    private Pair<List<BarEntry>, List<String>> getValueEntries() {
        List<String> labels = new ArrayList<>();
        List<BarEntry> entries = new ArrayList<>();

        Collections.sort(mSessions, new Comparator<Session>() {
            @Override
            public int compare(Session o1, Session o2) {
                return compare(o1.getTimeChanged(), o2.getTimeChanged());
            }
            @SuppressWarnings("UseCompareMethod")
            private int compare(long x, long y) {
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        });

        // create graph values
        String prevDate = TimeUtil.getDayMonthString(mSessions.get(0).getTimeChanged());
        int yVal = 0;
        for (int index = 0; index < mSessions.size(); index++) {
            Session session = mSessions.get(index);

            String curDate = TimeUtil.getDayMonthString(session.getTimeChanged());
            if (!curDate.equals(prevDate) || index == mSessions.size() - 1) {
                BarEntry entry = new BarEntry(entries.size(), yVal);
                entries.add(entry);
                labels.add(prevDate);

                if (index < mSessions.size() - 1) {
                    yVal = session.getPlaytime();
                    prevDate = curDate;
                } else {
                    entries.add(new BarEntry(entries.size(), session.getPlaytime()));
                    labels.add(curDate);
                }
            } else {
                yVal += session.getPlaytime();
            }
        }

        return new Pair<>(entries, labels);
    }

    private BarDataSet getDataSet(List<BarEntry> entries) {
        BarDataSet set = new BarDataSet(entries, TAG);

        int color = ContextCompat.getColor(mContext, R.color.playtime_graph_color);
        set.setColor(color);

        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setBarShadowColor(Color.rgb(203, 203, 203));

        return set;
    }

    private void animateChartY(int duration) {
        mChart.animateY(duration, Easing.EasingOption.EaseInCubic);
        mChart.invalidate();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mInitProfileTask != null) {
            mInitProfileTask.cancel(true);
            mInitProfileTask = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_achievement_total_container:
//                mRootNsv.smoothScrollTo(0, mAcmListRv.getTop());
//                animateChartY(CHART_ANIMATION_DURATION);
                break;
            case R.id.tv_playtime:
            case R.id.tv_playtime_unit:
//                mRootNsv.smoothScrollTo(0, mChart.getTop());
                break;
            case R.id.tv_completed_level_total:
            case R.id.tv_completed_level_total_title:
//                mRootNsv.smoothScrollTo(0, mSessionListRv.getTop());
                break;
        }
    }

    private class GetProfileTask extends AsyncTask<Void, Void, Boolean> {

        CCRequestManager mManager;

        GetProfileTask() {
            mManager = CCRequestManager.getInstance(getContext());
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            User user = DataUtil.getUserData(getContext());
            if (user != null && mManager != null) {
                try {
                    // level sessions
                    String userId = user.getId();
                    JSONArray sessionArr = mManager.requestLevelSessionsSync(userId);
                    if (sessionArr == null) {
                        return false;
                    }
                    mSessions = CCDataUtil.parseLevelSessions(sessionArr);

                    // sort sessions with time
                    Collections.sort(mSessions, new Comparator<Session>() {
                        @Override
                        public int compare(Session o1, Session o2) {
                            return (int) (o1.getTimeChanged() - o2.getTimeChanged());
                        }
                    });

                    // achievements
                    JSONArray acmArr = mManager.requestAchievementsSync(userId);
                    if (acmArr == null) {
                        return false;
                    }
                    mAchievements = CCDataUtil.parseAchievements(acmArr);

                    // basic info
                    JSONObject profileObj = mManager.requestUserProfile(userId);
                    if (profileObj == null) {
                        return false;
                    }
                    mProfileGeneral = CCDataUtil.parseProfileGeneral(profileObj);
                    mProfileGeneral.setAchievementTotal(mAchievements.size());

                    // if playtime is still zero, we should manually calculate it from each level
                    if (mProfileGeneral.getPlaytime() == 0) {
                        int playtime = 0;
                        for (Session session : mSessions) {
                            playtime += session.getPlaytime();
                        }
                        mProfileGeneral.setPlaytime(playtime);
                    }

                    // hero picture url
                    String url = mManager.getUserAvatar(userId);
                    mProfileGeneral.setHeroPictureUrl(url);

                    // calculate completed levels
                    int singleplayerCount = 0, multiplayerCount = 0;
                    for (Session session : mSessions) {
                        if (session.isCompleted()) {
                            if (session.getTotalScore() > 0) {
                                multiplayerCount++;
                            } else {
                                singleplayerCount++;
                            }
                        }
                    }
                    mProfileGeneral.setCompletedLevels(singleplayerCount, multiplayerCount);

                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if (success) {
                initProfileGeneral();
                initPlaytimeGraph();
                initTrackRecordGraph();
                initAchievementList();
                initLevelSessionList();
            } else {
                Toast.makeText(mContext, R.string.error_get_data_message, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mInitProfileTask = null;
        }
    }
}
