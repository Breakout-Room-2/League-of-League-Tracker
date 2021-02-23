package com.example.leagueoflegendstracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.leagueoflegendstracker.models.Summoner;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String API_KEY = BuildConfig.API_KEY;

    public static final String BASE_URL = "https://na1.api.riotgames.com%s";
    public static final String SUMMONER_ENDPOINT = "/lol/summoner/v4/summoners/by-name/%s";
    public static final String MATCH_LIST_ENDPOINT = "/lol/match/v4/matchlists/by-account/%s";
    public static final String MATCH_DETAIL_ENDPOINT = "/lol/match/v4/matches/%s";
    public static final String MASTERIES_ENDPOINT = "/lol/champion-mastery/v4/champion-masteries/by-summoner/%s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}