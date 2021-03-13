package com.example.leagueoflegendstracker.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class League {
    String queue, tier, rank;
    int lp, wins, losses;
    boolean hotStreak;
    MiniSeries miniSeries;

    public League(JSONObject jsonObject) throws JSONException {
        queue   = jsonObject.getString("queueType");
        tier    = jsonObject.getString("tier");
        rank    = jsonObject.getString("rank");
        lp      = jsonObject.getInt("leaguePoints");
        wins    = jsonObject.getInt("wins");
        losses  = jsonObject.getInt("losses");
        hotStreak   = jsonObject.getBoolean("hotStreak");
        if (lp == 100)
            miniSeries  = new MiniSeries(jsonObject.getJSONObject("miniSeries"));
    }

    public String getRanking(){
        return tier + " " + rank;
    }

    public int getValue(){
        int value = 0;
        switch (tier){
            case "Diamond":
                value += 1000;
            case "Platinum":
                value += 1000;
            case "Gold":
                value += 1000;
            case "Silver":
                value += 1000;
            case "Bronze":
                value += 1000;
        }
        switch (rank){
            case "IV":
                value += 200;
            case "III":
                value += 200;
            case "II":
                value += 200;
        }
        return value + lp;
    }

    public static ArrayList<League> fromJsonArray(JSONArray jsonArray) throws JSONException {
        ArrayList<League> leagues = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++)
            leagues.add(new League((JSONObject) jsonArray.get(i)));
        return leagues;
    }

    public static int getHigherLeague(ArrayList<League> leagues){
        if(leagues.size() == 0)
            return -1;
        else if(leagues.size() == 1 || leagues.get(0).getValue() > leagues.get(1).getValue())
            return 0;
        return 1;
    }

    public class MiniSeries {
        int wins, losses, target;
        String progress;

        public MiniSeries(JSONObject jsonObject) throws JSONException {
            wins    = jsonObject.getInt("wins");
            losses  = jsonObject.getInt("losses");
            target  = jsonObject.getInt("target");
            progress    = jsonObject.getString("progress");
        }
    }
}
