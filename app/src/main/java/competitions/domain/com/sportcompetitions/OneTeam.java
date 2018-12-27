package competitions.domain.com.sportcompetitions;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import competitions.domain.com.sportcompetitions.model.Athletes;
import competitions.domain.com.sportcompetitions.model.Competition;
import competitions.domain.com.sportcompetitions.model.Message;
import competitions.domain.com.sportcompetitions.model.Seats;
import competitions.domain.com.sportcompetitions.model.Team;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class OneTeam extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;
    private TextView mTeamName;
    private TextView mTeamCoach;
    private TextView mTeamCaptain;



    private FloatingActionButton mTeamEdit;

    private String mTeam_id;


    private List<Competition> mCompetitionList;
    private List<Seats> mSeatsList;
    private List<Team> mTeamsList;
    private List<Athletes> mAthletesList;


    private PrintWriter out;
    private BufferedReader in;

    private AlertDialog dialog;
    private String mEditString;
    private String mReceiveFromServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_team);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        mTeam_id = bundle.get("Team_ID").toString();
        initViews();
        initListeners();
        new SendOneTeamAsyncTask().execute();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(OneTeam.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_del_team, null);

                Button mDelete = mView.findViewById(R.id.button_dialog_delete_team);
                mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SendDeleteTeamAsyncTask().execute();
                    }
                });
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void initListeners() {
        mTeamEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(OneTeam.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_team, null);
                final TextInputEditText mNewTeamName = mView.findViewById(R.id.textInputLayoutTeamName);
                final TextInputEditText mNewTeamCoach = mView.findViewById(R.id.textInputLayoutTeamCoach);


                mNewTeamName.setText(mTeamsList.get(0).getName_of_team());
                mNewTeamCoach.setText((mTeamsList.get(0).getCoach()));


                Button mEditCompetition = mView.findViewById(R.id.button_dialog_edit_team);

                mEditCompetition.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mNewTeamName.getText().toString().isEmpty()) {
                            mEditString = mNewTeamName.getText().toString() + ":" +
                                    mNewTeamCoach.getText().toString();
                            new SendEditTeamAsyncTask().execute();

                        }
                    }
                });
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
            }
        });
    }
    private class SendDeleteTeamAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... aVoid) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                sendMessage(Message.DELETE_TEAM);

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
                Toast.makeText(OneTeam.this, mReceiveFromServer, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OneTeam.this, TeamsActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);

            } else
                Toast.makeText(OneTeam.this, "Ошибка в delete", Toast.LENGTH_SHORT).show();

        }
    }
    private class SendOneTeamAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                sendMessage(Message.GET_ATHLETES);
                sendMessage(Message.GET_ONE_TEAM);


            } catch (IOException e) {
                e.printStackTrace();
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mTeamName.setText(mTeamsList.get(0).getName_of_team());
            mTeamCoach.setText(mTeamsList.get(0).getCoach());


            for (int i = 0; i < mAthletesList.size(); i++) {
                if (mTeamsList.get(0).getCaptain().getAthlets_id() == mAthletesList.get(i).getAthlets_id()) {
                    mTeamCaptain.setText(mAthletesList.get(i - 1).getAthlet_last_name());
                }
            }

        }
    }
    private class SendEditTeamAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... aVoid) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                sendMessage(Message.UPDATE_TEAM);


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
                Toast.makeText(OneTeam.this, mReceiveFromServer, Toast.LENGTH_SHORT).show();
                new SendOneTeamAsyncTask().execute();

            } else
                Toast.makeText(OneTeam.this, "Ошибка в edit", Toast.LENGTH_SHORT).show();

        }
    }
    private void sendMessage(Message message) {

        switch (message) {
            case GET_ONE_TEAM:
                out.println(message + mTeam_id);
                getTeams();
                break;
            case GET_ATHLETES:
                out.println(message);
                getAthlets();
                break;

            case DELETE_TEAM:
                out.println(message + mTeamsList.get(0).getName_of_team());
                getUpdateCompetition();
                break;
            case UPDATE_TEAM:
                out.println(message + mTeamsList.get(0).getName_of_team() + ":" + mEditString);
                getUpdateCompetition();
                break;


        }

    }

    private void getUpdateCompetition() {
        try {
            mReceiveFromServer = in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
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
                if (!mAthletesList.isEmpty()){
                    newTeams.setCAP(mAthletesList.get(Integer.parseInt(clientData.get("captain"))));
                }
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
    private void initViews() {
        mCompetitionList = new ArrayList<>();
        mSeatsList = new ArrayList<>();
        mTeamsList = new ArrayList<>();
        mAthletesList = new ArrayList<>();

        mTeamName= findViewById(R.id.textView_One_team_name);
        mTeamCoach= findViewById(R.id.textView_One_team_coach);
        mTeamCaptain= findViewById(R.id.textView_One_name_captain);


        mTeamEdit = findViewById(R.id.button_dialog_edit_team);
    }
}
