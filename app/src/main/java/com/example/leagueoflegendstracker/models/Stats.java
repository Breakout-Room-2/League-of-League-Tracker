package com.example.leagueoflegendstracker.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.Locale;

@Parcel
public class Stats{
    private static final String TAG = "Stats Model";
    int[] items = new int[7];
    int[] runes = new int[6];
    int[] runeShards = new int[3];
    int runePrimary, runeSecondary;
    int level, kills, deaths, assists, CS, visionScore,
            visionWards, wardsPlaced, wardsKilled, goldEarned;
    long totalDamageDealt;

    // Parceler library requires an empty constructor
    public Stats(){}

    public Stats(JSONObject jsonObject){
        try {
            for (int i = 0; i < 7; i++)
                items[i] = jsonObject.getInt(String.format(Locale.US, "item%d", i));
            for (int i = 0; i < 6; i++)
                runes[i] = jsonObject.getInt(String.format(Locale.US, "perk%d", i));
            for (int i = 0; i < 3; i++)
                runeShards[i] = jsonObject.getInt(String.format(Locale.US, "statPerk%d", i));
            runePrimary = jsonObject.getInt("perkPrimaryStyle");
            runeSecondary = jsonObject.getInt("perkSubStyle");
            level = jsonObject.getInt("champLevel");
            kills = jsonObject.getInt("kills");
            deaths = jsonObject.getInt("deaths");
            assists = jsonObject.getInt("assists");
            CS = jsonObject.getInt("totalMinionsKilled");
            visionScore = jsonObject.getInt("visionScore");
            goldEarned = jsonObject.getInt("goldEarned");
            totalDamageDealt = jsonObject.getLong("totalDamageDealt");
            visionWards = jsonObject.getInt("visionWardsBoughtInGame");
            // wards info set last in case of ARAM games so other values won't skip being set
            wardsKilled = jsonObject.getInt("wardsKilled");
            wardsPlaced = jsonObject.getInt("wardsPlaced");
        } catch(Exception e) {
            Log.e(TAG, "Hit JsonException: " + e);
        }
    }

    public int[] getItems() {
        return items;
    }

    public int[] getRunes() {
        return runes;
    }

    public int[] getRuneShards() {
        return runeShards;
    }

    public int getRunePrimary() {
        return runePrimary;
    }

    public int getRuneSecondary() {
        return runeSecondary;
    }

    public int getLevel() {
        return level;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getAssists() {
        return assists;
    }

    public int getCS() {
        return CS;
    }

    public int getVisionScore() {
        return visionScore;
    }

    public int getVisionWards() {
        return visionWards;
    }

    public int getWardsPlaced() {
        return wardsPlaced;
    }

    public int getWardsKilled() {
        return wardsKilled;
    }

    public int getGoldEarned() {
        return goldEarned;
    }

    public long getTotalDamageDealt() {
        return totalDamageDealt;
    }
}
