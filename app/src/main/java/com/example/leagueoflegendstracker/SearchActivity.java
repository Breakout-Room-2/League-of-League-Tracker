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
import com.example.leagueoflegendstracker.models.Summoner;

import org.json.JSONException;

import okhttp3.Headers;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "SearchActivity";
    public static final String API_KEY = BuildConfig.API_KEY;

    public static final String BASE_URL = "https://na1.api.riotgames.com%s";
    public static final String SUMMONER_ENDPOINT = "/lol/summoner/v4/summoners/by-name/%s";

    EditText etSummonerSearch;
    ImageView ivSearchButton;
    Summoner summoner;

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

        String url = String.format(BASE_URL, String.format(SUMMONER_ENDPOINT, summonerName));
        Log.i(TAG, "Making call to: " + url);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "Success, with data: "+json);
                try {
                    summoner = new Summoner(json.jsonObject);
                    Log.i(TAG, "Successfully created summoner model: "+summoner.toString());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit jsonException: "+e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "Failure, with resp: "+response);
            }
        });
    }
}