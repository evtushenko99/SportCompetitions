package competitions.domain.com.sportcompetitions;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import competitions.domain.com.sportcompetitions.model.Athletes;
import competitions.domain.com.sportcompetitions.model.Competition;
import competitions.domain.com.sportcompetitions.model.Message;
import competitions.domain.com.sportcompetitions.model.Seats;
import competitions.domain.com.sportcompetitions.model.Team;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamsActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;

    private TeamAdapter mAdapter;

    private List<Competition> mCompetitions;
    private List<Seats> mSeatsList;
    private List<Team> mTeamsList;
    private List<Athletes> mAthletesList;

    private PrintWriter out;
    private BufferedReader in;

    private String mReceiveFromServer;
    private FloatingActionButton mFloatingActionButton;


    private int num = 5;
    private AlertDialog dialog;
    private String mEditString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        initListeners();
        new SendTeamsAsyncTask().execute();
        buildRecyclerView();


    }

    public void buildRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTeams);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new TeamAdapter(mTeamsList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new TeamAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent = new Intent(TeamsActivity.this, OneTeam.class);
                intent.putExtra("Team_ID", mTeamsList.get(position).getTeams_id());
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }

        });
    }

    private void initViews() {
        mCompetitions = new ArrayList<>();
        mSeatsList = new ArrayList<>();
        mTeamsList = new ArrayList<>();
        mAthletesList = new ArrayList<>();
    }


    private void initListeners() {
        mFloatingActionButton = findViewById(R.id.button_add_team);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(TeamsActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_team, null);
                final TextInputEditText mNewTeamName = mView.findViewById(R.id.textInputLayoutTeamName);
                final TextInputEditText mNewCoach = mView.findViewById(R.id.textInputLayoutTeamCoach);


                Button mEditCompetition = mView.findViewById(R.id.button_dialog_edit_team);

                mEditCompetition.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mNewTeamName.getText().toString().isEmpty()) {
                            mEditString = mNewTeamName.getText().toString() + ":" +
                                    mNewCoach.getText().toString();

                            new SendNewTeamAsyncTask().execute();

                        }
                    }
                });
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
            }
        });
    }

    private void sendMessage(Message message) {
        num = num+1;
        switch (message) {

            case GET_ATHLETES:
                out.println(message);
                getAthlets();
                break;
            case GET_TEAMS:
                out.println(message);
                getTeams();
                break;
            case SET_TEAM:

                out.println(message + String.valueOf(num) + ":" + mEditString);
                getNewTeam();
                break;

        }

    }
    private class SendTeamsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);


                sendMessage(Message.GET_ATHLETES);
                sendMessage(Message.GET_TEAMS);



            } catch (IOException e) {
                e.printStackTrace();
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mAdapter.setTeams(mTeamsList);
        }
    }

    private class SendNewTeamAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... aVoid) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                sendMessage(Message.SET_TEAM);


            } catch (IOException e) {
                e.printStackTrace();
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.cancel();
            if (mReceiveFromServer.equals("[Done]")) {
                Toast.makeText(TeamsActivity.this, mReceiveFromServer, Toast.LENGTH_SHORT).show();
                new SendTeamsAsyncTask().execute();

            } else
                Toast.makeText(TeamsActivity.this, "Ошибка в edit", Toast.LENGTH_SHORT).show();

        }
    }

    public void getTeams() {
        try {
            String json = in.readLine();
            // Create parser and parsing recieved JSON
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(json);
            HashMap<String, String> clientData = new HashMap<>();
            mTeamsList.clear();
            for (int i = 0; i < element.getAsJsonArray().size(); i++) {
                clientData.put("teams_id", element.getAsJsonArray().get(i).getAsJsonObject().get("teams_id").getAsString());
                clientData.put("name_of_team", element.getAsJsonArray().get(i).getAsJsonObject().get("name_of_team").getAsString());
                clientData.put("coach", element.getAsJsonArray().get(i).getAsJsonObject().get("coach").getAsString());
                clientData.put("captain", element.getAsJsonArray().get(i).getAsJsonObject().get("captain").getAsString());

                Team newTeams = new Team(Integer.parseInt(clientData.get("teams_id")), clientData.get("name_of_team"),
                        clientData.get("coach"));
                mTeamsList.add(newTeams);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getAthlets() {
        try {
            String json = in.readLine();
            // Create parser and parsing recieved JSON
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(json);
            HashMap<String, String> clientData = new HashMap<>();
            mAthletesList.clear();
            for (int i = 0; i < element.getAsJsonArray().size(); i++) {
                clientData.put("athletes_id", element.getAsJsonArray().get(i).getAsJsonObject().get("athletes_id").getAsString());
                clientData.put("athlet_first_name", element.getAsJsonArray().get(i).getAsJsonObject().get("athlet_first_name").getAsString());
                clientData.put("athlet_last_name", element.getAsJsonArray().get(i).getAsJsonObject().get("athlet_last_name").getAsString());
                clientData.put("athlet_age", element.getAsJsonArray().get(i).getAsJsonObject().get("athlet_age").getAsString());
                clientData.put("history_of_teams", element.getAsJsonArray().get(i).getAsJsonObject().get("history_of_teams").getAsString());

                Athletes newAthlet = new Athletes(Integer.parseInt(clientData.get("athletes_id")), clientData.get("athlet_first_name"),
                        clientData.get("athlet_last_name"), clientData.get("athlet_age"));
                mAthletesList.add(newAthlet);

            }
        } catch (IOException e) {
        }

    }


    private void getNewTeam() {
        try {
            mReceiveFromServer = in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
