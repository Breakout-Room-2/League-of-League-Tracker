package com.example.leagueoflegendstracker.models;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class Summoner {
    String summonerName;
    String encryptedSummonerId;
    String accountId;
    int profileIconId;
    int summonerLevel;

    public Summoner(JSONObject jsonObject) throws JSONException {
        summonerName = jsonObject.getString("name");
        encryptedSummonerId = jsonObject.getString("id");
        accountId = jsonObject.getString("accountId");
        profileIconId = jsonObject.getInt("profileIconId");
        summonerLevel = jsonObject.getInt("summonerLevel");
    }

    public String getEncryptedSummonerId() {
        return encryptedSummonerId;
    }

    public String getAccountId() {
        return accountId;
    }

    public int getProfileIconId() {
        return profileIconId;
    }

    public int getSummonerLevel() {
        return summonerLevel;
    }

    public String getSummonerName() {
        return summonerName;
    }
}
