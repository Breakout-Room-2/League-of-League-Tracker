package com.example.leagueoflegendstracker;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import okhttp3.Headers;

public class IdConverter {
    private static final String TAG = "IdConverter";
    private static final String CHAMP_DATA = "https://ddragon.leagueoflegends.com/cdn/11.6.1/data/en_US/champion.json";
    private static final String SPELL_DATA = "http://ddragon.leagueoflegends.com/cdn/11.6.1/data/en_US/summoner.json";
    private static final String SUMMONER_ICONS_ENDPOINT = "https://cdn.communitydragon.org/latest/profile-icon/%s";
    private static final String CHAMP_ICONS_ENDPOINT    = "https://cdn.communitydragon.org/latest/champion/%s/square";
    private static final String SPELL_ICONS_ENDPOINT    = "http://ddragon.leagueoflegends.com/cdn/11.6.1/img/spell/%s";
    private static final String ITEMS_ICONS_ENDPOINT    = "http://ddragon.leagueoflegends.com/cdn/11.6.1/img/item/%s.png";

    private static HashMap<Integer, String> champData = new HashMap<>();
    private static HashMap<Integer, String> spellData = new HashMap<>();

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static RequestParams params = new RequestParams();

    public static void loadSummonerIcon(Context context, ImageView view, int profileID){
        loadIcon(context, view, profileID, SUMMONER_ICONS_ENDPOINT);
    }

    public static void loadChampIcon(Context context, ImageView view, int champID){
        loadIcon(context, view, champID, CHAMP_ICONS_ENDPOINT);
    }

    public static void loadItemIcon(Context context, ImageView view, int itemID){
        loadIcon(context, view, itemID, ITEMS_ICONS_ENDPOINT);
    }

    public static void loadSpellIcon(Context context, ImageView view, int spellID){
        // All initial calls will have an empty HashMap since they're called in series and the
        // network call isn't completed until waay later. Still necessary since getSpellData will
        // call loadSpellIcon after the network call - thus just needed to avoid infinite looping
        if (spellData.isEmpty()){
            getSpellData(context, view, spellID);
        } else {
            loadIcon(context, view, spellData.get(spellID), SPELL_ICONS_ENDPOINT);
        }
    }

    private static void loadIcon(Context context, ImageView view, String key, String ENDPOINT){
        String url = String.format(ENDPOINT, key);
        Glide.with(context).load(url).into(view);
    }

    private static void loadIcon(Context context, ImageView view, int ID, String ENDPOINT){
        loadIcon(context, view, String.valueOf(ID), ENDPOINT);
    }

    public static void loadChampName(Context context, TextView view, int champID){
        // All initial calls will have an empty HashMap since they're called in series and the
        // network call isn't completed until waay later. Still necessary since getChampData will
        // call loadChampIcon after the network call - thus just needed to avoid infinite looping
        if (champData.isEmpty()){
            getChampData(context, view, champID);
        } else {
            view.setText(champData.get(champID));
        }
    }

    private static void getChampData(Context context, TextView view, int champID){
        client.get(CHAMP_DATA, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Successful datadragon call to: " + CHAMP_DATA);
                // check to make sure you don't fill in the HashMap multiple times
                if (champData.isEmpty())
                    fillChampData(json.jsonObject);
                loadChampName(context, view, champID);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "Failure, with resp: " + response);
            }
        });
    }

    private static void fillChampData(JSONObject json) {
        try {
            JSONObject data = json.getJSONObject("data");

            for (Iterator<String> keys = data.keys(); keys.hasNext(); ) {
                JSONObject champ = data.getJSONObject(keys.next());
                int champID = Integer.parseInt(champ.getString("key"));
                String champName = champ.getString("name");
                champData.put(champID, champName);
            }
        } catch (JSONException e) {
            Log.i(TAG, "Ran into jsonException: " + e);
        }
    }

    private static void getSpellData(Context context, ImageView view, int spellID){
        client.get(SPELL_DATA, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Successful datadragon call to: " + SPELL_DATA);
                // check to make sure you don't fill in the HashMap multiple times
                if (spellData.isEmpty())
                    fillSpellData(json.jsonObject);
                loadSpellIcon(context, view, spellID);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "Failure, with resp: " + response);
            }
        });
    }

    private static void fillSpellData(JSONObject jsonObject){
        try {
            JSONObject data = jsonObject.getJSONObject("data");

            for (Iterator<String> keys = data.keys(); keys.hasNext(); ) {
                JSONObject spell = data.getJSONObject(keys.next());
                int spellID = Integer.parseInt(spell.getString("key"));
                String image_url = String.format(SPELL_ICONS_ENDPOINT, spell.getJSONObject("image").getString("full"));
                spellData.put(spellID, image_url);
            }
        } catch (JSONException e) {
            Log.i(TAG, "Ran into jsonException: " + e);
        }
    }
}