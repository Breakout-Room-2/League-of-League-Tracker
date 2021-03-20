package com.example.leagueoflegendstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leagueoflegendstracker.models.Participant;
import com.example.leagueoflegendstracker.models.Stats;

import java.util.List;
import java.util.Locale;

public class GameDetailsAdapter extends RecyclerView.Adapter<GameDetailsAdapter.ViewHolder> {
    Context context;
    List<Participant> team;
    public GameDetailsAdapter(Context context, List<Participant> team) {
        this.context = context;
        this.team = team;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_match, parent, false);
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
        ImageView ivChampionBox, ivSpell1, ivSpell2, ivRune, ivItem1, ivItem2, ivItem3, ivItem4, ivItem5, ivItem6, ivItem7;
        TextView tvSummoner, tvLevel, tvKDA, tvCS, ivVision, tvP, tvK, tvC, tvGold;
        RelativeLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChampionBox   = itemView.findViewById(R.id.ivChampionBox);
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
            tvLevel         = itemView.findViewById(R.id.tvLevel);
            tvKDA           = itemView.findViewById(R.id.tvKDA);
            tvCS            = itemView.findViewById(R.id.tvCS);
            tvSummoner      = itemView.findViewById(R.id.tvSummoner);
        }

        public void bind(Participant summoner) {
            Stats userStats = summoner.getStats();
            tvKDA.setText(String.format(Locale.US, "%d/%d/%d", userStats.getKills(), userStats.getDeaths(), userStats.getAssists()));
            tvLevel.setText(String.format(Locale.US, "lvl %d", userStats.getLevel()));
            tvCS.setText(String.format(Locale.US, "%d CS", userStats.getCS()));
            IdConverter.loadChampIcon(context, ivChampionBox, summoner.getChampion());
            tvSummoner.setText(summoner.getName());
            int[] userItems = userStats.getItems();
            IdConverter.loadItemIcon(context, ivItem1, userItems[0]);
            IdConverter.loadItemIcon(context, ivItem2, userItems[1]);
            IdConverter.loadItemIcon(context, ivItem3, userItems[2]);
            IdConverter.loadItemIcon(context, ivItem4, userItems[3]);
            IdConverter.loadItemIcon(context, ivItem5, userItems[4]);
            IdConverter.loadItemIcon(context, ivItem6, userItems[5]);
            IdConverter.loadItemIcon(context, ivItem7, userItems[6]);
            IdConverter.loadSpellIcon(context, ivSpell1, summoner.getSpell1());
            IdConverter.loadSpellIcon(context, ivSpell2, summoner.getSpell2());

        }
    }
}
