package com.example.leagueoflegendstracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class MatchDetailsActivity extends Activity {

    public static final String TAG  = "MatchDetailsActivity";

    ImageView ivChampionBox, ivSpell1, ivSpell2, ivRune, ivItem1, ivItem2, ivItem3, ivItem4, ivItem5, ivItem6, ivItem7;
    TextView tvLevel, tvSummoner, tvSummonerRank, tvKDA, tvCS, tvP, tvK, tvC, tvGold;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        ivChampionBox   = findViewById(R.id.ivChampionBox);
        ivSpell1        = findViewById(R.id.ivSpell1);
        ivSpell2        = findViewById(R.id.ivSpell2);
        ivRune          = findViewById(R.id.ivRune);
        tvLevel         = findViewById(R.id.tvLevel);
        tvSummoner      = findViewById(R.id.tvSummonerName);
        tvSummonerRank  = findViewById(R.id.tvSummonerRank);
        tvKDA           = findViewById(R.id.tvKDA);
        tvCS            = findViewById(R.id.tvCS);
        tvP             = findViewById(R.id.tvP);
        tvK             = findViewById(R.id.tvK);
        tvC             = findViewById(R.id.tvC);
        tvGold          = findViewById(R.id.tvGold);
        ivItem1         = findViewById(R.id.ivItem1);
        ivItem2         = findViewById(R.id.ivItem2);
        ivItem3         = findViewById(R.id.ivItem3);
        ivItem4         = findViewById(R.id.ivItem4);
        ivItem5         = findViewById(R.id.ivItem5);
        ivItem6         = findViewById(R.id.ivItem6);
        ivItem7         = findViewById(R.id.ivItem7);
    }
}
