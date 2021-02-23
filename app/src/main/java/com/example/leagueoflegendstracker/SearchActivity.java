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

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "SearchActivity";
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
                    performSearch();
                    return true;
                }
                return false;
            }
        });
        ivSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSearch();
            }
        });
    }

    private void performSearch() {
        Log.i(TAG, "User is searching for: "+etSummonerSearch.getText());
    }
}