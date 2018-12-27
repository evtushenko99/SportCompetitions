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

public class OneAthlet extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;
    private TextView mAthletFirstName;
    private TextView mAthletLastName;
    private TextView mAthletAge;
    private TextView mAthletTeam;


    private FloatingActionButton mAthletEdit;

    private String mAthlet_id;


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
        setContentView(R.layout.activity_one_athlet);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        mAthlet_id = bundle.get("Athlet_ID").toString();
        initViews();
        initListeners();
        new SendOneAthletAsyncTask().execute();
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
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(OneAthlet.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_del_athlet, null);

                Button mDelete = mView.findViewById(R.id.button_dialog_delete_athlet);
                mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SendDeleteAthletAsyncTask().execute();
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

    private class SendDeleteAthletAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... aVoid) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                sendMessage(Message.DELETE_ATHLET);

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
                Toast.makeText(OneAthlet.this, mReceiveFromServer, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OneAthlet.this, AthletsActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);

            } else
                Toast.makeText(OneAthlet.this, "Ошибка в delete", Toast.LENGTH_SHORT).show();

        }
    }

    private class SendOneAthletAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                sendMessage(Message.GET_TEAMS);
                sendMessage(Message.GET_ONE_ATHLET);


            } catch (IOException e) {
                e.printStackTrace();
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAthletFirstName.setText(mAthletesList.get(0).getAthlet_first_name());
            mAthletLastName.setText(mAthletesList.get(0).getAthlet_last_name());
            mAthletAge.setText(mAthletesList.get(0).getAthlet_age());
            for (int i = 0; i < mTeamsList.size(); i++) {
                if (mAthletesList.get(0).getHistory_of_teams().getTeams_id() == mTeamsList.get(i).getTeams_id()) {
                    mAthletTeam.setText(mTeamsList.get(i - 1).getName_of_team());
                }
            }

        }
    }

    private void sendMessage(Message message) {

        switch (message) {
            case GET_ONE_ATHLET:
                out.println(message + mAthlet_id);
                getAthlets();
                break;
            case GET_TEAMS:
                out.println(message);
                getTeams();
                break;
            case DELETE_ATHLET:
                out.println(message + mAthletesList.get(0).getAthlet_last_name());
                getUpdateCompetition();
                break;
            case UPDATE_ATHLET:
                out.println(message + mAthlet_id + ":" + mEditString);
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

                if (!mTeamsList.isEmpty()){
                    newAthlet.setHistoryOfTeam(mTeamsList.get(Integer.parseInt(clientData.get("history_of_teams"))));
                }
                mAthletesList.add(newAthlet);

            }
        } catch (IOException e) {
        }

    }

    private void initListeners() {
        mAthletEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(OneAthlet.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_athlet, null);
                final TextInputEditText mNewAthletFirstName = mView.findViewById(R.id.textInputLayoutAthletFirstName);
                final TextInputEditText mNewAthletLastName = mView.findViewById(R.id.textInputLayoutAthletLastName);
                final TextInputEditText mNewAthletAge = mView.findViewById(R.id.textInputLayoutAthletAge);

                mNewAthletFirstName.setText(mAthletesList.get(0).getAthlet_first_name());
                mNewAthletLastName.setText((mAthletesList.get(0).getAthlet_last_name()));
                mNewAthletAge.setText((mAthletesList.get(0).getAthlet_age()));

                Button mEditCompetition = mView.findViewById(R.id.button_dialog_edit_athlet);

                mEditCompetition.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mNewAthletFirstName.getText().toString().isEmpty()) {
                            mEditString = mNewAthletFirstName.getText().toString() + ":" +
                                    mNewAthletLastName.getText().toString() + ":" +
                                    mNewAthletAge.getText().toString();
                            new SendEditAthletAsyncTask().execute();

                        }
                    }
                });
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
            }
        });
    }

    private class SendEditAthletAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... aVoid) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                sendMessage(Message.UPDATE_ATHLET);


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
                Toast.makeText(OneAthlet.this, mReceiveFromServer, Toast.LENGTH_SHORT).show();
                new SendOneAthletAsyncTask().execute();

            } else
                Toast.makeText(OneAthlet.this, "Ошибка в edit", Toast.LENGTH_SHORT).show();

        }
    }



    private void initViews() {
        mCompetitionList = new ArrayList<>();
        mSeatsList = new ArrayList<>();
        mTeamsList = new ArrayList<>();
        mAthletesList = new ArrayList<>();

        mAthletFirstName = findViewById(R.id.textView_One_athlet_first);
        mAthletLastName = findViewById(R.id.textView_One_athlet_last);
        mAthletAge = findViewById(R.id.textView_One_athlet_age);
        mAthletTeam = findViewById(R.id.textView_One_athlet_team);

        mAthletEdit = findViewById(R.id.button_dialog_edit_athlet);
    }
}

