package com.example.leagueoflegendstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    EditText etSummonerSearch;
    ImageView ivSearchButton;

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
                    goSummonerDetails(etSummonerSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });
        ivSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goSummonerDetails(etSummonerSearch.getText().toString());
            }
        });
    }

    private void goSummonerDetails(String summonerName) {
        Intent i = new Intent(this, SummonerDetailsActivity.class);
        i.putExtra("summonerName", summonerName);

        startActivity(i);
        finish();
    }
}