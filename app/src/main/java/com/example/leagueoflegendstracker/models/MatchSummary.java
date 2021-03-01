package com.example.leagueoflegendstracker.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MatchSummary {
    long gameID, timeStamp;
    String role;
    int champion, queue;

    public MatchSummary(JSONObject jsonObject) throws JSONException {
        gameID      = jsonObject.getLong("gameId");
        champion    = jsonObject.getInt("champion");
        queue       = jsonObject.getInt("queue");
        timeStamp   = jsonObject.getLong("timestamp");
        role        = jsonObject.getString("role");
    }
    public static ArrayList<MatchSummary> fromJsonArray(JSONArray jsonArray) throws JSONException {
        ArrayList<MatchSummary> matches = new ArrayList<>();
        for(int i=0; i<10; i++)
            matches.add(new MatchSummary((JSONObject) jsonArray.get(i)));
        return matches;
    }
}
