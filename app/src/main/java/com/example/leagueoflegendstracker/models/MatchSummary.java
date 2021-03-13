package com.example.leagueoflegendstracker.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MatchSummary {
    long gameID, timeStamp, gameDuration;
    boolean win;
    int champion, queueID, userIndex;
    Participant[] participants;

    public MatchSummary(JSONObject jsonObject) throws JSONException {
        gameID      = jsonObject.getLong("gameId");
        champion    = jsonObject.getInt("champion");
        queueID     = jsonObject.getInt("queue");
        timeStamp   = jsonObject.getLong("timestamp");
    }

    public static ArrayList<MatchSummary> fromJsonArray(JSONArray jsonArray) throws JSONException {
        ArrayList<MatchSummary> matches = new ArrayList<>();
        for(int i=0; i<10; i++)
            matches.add(new MatchSummary((JSONObject) jsonArray.get(i)));
        return matches;
    }

    public void setMatchDetails(JSONObject jsonObject, String userName) throws JSONException{
        gameDuration = jsonObject.getLong("gameDuration");

        JSONArray participantsList = jsonObject.getJSONArray("participants");
        participants = new Participant[10];

        for(int i=0; i<10; i++){
            participants[i] = new Participant(participantsList.getJSONObject(i));
            if (participants[i].name.equals(userName))
                userIndex = i;
            if (participants[i].team == getWinningTeam(jsonObject.getJSONArray("teams")))
                win = true;
        }
    }

    public Participant getUserStats(){
        return participants[userIndex];
    }

    // very ugly way of doing this - and pretty much this entire file
    private int getWinningTeam(JSONArray teams) throws JSONException{
        int winningTeam = 100;
        if (teams.getJSONObject(1).getString("win").equals("Win"))
            winningTeam = 200;
        return winningTeam;
    }

    class Participant{
        String name;
        int ID, champion, team, spell1, spell2, iconID;
        Stats stats;

        public Participant(JSONObject jsonObject) throws JSONException{
            ID          = jsonObject.getInt("participantId");
            champion    = jsonObject.getInt("championId");
            team        = jsonObject.getInt("teamId");
            spell1      = jsonObject.getInt("spell1Id");
            spell2      = jsonObject.getInt("spell2Id");
            stats = new Stats(jsonObject.getJSONObject("stats"));
        }

        class Stats{
            int[] items;
            int[] runes;
            int[] runeShards;
            int runePrimary, runeSecondary;
            int level, kills, deaths, assists, CS, visionScore,
            visionWards, wardsPlaced, wardsKilled, goldEarned;
            long totalDamageDealt;

            public Stats(JSONObject jsonObject) throws JSONException{
                for(int i=0; i<7; i++)
                    items[i]        = jsonObject.getInt(String.format(Locale.US,"item%d", i));
                for(int i=0; i<6; i++)
                    runes[i]        = jsonObject.getInt(String.format(Locale.US, "perk%d", i));
                for(int i=0; i<3; i++)
                    runeShards[i]   = jsonObject.getInt(String.format(Locale.US, "statPerk%d", i));
                runePrimary     = jsonObject.getInt("perkPrimaryStyle");
                runeSecondary   = jsonObject.getInt("perkSubStyle");
                level   = jsonObject.getInt("champLevel");
                kills   = jsonObject.getInt("kills");
                deaths  = jsonObject.getInt("deaths");
                assists = jsonObject.getInt("assists");
                CS      = jsonObject.getInt("totalMinionsKilled");
                visionScore = jsonObject.getInt("visionScore");
                visionWards = jsonObject.getInt("visionWardsBoughtInGame");
                wardsPlaced = jsonObject.getInt("wardsPlaced");
                wardsKilled = jsonObject.getInt("wardsKilled");
                goldEarned  = jsonObject.getInt("goldEarned");
                totalDamageDealt    = jsonObject.getLong("totalDamageDealt");
            }
        }
    }
}
