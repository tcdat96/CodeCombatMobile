package tcd.android.com.codecombatmobile.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Response;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.course.TClassroom;
import tcd.android.com.codecombatmobile.data.user.User;
import tcd.android.com.codecombatmobile.ui.adapter.TClassroomAdapter;
import tcd.android.com.codecombatmobile.util.CCDataUtil;
import tcd.android.com.codecombatmobile.util.CCRequestManager;
import tcd.android.com.codecombatmobile.util.DataUtil;

public class TClassroomListFragment extends ClassroomListFragment {

    private List<TClassroom> mClassrooms = new ArrayList<>();
    private TClassroomAdapter mAdapter;
    
    private Context mContext;

    private ProgressBar mLoadingProgressBar;

    public static TClassroomListFragment newInstance() {
        Bundle args = new Bundle();
        TClassroomListFragment fragment = new TClassroomListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classroom_list, container, false);
        configureActionBar(view);

        mContext = view.getContext();

        initUiComponents(view);

        initClassroomRecyclerView(view);
        requestClassroomList();

        return view;
    }

    private void initUiComponents(View view) {
        mLoadingProgressBar = view.findViewById(R.id.pb_loading);

        view.findViewById(R.id.fab_add).setVisibility(View.GONE);
    }


    private void initClassroomRecyclerView(View view) {
        RecyclerView classroomListRv = view.findViewById(R.id.rv_student_classes);
        classroomListRv.setLayoutManager(new LinearLayoutManager(mContext));
        classroomListRv.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new TClassroomAdapter(mContext, mClassrooms);
        classroomListRv.setAdapter(mAdapter);
    }

    private void requestClassroomList() {
        User user = DataUtil.getUserData(mContext);
        if (user != null) {
            CCRequestManager.getInstance(mContext).requestTeacherClassList(user.getId(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    if (response != null) {
                        List<TClassroom> classrooms = CCDataUtil.getTClassroomList(response);
                        mClassrooms.addAll(classrooms);
                        mAdapter.notifyDataSetChanged();

                        mLoadingProgressBar.setVisibility(View.GONE);
                    }
                }
            }, null);
        }
    }
}
