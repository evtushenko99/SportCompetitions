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

public class AthletsActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;

    private AthletAdapter mAdapter;

    private List<Competition> mCompetitions;
    private List<Seats> mSeatsList;
    private List<Team> mTeamsList;
    private List<Athletes> mAthletesList;

    private PrintWriter out;
    private BufferedReader in;

    private String mReceiveFromServer;
    private FloatingActionButton mFloatingActionButton;

    private AlertDialog dialog;
    private String mEditString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlets);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        initListeners();
        new SendAthletAsyncTask().execute();
        buildRecyclerView();
    }

    public void buildRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewAthlet);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        mAdapter = new AthletAdapter(mAthletesList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AthletAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(AthletsActivity.this, OneAthlet.class);
                intent.putExtra("Athlet_ID", mAthletesList.get(position).getAthlets_id());
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
        mFloatingActionButton = findViewById(R.id.button_add_athlet);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(AthletsActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_athlet, null);
                final TextInputEditText mNewAthletFirstName = mView.findViewById(R.id.textInputLayoutAthletFirstName);
                final TextInputEditText mNewAthletLastName = mView.findViewById(R.id.textInputLayoutAthletLastName);
                final TextInputEditText mNewAthletAge = mView.findViewById(R.id.textInputLayoutAthletAge);

                Button mEditCompetition = mView.findViewById(R.id.button_dialog_edit_athlet);

                mEditCompetition.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mNewAthletFirstName.getText().toString().isEmpty()) {
                            mEditString = mNewAthletFirstName.getText().toString() + ":" +
                                    mNewAthletLastName.getText().toString() + ":" +
                                    mNewAthletAge.getText().toString();

                            new SendNewAthletAsyncTask().execute();

                        }
                    }
                });
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
            }
        });
    }

    private class SendAthletAsyncTask extends AsyncTask<Void, Void, Void> {

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
            mAdapter.setAthletes(mAthletesList);
        }
    }

    private class SendNewAthletAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... aVoid) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                sendMessage(Message.SET_ATHLET);


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
                Toast.makeText(AthletsActivity.this, mReceiveFromServer, Toast.LENGTH_SHORT).show();
                new SendAthletAsyncTask().execute();

            } else {
                Toast.makeText(AthletsActivity.this, "Ошибка в edit", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void sendMessage(Message message) {

        switch (message) {

            case GET_TEAMS:
                out.println(message);
                getTeams();
                break;
            case GET_ATHLETES:
                out.println(message);
                getAthlets();
                break;
            case SET_ATHLET:
                out.println(message + mEditString);
                getNewAthlet();
                break;

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


    private void getNewAthlet() {
        try {
            mReceiveFromServer = in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}



