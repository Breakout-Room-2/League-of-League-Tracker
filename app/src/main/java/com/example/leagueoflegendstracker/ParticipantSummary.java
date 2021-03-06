package com.example.leagueoflegendstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leagueoflegendstracker.models.Participant;
import com.example.leagueoflegendstracker.models.Stats;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class ParticipantSummary extends RecyclerView.Adapter<ParticipantSummary.ViewHolder> {
    Context context;
    List<Participant> team;
    public ParticipantSummary(Context context, List<Participant> team) {
        this.context = context;
        this.team = team;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_match_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Participant match = team.get(position);
        holder.bind(match);
    }

    @Override
    public int getItemCount() {
        return team.size();
    }

    public void addAll(List<Participant> matchList) {
        team.addAll(matchList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        DecimalFormat thousands = new DecimalFormat("#.00 K");
        DecimalFormat tens      = new DecimalFormat("##.0 K");
        DecimalFormat hundreds  = new DecimalFormat("### K");
        DecimalFormat millions  = new DecimalFormat("#.00 M");

        ImageView ivChampionBox, ivSpell1, ivSpell2, ivRune, ivItem1, ivItem2, ivItem3, ivItem4, ivItem5, ivItem6, ivItem7;
        TextView tvSummoner, tvLevel, tvKDA, tvCS, tvWards, tvControl, tvGold;
        RelativeLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSummoner      = itemView.findViewById(R.id.tvSummoner);
            tvLevel         = itemView.findViewById(R.id.tvLevel);
            tvKDA           = itemView.findViewById(R.id.tvKDA);
            tvCS            = itemView.findViewById(R.id.tvCS);
            tvWards         = itemView.findViewById(R.id.tvWards);
            tvControl       = itemView.findViewById(R.id.tvControl);
            tvGold          = itemView.findViewById(R.id.tvGold);

            ivChampionBox   = itemView.findViewById(R.id.ivChampionBox);
            ivSpell1        = itemView.findViewById(R.id.ivSpell1);
            ivSpell2        = itemView.findViewById(R.id.ivSpell2);
            ivRune          = itemView.findViewById(R.id.ivRune);
            ivItem1         = itemView.findViewById(R.id.ivItem1);
            ivItem2         = itemView.findViewById(R.id.ivItem2);
            ivItem3         = itemView.findViewById(R.id.ivItem3);
            ivItem4         = itemView.findViewById(R.id.ivItem4);
            ivItem5         = itemView.findViewById(R.id.ivItem5);
            ivItem6         = itemView.findViewById(R.id.ivItem6);
            ivItem7         = itemView.findViewById(R.id.ivItem7);

            container       = itemView.findViewById(R.id.rvContainer);
        }

        public void bind(Participant summoner) {
            Stats userStats = summoner.getStats();
            int[] userItems = userStats.getItems();

            tvSummoner.setText(summoner.getName());
            tvLevel.setText(String.format(Locale.US, "lvl %d", userStats.getLevel()));
            tvKDA.setText(String.format(Locale.US, "%d/%d/%d", userStats.getKills(), userStats.getDeaths(), userStats.getAssists()));
            tvCS.setText(String.format(Locale.US, "%d CS", userStats.getCS()));
            tvWards.setText(String.format(Locale.US, "%d/%d", userStats.getWardsPlaced(), userStats.getVisionWards()));
            tvControl.setText(String.format(Locale.US, "%d", userStats.getVisionWards()));
            tvGold.setText(formatCoins(userStats.getGoldEarned()));

            IdConverter.loadChampIcon(context, ivChampionBox, summoner.getChampion());
            IdConverter.loadSpellIcon(context, ivSpell1, summoner.getSpell1());
            IdConverter.loadSpellIcon(context, ivSpell2, summoner.getSpell2());
            IdConverter.loadRuneIcon(context, ivRune, userStats.getRunePrimary());
            IdConverter.loadItemIcon(context, ivItem1, userItems[0]);
            IdConverter.loadItemIcon(context, ivItem2, userItems[1]);
            IdConverter.loadItemIcon(context, ivItem3, userItems[2]);
            IdConverter.loadItemIcon(context, ivItem4, userItems[3]);
            IdConverter.loadItemIcon(context, ivItem5, userItems[4]);
            IdConverter.loadItemIcon(context, ivItem6, userItems[5]);
            IdConverter.loadItemIcon(context, ivItem7, userItems[6]);

            if(summoner.isWin()) {
                container.setBackground(ContextCompat.getDrawable(context, R.drawable.lighter_blue_background));
                container.setMinimumHeight(120);
                container.setPadding(15,15,15,15);
            }
        }

        public String formatCoins(int coins){
            if (coins < 1000)
                return String.format(Locale.US,"%d", coins);
            if (coins < 10000)
                return thousands.format(coins/1000.0);
            if (coins < 100000)
                return tens.format(coins/(1000.0));
            if (coins < 1000000)
                return hundreds.format(coins/(1000.0));
            return millions.format(coins/(1000*1000.0));
        }
    }
}
