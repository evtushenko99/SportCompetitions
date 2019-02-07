package competitions.domain.com.sportcompetitions;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import competitions.domain.com.sportcompetitions.Connection.Commands;
import competitions.domain.com.sportcompetitions.model.Athlet;
import competitions.domain.com.sportcompetitions.model.Competition;
import competitions.domain.com.sportcompetitions.model.Message;
import competitions.domain.com.sportcompetitions.model.Seat;
import competitions.domain.com.sportcompetitions.model.Team;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TeamsActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;
    private static final Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting().serializeNulls();
        GSON = builder.create();
    }

    private TeamAdapter mAdapter;

    private List<Team> mTeamsList;

    private String mReceiveFromServer;
    private FloatingActionButton mFloatingActionButton;


    private int num ;
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
        mTeamsList = new ArrayList<>();
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
                            mEditString = mNewTeamName.getText().toString() + "'" +
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


    private class SendTeamsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                outputStream.writeUTF(Commands.GET_TEAMS.toString());
                mTeamsList = Arrays.asList(GSON.fromJson(inputStream.readUTF(), Team[].class));
                num = mTeamsList.size();

                outputStream.close();
                inputStream.close();
                socket.close();

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
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());


                String[] parts = mEditString.split("'");
                Team teamToAdd = new Team(num+1, parts[0], parts[1], "1");
                outputStream.writeUTF(Commands.ADD_TEAM.toString() + "'" + GSON.toJson(teamToAdd));
                mReceiveFromServer = inputStream.readUTF();

                outputStream.close();
                inputStream.close();
                socket.close();
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
            if (mReceiveFromServer.equals("Done")) {
                Toast.makeText(TeamsActivity.this, mReceiveFromServer, Toast.LENGTH_SHORT).show();
                new SendTeamsAsyncTask().execute();

            } else
                Toast.makeText(TeamsActivity.this, "Ошибка в edit", Toast.LENGTH_SHORT).show();

        }
    }

}
