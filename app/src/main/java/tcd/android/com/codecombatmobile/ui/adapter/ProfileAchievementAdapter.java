package tcd.android.com.codecombatmobile.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.game.Achievement;

public class ProfileAchievementAdapter extends RecyclerView.Adapter<ProfileAchievementAdapter.AchievementViewHolder> {

    private List<Achievement> mAchievements;
    private Context mContext;

    public ProfileAchievementAdapter(List<Achievement> Achievement) {
        mAchievements = Achievement;
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_profile_achievement, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement acm = mAchievements.get(position);
        holder.mNameTextView.setText(acm.getName());

        String lastEarned = DateUtils.formatDateTime(mContext, acm.getLastEarned(), DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_YEAR);
        holder.mLastEarnedTextView.setText(lastEarned);

        String earnedGems = mContext.getResources().getQuantityString(R.plurals.gem, acm.getEarnedGems(), acm.getEarnedGems());
        holder.mEarnedGemsTextView.setText(earnedGems);
    }

    @Override
    public int getItemCount() {
        return mAchievements.size();
    }

    class AchievementViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTextView;
        private TextView mLastEarnedTextView;
        private TextView mEarnedGemsTextView;

        AchievementViewHolder(View itemView) {
            super(itemView);
            mNameTextView = itemView.findViewById(R.id.tv_name);
            mLastEarnedTextView = itemView.findViewById(R.id.tv_last_earned);
            mEarnedGemsTextView = itemView.findViewById(R.id.tv_earned_gems);
        }
    }
}
