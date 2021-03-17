package com.example.leagueoflegendstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leagueoflegendstracker.models.MatchSummary;

import java.util.List;
import java.util.Locale;

public class MatchDetailsAdapter extends RecyclerView.Adapter<MatchDetailsAdapter.ViewHolder> {

    Context context;
    List<MatchSummary>  matches;

    public MatchDetailsAdapter(Context context, List<MatchSummary> matches) {
        this.context = context;
        this.matches = matches;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.item_match, parent, false);
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

        private final String KDA = "%d/%d/%d";

        ImageView ivChampionBox, ivitem1, ivitem2, ivitem3, ivitem4, ivitem5, ivitem6;
        TextView tvChampName, tvLevel, tvKDA, tvCS, tvDuration, tvMode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChampionBox   = itemView.findViewById(R.id.ivChampionBox);
            ivitem1         = itemView.findViewById(R.id.ivItem1);
            ivitem2         = itemView.findViewById(R.id.ivItem2);
            ivitem3         = itemView.findViewById(R.id.ivItem3);
            ivitem4         = itemView.findViewById(R.id.ivItem4);
            ivitem5         = itemView.findViewById(R.id.ivItem5);
            ivitem6         = itemView.findViewById(R.id.ivItem6);
            tvChampName     = itemView.findViewById(R.id.tvChampName);
            tvLevel         = itemView.findViewById(R.id.tvLevel);
            tvKDA           = itemView.findViewById(R.id.tvKDA);
            tvCS            = itemView.findViewById(R.id.tvCS);
            tvDuration      = itemView.findViewById(R.id.tvDuration);
            tvMode          = itemView.findViewById(R.id.tvMode);
        }

        public void bind(MatchSummary match) {
            MatchSummary.Participant user = match.getUserParticipant();
            IdConverter.loadChampName(context, tvChampName, match.getChampion());
            IdConverter.loadChampIcon(context, ivChampionBox, match.getChampion());
            MatchSummary.Participant.Stats userStats = user.getStats();
            int[] userItems = userStats.getItems();
            int kills    = userStats.getKills();
            int deaths   = userStats.getDeaths();
            int assists  = userStats.getAssists();
            tvKDA.setText(String.format(Locale.US, KDA, kills, deaths, assists));
        }
    }
}