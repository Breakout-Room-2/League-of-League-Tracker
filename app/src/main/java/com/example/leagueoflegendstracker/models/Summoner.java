package com.example.leagueoflegendstracker.models;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class Summoner {
    String name;
    String summonerID;
    String accountID;
    int iconID;
    int level;

    public Summoner(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString("name");
        summonerID = jsonObject.getString("id");
        accountID = jsonObject.getString("accountId");
        iconID = jsonObject.getInt("profileIconId");
        level = jsonObject.getInt("summonerLevel");
    }

    public String getSummonerName() {
        return name;
    }

    public String getEncryptedSummonerId() {
        return summonerID;
    }

    public String getEncryptedAccountId() {
        return accountID;
    }

    public int getProfileIconId() {
        return iconID;
    }

    public int getSummonerLevel() {
        return level;
    }
}
