package com.example.leagueoflegendstracker.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Mastery {
    int championID;
    int masteryLevel;
    int masteryPoints;

    public Mastery(JSONObject jsonObject) throws JSONException {
        championID      = jsonObject.getInt("championId");
        masteryLevel    = jsonObject.getInt("championLevel");
        masteryPoints   = jsonObject.getInt("championPoints");
    }

    public static ArrayList<Mastery> fromJSONArray(JSONArray jsonArray) throws JSONException {
        // Lucky for us the array returned is already sorted to have highest masteries on top
        ArrayList<Mastery> topMasteries = new ArrayList<>();
        for (int i=0; i<3; i++) {
            topMasteries.add(new Mastery(jsonArray.getJSONObject(i)));
        }
        return topMasteries;
    }
    public int getChampionID() {
        return championID;
    }

    public int getMasteryLevel() {
        return masteryLevel;
    }

    public int getMasteryPoints() {
        return masteryPoints;
    }
}
