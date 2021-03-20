package com.example.leagueoflegendstracker.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Participant{
    String name;
    int ID, champion, team, spell1, spell2, icon;
    Stats stats;

    boolean win;

    // Parceler library requires an empty constructor
    public Participant(){}

    public Participant(JSONObject jsonObject) throws JSONException {
        ID          = jsonObject.getInt("participantId");
        champion    = jsonObject.getInt("championId");
        team        = jsonObject.getInt("teamId");
        spell1      = jsonObject.getInt("spell1Id");
        spell2      = jsonObject.getInt("spell2Id");
        stats = new Stats(jsonObject.getJSONObject("stats"));
    }

    public void setSummonerDetails(JSONObject player) throws JSONException{
        name = player.getString("summonerName");
        icon = player.getInt("profileIcon");
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public int getChampion() {
        return champion;
    }

    public int getTeam() {
        return team;
    }

    public int getSpell1() {
        return spell1;
    }

    public int getSpell2() {
        return spell2;
    }

    public int getIcon() {
        return icon;
    }

    public boolean isWin() {
        return win;
    }


    public Stats getStats() {
        return stats;
    }

}