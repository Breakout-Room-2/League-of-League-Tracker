package com.example.leagueoflegendstracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.example.leagueoflegendstracker.models.MatchSummary;

import org.parceler.Parcels;

public class MatchDetailsActivity extends Activity {

    public static final String TAG  = "MatchDetailsActivity";

    MatchSummary matchSummary;
    RecyclerView rvWinningTeam, rvLosingTeam;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);
        matchSummary = (MatchSummary) Parcels.unwrap(getIntent().getParcelableExtra("match"));

        rvWinningTeam   = findViewById(R.id.rvWinningTeam);
        rvLosingTeam    = findViewById(R.id.rvLosingTeam);

        // Participant[] to pass into adapter constructor once it's set up
        // matchSummary.getWinningTeam();
        // matchSummary.getLosingTeam();
    }
}
