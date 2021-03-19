package com.example.leagueoflegendstracker.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class MatchSummary {
    long gameID, timeStamp, gameDuration;
    boolean win;
    int champion, queueID, userIndex;
    int[] teams;
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
        setTeams(jsonObject.getJSONArray("teams"));

        for(int i=0; i<10; i++){
            participants[i] = new Participant(participantsList.getJSONObject(i));
            participants[i].setSummonerDetails(participantIDs.getJSONObject(i).getJSONObject("player"));
            if (participants[i].name.equals(userName)) {
                userIndex = i;
                win = (participants[i].team == teams[0]);
            }
        }
    }

    public Participant getUserStats(){
        return participants[userIndex];
    }

    private void setTeams(JSONArray teams) throws JSONException{
        JSONObject blue = teams.getJSONObject(0);
        JSONObject red  = teams.getJSONObject(1);
        if (blue.getString("win").equals("Win"))
            this.teams = new int[]{blue.getInt("teamId"), red.getInt("teamId")};
        this.teams = new int[]{red.getInt("teamId"), blue.getInt("teamId")};
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

    private Participant[] getTeam(int teamID){
        Participant[] team = new Participant[5];
        int i = 0;
        for (Participant participant : participants)
            if (participant.team == teamID)
                team[i++] = participant;
        return team;
    }

    public Participant[] getWinningTeam() {
        return getTeam(teams[0]);
    }

    public Participant[] getLosingTeam() {
        return getTeam(teams[1]);
    }
}
