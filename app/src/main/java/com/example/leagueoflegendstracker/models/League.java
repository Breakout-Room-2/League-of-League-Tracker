package com.example.leagueoflegendstracker.models;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

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

    public static ArrayList<League> fromJsonArray(JSONArray jsonArray) throws JSONException {
        ArrayList<League> leagues = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++)
            leagues.add(new League((JSONObject) jsonArray.get(i)));
        return leagues;
    }

    public class MiniSeries {
        int wins, losses, target;
        String progress;

        public MiniSeries(JSONObject jsonObject) throws  JSONException {
            wins    = jsonObject.getInt("wins");
            losses  = jsonObject.getInt("losses");
            target  = jsonObject.getInt("target");
            progress    = jsonObject.getString("progress");
        }
    }
}
