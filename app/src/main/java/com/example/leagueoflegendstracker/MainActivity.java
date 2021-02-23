package com.example.leagueoflegendstracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "https://na1.api.riotgames.com%s";
    public static final String SUMMONER_ENDPOINT = "/lol/summoner/v4/summoners/by-name/%s";
    public static final String MATCH_LIST_ENDPOINT = "/lol/match/v4/matchlists/by-account/%s";
    public static final String MATCH_DETAIL_ENDPOINT = "/lol/match/v4/matches/%s";
    public static final String MASTERIES_ENDPOINT = "/lol/champion-mastery/v4/champion-masteries/by-summoner/%s";
    public static final String API_KEY = BuildConfig.API_KEY;
    public static final String SUMMONER = "Marethyu86";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api_key", API_KEY);

        getSummonerInfo(SUMMONER, client, params);
    }

    public static void makeApiRequest(String endPoint, AsyncHttpClient client, RequestParams params){
        Log.i(TAG, "Making call to " + String.format(BASE_URL, endPoint));
        client.get(String.format(BASE_URL, endPoint), params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "Success, with data: "+json);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "Failure, with message: "+response);
            }
        });
    }

    public static void getSummonerInfo(String summoner, AsyncHttpClient client, RequestParams params){
        makeApiRequest(String.format(SUMMONER_ENDPOINT, summoner), client, params);
    }
}