package com.example.leagueoflegendstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SummonerSearchActivity extends AppCompatActivity {

    EditText etSummonerSearch;
    ImageView ivSearchButton;
    long EXP_DATE = Long.parseLong(BuildConfig.EXP_DATE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        long diff = EXP_DATE - System.currentTimeMillis();
        if (diff < 0)
            Toast.makeText(SummonerSearchActivity.this, String.format(Locale.US, "API Key expired %s ago!", TimeFormatter.getTimeLeft(-diff)), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(SummonerSearchActivity.this, String.format(Locale.US, "API Key lasts for %s!", TimeFormatter.getTimeLeft(diff)), Toast.LENGTH_SHORT).show();

        etSummonerSearch = findViewById(R.id.etSummonerSearch);
        ivSearchButton = findViewById(R.id.ivSearchButton);

        etSummonerSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                goSummonerDetails(etSummonerSearch.getText().toString());
                return true;
            }
            return false;
        });
        ivSearchButton.setOnClickListener(view -> goSummonerDetails(etSummonerSearch.getText().toString()));
    }

    private void goSummonerDetails(String summonerName) {
        long diff = EXP_DATE - System.currentTimeMillis();
        if (diff < 0) {
            Toast.makeText(SummonerSearchActivity.this, String.format(Locale.US, "API Key expired %s ago!", TimeFormatter.getTimeLeft(-diff)), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = new Intent(this, SummonerFeedActivity.class);
        i.putExtra("summonerName", summonerName);

        startActivity(i);
    }
}