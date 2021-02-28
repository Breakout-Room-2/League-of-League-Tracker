package com.example.leagueoflegendstracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.leagueoflegendstracker.models.League;
import com.example.leagueoflegendstracker.models.Mastery;
import com.example.leagueoflegendstracker.models.MatchSummary;
import com.example.leagueoflegendstracker.models.Summoner;

import org.json.JSONException;

import java.util.ArrayList;

import okhttp3.Headers;

public class SummonerDetailsActivity extends AppCompatActivity {

    public static final String TAG = "SummonerDetailsActivity";
    public static final String API_KEY = BuildConfig.API_KEY;

    public static final String BASE_URL = "https://na1.api.riotgames.com%s";
    public static final String SUMMONER_ENDPOINT = "/lol/summoner/v4/summoners/by-name/%s";
    public static final String MATCH_LIST_ENDPOINT = "/lol/match/v4/matchlists/by-account/%s";
    public static final String MASTERIES_ENDPOINT = "/lol/champion-mastery/v4/champion-masteries/by-summoner/%s";
    public static final String LEAGUE_ENDPOINT = "/lol/league/v4/entries/by-summoner/%s";

    Summoner summoner;
    ArrayList<Mastery> top_masteries;
    ArrayList<League> leagues;
    ArrayList<MatchSummary> matchList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summoner_details);

        Intent i = getIntent();
        String summonerName = i.getStringExtra("summonerName");

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api_key", API_KEY);

        String url = String.format(BASE_URL, String.format(SUMMONER_ENDPOINT, summonerName));

        Log.i(TAG, "Making call to: " + url);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "Success, with data: "+json);
                try {
                    summoner = new Summoner(json.jsonObject);
                    Log.i(TAG, "Successfully created summoner model w/ ref: "+summoner.toString());
                    getStats(client, params);
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JsonException: "+e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "Failure, with resp: "+response);
            }
        });
    }

    private void getStats(AsyncHttpClient client, RequestParams params) {
        Log.i(TAG, "Getting champion masteries for: " + summoner.getSummonerName());
        makeApiCall(MASTERIES_ENDPOINT, summoner.getEncryptedSummonerId(), client, params);

        Log.i(TAG, "Getting summoner league ranking for: " + summoner.getSummonerName());
        makeApiCall(LEAGUE_ENDPOINT, summoner.getEncryptedSummonerId(), client, params);

        Log.i(TAG, "Getting summoner matchlist for: " + summoner.getSummonerName());
        makeApiCall(MATCH_LIST_ENDPOINT, summoner.getEncryptedAccountId(), client, params);
    }

    private void makeApiCall(String endpoint, String data, AsyncHttpClient client, RequestParams params){
        String url = String.format(BASE_URL, String.format(endpoint, data));
        Log.i(TAG, "Making call to " + url);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Success, with data: " + json);
                try {
                    createModel(endpoint, json);
                } catch (JSONException e) {
                    Log.i(TAG, "Hit JsonException " + e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "Failure, with data: " + response);
            }
        });
    }

    private void createModel(String endpoint, JsonHttpResponseHandler.JSON json) throws JSONException {
        switch (endpoint){
            case MASTERIES_ENDPOINT:
                top_masteries = Mastery.fromJSONArray(json.jsonArray);
                Log.i(TAG, "Created list of top masteries model w/ refs: " + top_masteries);
                break;
            case LEAGUE_ENDPOINT:
                leagues = League.fromJsonArray(json.jsonArray);
                Log.i(TAG, "Created list of league (ranking) models w/ ref: " + leagues);
                break;
            case MATCH_LIST_ENDPOINT:
                matchList = MatchSummary.fromJsonArray(json.jsonObject.getJSONArray("matches"));
                Log.i(TAG, "Created list of match summary models w/ ref: " + matchList);
                break;
        }

    }
}
