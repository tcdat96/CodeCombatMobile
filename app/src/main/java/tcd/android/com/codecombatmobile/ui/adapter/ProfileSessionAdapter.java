package tcd.android.com.codecombatmobile.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.game.Session;

public class ProfileSessionAdapter extends RecyclerView.Adapter<ProfileSessionAdapter.SessionViewHolder> {

    private List<Session> mSessions;
    private long mNow = System.currentTimeMillis();
    private Context mContext;

    public ProfileSessionAdapter(List<Session> sessions) {
        mSessions = sessions;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_profile_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = mSessions.get(position);
        holder.mLevelNameTextView.setText(session.getLevelName());

        String relativeTime = (String) DateUtils.getRelativeTimeSpanString(
                session.getTimeChanged(), mNow, 0L, DateUtils.FORMAT_ABBREV_ALL);
        holder.mTimeChangedTextView.setText(relativeTime);

        if (session.getTotalScore() > 0) {
            String totalScore = String.valueOf(session.getTotalScore());
            holder.mStatusTextView.setText(totalScore);
        } else {
            String status = mContext.getString(session.isCompleted() ? R.string.completed : R.string.unfinished);
            holder.mStatusTextView.setText(status);
            int color = ContextCompat.getColor(mContext, session.isCompleted() ? R.color.complete_level_color: R.color.keyboard_button_color);
            holder.mStatusTextView.setTextColor(color);
        }
    }

    @Override
    public int getItemCount() {
        return mSessions.size();
    }

    class SessionViewHolder extends RecyclerView.ViewHolder {
        private TextView mLevelNameTextView;
        private TextView mTimeChangedTextView;
        private TextView mStatusTextView;

        SessionViewHolder(View itemView) {
            super(itemView);
            mLevelNameTextView = itemView.findViewById(R.id.tv_level_name);
            mTimeChangedTextView = itemView.findViewById(R.id.tv_time_changed);
            mStatusTextView = itemView.findViewById(R.id.tv_level_status);
        }
    }
}
