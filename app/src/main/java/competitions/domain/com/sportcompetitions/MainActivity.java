package competitions.domain.com.sportcompetitions;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mButtonAllCompetitions;
    private Button mButtonAllAthlets;
    private Button mButtonAllTeams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonAllCompetitions = findViewById(R.id.allCompetitions);
        mButtonAllCompetitions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CompetitionsActivity.class);
                startActivity(intent);
            }
        });
        mButtonAllAthlets = findViewById(R.id.allAthletes);
        mButtonAllAthlets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AthletsActivity.class);
                startActivity(intent);
            }
        });
        mButtonAllTeams = findViewById(R.id.allTeams);
        mButtonAllTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TeamsActivity.class);
                startActivity(intent);
            }
        });
    }
}
