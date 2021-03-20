package com.example.leagueoflegendstracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leagueoflegendstracker.models.MatchSummary;

import org.parceler.Parcels;

import java.util.Arrays;

public class MatchDetailsActivity extends Activity {

    public static final String TAG  = "MatchDetailsActivity";

    MatchSummary matchSummary;

    ImageView ivBackArrow;
    RecyclerView rvWinningTeam, rvLosingTeam;
    GameDetailsAdapter winTeamAdapter, loseTeamAdapter;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);
        matchSummary = (MatchSummary) Parcels.unwrap(getIntent().getParcelableExtra("match"));

        ivBackArrow     = findViewById(R.id.ivBackArrow);
        rvWinningTeam   = findViewById(R.id.rvWinningTeam);
        rvLosingTeam    = findViewById(R.id.rvLosingTeam);

        winTeamAdapter  = new GameDetailsAdapter(this, Arrays.asList(matchSummary.getWinningTeam()));
        loseTeamAdapter = new GameDetailsAdapter(this, Arrays.asList(matchSummary.getLosingTeam()));

        rvWinningTeam.setLayoutManager(new LinearLayoutManager(this));
        rvLosingTeam.setLayoutManager(new LinearLayoutManager(this));

        rvWinningTeam.setAdapter(winTeamAdapter);
        rvLosingTeam.setAdapter(loseTeamAdapter);

        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
