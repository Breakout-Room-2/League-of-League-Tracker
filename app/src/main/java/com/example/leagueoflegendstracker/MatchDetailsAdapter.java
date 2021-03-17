package com.example.leagueoflegendstracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.leagueoflegendstracker.models.MatchSummary;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Headers;

import static android.content.ContentValues.TAG;

//import static com.example.leagueoflegendstracker.IdConverter.CHAMP_DATA;
//
//  Summoner summoner;
//          ArrayList<Mastery> top_masteries;
//        ArrayList<League> leagues;
//        ArrayList<MatchSummary> matchList;
//
//        ImageView ivChampOne, ivChampTwo, ivChampThree, ivSummonerIcon;
//        TextView tvSummonerName, tvSummonerLevel, tvChampOne, tvChampTwo, tvChampThree, tvRank;

public class MatchDetailsAdapter extends RecyclerView.Adapter<MatchDetailsAdapter.ViewHolder> {
    public static final String CHAMP_ICONS_ENDPOINT    = "https://cdn.communitydragon.org/latest/champion/%s/square";
    Context context;
    List<MatchSummary>  matches;
    HashMap<Integer, String> champData;
    public MatchDetailsAdapter(Context context, List<MatchSummary> matches) {
        champData = new HashMap<Integer, String>();
        this.context = context;
        this.matches = matches;
    }

//    private void getChampData(){
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.get(CHAMP_DATA, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Headers headers, JSON json) {
//                Log.i(TAG, "Successful datadragon call to: " + CHAMP_DATA);
//                // Log.i(TAG, "Success, with data: " + json);
//                createChampData(json.jsonObject);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                Log.i(TAG, "Failure, with data: " + response);
//            }
//        });
//    }
//
//    private void createChampData(JSONObject json) {
//        try {
//            champData = new HashMap<>();
//            JSONObject data = json.getJSONObject("data");
//
//            for (Iterator<String> keys = data.keys(); keys.hasNext(); ) {
//                JSONObject champ = data.getJSONObject(keys.next());
//                int champID = Integer.parseInt(champ.getString("key"));
//                String champName = champ.getString("name");
//                champData.put(champID, champName);
//            }
//        } catch (JSONException e) {
//            Log.i(TAG, "Ran into jsonException: " + e);
//        }
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.item_match_defeat, parent, false);
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
        ImageView ivChampionBox;
        ImageView ivitem1, ivitem2, ivitem3, ivitem4, ivitem5, ivitem6;
        TextView tvChampName, tvLevel, tvKDA, tvCS, tvDuration, tvMode;
//        TextView
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChampionBox = itemView.findViewById(R.id.ivChampionBox);
            ivitem1 = itemView.findViewById(R.id.ivItem1);
            ivitem2 = itemView.findViewById(R.id.ivItem2);
            ivitem3 = itemView.findViewById(R.id.ivItem3);
            ivitem4 = itemView.findViewById(R.id.ivItem4);
            ivitem5 = itemView.findViewById(R.id.ivItem5);
            ivitem6 = itemView.findViewById(R.id.ivItem6);
            tvChampName = itemView.findViewById(R.id.tvChampName); //SummonerName is Champion name
            tvLevel = itemView.findViewById(R.id.tvLevel);
            tvKDA = itemView.findViewById(R.id.tvKDA);
            tvCS = itemView.findViewById(R.id.tvCS);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvMode = itemView.findViewById(R.id.tvMode);
        }
        public void bind(MatchSummary match) {
            MatchSummary.Participant currentSumm = match.getUserParticipant();
            tvChampName.setText(champData.get(match.getChampion()));
            String champIcon = champData.get(match.getChampion());
//            Glide.with(this).load(champIcon).into(ivChampionBox);
            Glide.with(itemView).load(champIcon).into(ivChampionBox);
            MatchSummary.Participant.Stats userStats = currentSumm.getStats();
            int[] currItems = userStats.getItems();
            String kills = String.valueOf(userStats.getKills());
            String deaths = String.valueOf(userStats.getDeaths());
            String assists = String.valueOf(userStats.getAssists());
            tvKDA.setText(kills + "/" + deaths + "/" + assists);
            tvCS.setText(userStats.getCS());
            tvDuration.setText(toString().valueOf(match.getGameDuration()));




        }
    }
}