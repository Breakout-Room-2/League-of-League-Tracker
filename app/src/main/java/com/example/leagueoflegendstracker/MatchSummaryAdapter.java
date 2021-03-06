package com.example.leagueoflegendstracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leagueoflegendstracker.models.MatchSummary;
import com.example.leagueoflegendstracker.models.Participant;
import com.example.leagueoflegendstracker.models.Stats;

import org.parceler.Parcels;

import java.util.List;
import java.util.Locale;

public class MatchSummaryAdapter extends RecyclerView.Adapter<MatchSummaryAdapter.ViewHolder> {

    Context context;
    List<MatchSummary>  matches;

    public MatchSummaryAdapter(Context context, List<MatchSummary> matches) {
        this.context = context;
        this.matches = matches;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.item_match_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchSummary match = matches.get(position);
        holder.bind(match);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public void clear() {
        matches.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<MatchSummary> matchList) {
        matches.addAll(matchList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivChampionBox, ivSpell1, ivSpell2, ivItem1, ivItem2, ivItem3, ivItem4, ivItem5, ivItem6, ivItem7;
        TextView tvChampName, tvLevel, tvKDA, tvCS, tvTimeStamp, tvDuration, tvMode, tvMatchResults;
        RelativeLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChampionBox   = itemView.findViewById(R.id.ivChampionBox);
            ivSpell1        = itemView.findViewById(R.id.ivSpell1);
            ivSpell2        = itemView.findViewById(R.id.ivSpell2);
            ivItem1         = itemView.findViewById(R.id.ivItem1);
            ivItem2         = itemView.findViewById(R.id.ivItem2);
            ivItem3         = itemView.findViewById(R.id.ivItem3);
            ivItem4         = itemView.findViewById(R.id.ivItem4);
            ivItem5         = itemView.findViewById(R.id.ivItem5);
            ivItem6         = itemView.findViewById(R.id.ivItem6);
            ivItem7         = itemView.findViewById(R.id.ivItem7);
            tvChampName     = itemView.findViewById(R.id.tvChampName);
            tvLevel         = itemView.findViewById(R.id.tvLevel);
            tvKDA           = itemView.findViewById(R.id.tvKDA);
            tvCS            = itemView.findViewById(R.id.tvCS);
            tvTimeStamp     = itemView.findViewById(R.id.tvTimestamp);
            tvDuration      = itemView.findViewById(R.id.tvDuration);
            tvMode          = itemView.findViewById(R.id.tvMode);
            tvMatchResults  = itemView.findViewById(R.id.tvMatchResult);
            container       = itemView.findViewById(R.id.container);
        }

        public void bind(MatchSummary match) {
            Participant user = match.getUserParticipant();
            Stats userStats = user.getStats();
            int[] userItems = userStats.getItems();

            if (TimeFormatter.getTimeMinutes(match.getGameDuration()) > 5) {
                if (match.isWin()) {
                    tvMatchResults.setText(R.string.victory);
                    tvMatchResults.setTextColor(ContextCompat.getColor(context, R.color.victory_blue));
                    container.setBackground(ContextCompat.getDrawable(context, R.drawable.lighter_blue_background));
                } else {
                    tvMatchResults.setText(R.string.defeat);
                    tvMatchResults.setTextColor(ContextCompat.getColor(context, R.color.defeat_red));
                    container.setBackground(ContextCompat.getDrawable(context, R.drawable.light_red_background));
                }
            }

            tvKDA.setText(String.format(Locale.US, "%d/%d/%d", userStats.getKills(), userStats.getDeaths(), userStats.getAssists()));
            tvLevel.setText(String.format(Locale.US, "lvl %d", userStats.getLevel()));
            tvCS.setText(String.format(Locale.US, "%d CS", userStats.getCS()));
            tvTimeStamp.setText(TimeFormatter.getTimeDifference(match.getTimeStamp()));
            tvDuration.setText(TimeFormatter.getTimeDuration(match.getGameDuration()));
            IdConverter.loadChampName(context, tvChampName, match.getChampion());
            IdConverter.loadChampIcon(context, ivChampionBox, match.getChampion());
            IdConverter.loadSpellIcon(context, ivSpell1, user.getSpell1());
            IdConverter.loadSpellIcon(context, ivSpell2, user.getSpell2());
            IdConverter.loadItemIcon(context, ivItem1, userItems[0]);
            IdConverter.loadItemIcon(context, ivItem2, userItems[1]);
            IdConverter.loadItemIcon(context, ivItem3, userItems[2]);
            IdConverter.loadItemIcon(context, ivItem4, userItems[3]);
            IdConverter.loadItemIcon(context, ivItem5, userItems[4]);
            IdConverter.loadItemIcon(context, ivItem6, userItems[5]);
            IdConverter.loadItemIcon(context, ivItem7, userItems[6]);
            IdConverter.loadQueueType(context, tvMode, match.getQueueID());

            container.setOnClickListener(v -> {
                Intent i = new Intent(context, MatchDetailsActivity.class);
                i.putExtra("match", Parcels.wrap(match));
                context.startActivity(i);
            });
        }
    }
}