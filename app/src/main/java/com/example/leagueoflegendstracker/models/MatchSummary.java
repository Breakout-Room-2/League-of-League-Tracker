package com.example.leagueoflegendstracker.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Locale;

@Parcel
public class MatchSummary {
    long gameID, timeStamp, gameDuration;
    boolean win;
    int champion, queueID, userIndex;
    Participant[] participants = new Participant[10];

    // Empty constructor required by Parceler library
    public MatchSummary() {}

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
        JSONArray participantIDs    = jsonObject.getJSONArray("participantIdentities");
        JSONArray participantsList  = jsonObject.getJSONArray("participants");

        for(int i=0; i<10; i++){
            participants[i] = new Participant(participantsList.getJSONObject(i));
            participants[i].setSummonerDetails(participantIDs.getJSONObject(i).getJSONObject("player"));
            if (participants[i].name.equals(userName)) {
                userIndex = i;
                win = (participants[i].team == getWinningTeam(jsonObject.getJSONArray("teams")));
            }
        }
    }

    public Participant getUserStats(){
        return participants[userIndex];
    }

    // very ugly way of doing this - but what isn't ugly in this entire file?
    private int getWinningTeam(JSONArray teams) throws JSONException{
        JSONObject blue = teams.getJSONObject(0);
        JSONObject red  = teams.getJSONObject(1);
        if (blue.getString("win").equals("Win"))
            return blue.getInt("teamId");
        return red.getInt("teamId");
    }

    public long getGameID() {
        return gameID;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public long getGameDuration() {
        return gameDuration;
    }

    public boolean isWin() {
        return win;
    }

    public int getChampion() {
        return champion;
    }

    public int getQueueID() {
        return queueID;
    }

    public Participant getUserParticipant(){
        return participants[userIndex];
    }

    public Participant getParticipantBySummonerName(String summonerName){
        for (Participant participant : participants){
            if (participant.name.equals(summonerName))
                return participant;
        }
        return null;
    }

    public Participant[] getTeam(int teamID){
        Participant[] team = new Participant[5];
        int i = 0;
        for (Participant participant : participants)
            if (participant.team == teamID)
                team[i++] = participant;
        return team;
    }

}
