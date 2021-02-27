package com.example.leagueoflegendstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.leagueoflegendstracker.models.League;
import com.example.leagueoflegendstracker.models.Mastery;
import com.example.leagueoflegendstracker.models.MatchSummary;
import com.example.leagueoflegendstracker.models.Summoner;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "SearchActivity";
    public static final String API_KEY = BuildConfig.API_KEY;

    public static final String BASE_URL = "https://na1.api.riotgames.com%s";
    public static final String SUMMONER_ENDPOINT = "/lol/summoner/v4/summoners/by-name/%s";
    public static final String MATCH_LIST_ENDPOINT = "/lol/match/v4/matchlists/by-account/%s";
    public static final String MASTERIES_ENDPOINT = "/lol/champion-mastery/v4/champion-masteries/by-summoner/%s";
    public static final String LEAGUE_ENDPOINT = "/lol/league/v4/entries/by-summoner/%s";

    EditText etSummonerSearch;
    ImageView ivSearchButton;
    Summoner summoner;
    ArrayList<Mastery> top_masteries;
    ArrayList<League> leagues;
    ArrayList<MatchSummary> matchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSummonerSearch = findViewById(R.id.etSummonerSearch);
        ivSearchButton = findViewById(R.id.ivSearchButton);

        etSummonerSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    performSearch(etSummonerSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });
        ivSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSearch(etSummonerSearch.getText().toString());
            }
        });
    }

    private void performSearch(String summonerName) {
        Log.i(TAG, "User is searching for: "+etSummonerSearch.getText());
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api_key", API_KEY);

        getSummoner(summonerName, client, params);
        // getStats(client, params);

        // testSummoner(summonerName);
    }

    private void getSummoner(String summonerName, AsyncHttpClient client, RequestParams params){
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
        getMasteries(client, params);
        getRanking(client, params);
        getMatchList(client, params);
    }

    private void getMasteries(AsyncHttpClient client, RequestParams params) {
        Log.i(TAG, "Getting champion masteries for: " + summoner.getSummonerName());
        tryApiCall(MASTERIES_ENDPOINT, summoner.getEncryptedSummonerId(), client, params);
    }

    private void getRanking(AsyncHttpClient client, RequestParams params) {
        Log.i(TAG, "Getting summoner league ranking for: " + summoner.getSummonerName());
        tryApiCall(LEAGUE_ENDPOINT, summoner.getEncryptedSummonerId(), client, params);
    }

    private void getMatchList(AsyncHttpClient client, RequestParams params) {
        Log.i(TAG, "Getting summoner matchlist for: " + summoner.getSummonerName());
        tryApiCall(MATCH_LIST_ENDPOINT, summoner.getEncryptedAccountId(), client, params);
    }

    private void tryApiCall(String endpoint, String data, AsyncHttpClient client, RequestParams params){
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

    private void testSummoner(String summonerName){
        String url = String.format(BASE_URL, String.format(SUMMONER_ENDPOINT, summonerName));

        try {
            summoner = new Summoner(testApiCall(url));
            Log.i(TAG, "Created new summoner: " + summoner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonHttpResponseHandler.JSON makeApiCall(String url, @NotNull AsyncHttpClient client, RequestParams params) {
        final JsonHttpResponseHandler.JSON[] response = new JsonHttpResponseHandler.JSON[1];
        Log.i(TAG, "Making call to " + url);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Success, with data: " + json);
                response[0] = json;
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Failure, with resp: " + response);
            }
        });

        return response[0];
    }

    private JSONObject testApiCall(String url) throws Exception{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        JSONObject jsonObject = null;

        try (Response response = client.newCall(request).execute()){
            if (!response.isSuccessful()) {
                Log.e(TAG, "Failure, with resp: " + response);
            }

            try {
                jsonObject = new JSONObject(response.body().string());
                Log.i(TAG, "Success with data: " + jsonObject);
            } catch (JSONException e) {
                Log.e(TAG, "Hit JSONException " + e);
            }
        }

        return jsonObject;
    }
}