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

public class CompetitionsActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;

    private CompetitionAdapter mAdapter;

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
        setContentView(R.layout.activity_competitions);
        initViews();
        initListeners();
        new SendCompetitionsAsyncTask().execute();
        buildRecyclerView();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    public void buildRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        mAdapter = new CompetitionAdapter(mCompetitions);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CompetitionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent = new Intent(CompetitionsActivity.this, OneCompetition.class);
                intent.putExtra("Competition_ID", mCompetitions.get(position).getCompetitions_id());
                intent.putExtra("Size", mCompetitions.size());
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
        mFloatingActionButton = findViewById(R.id.button_add_pot);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(CompetitionsActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_competition, null);
                final TextInputEditText mNewTournamentName = mView.findViewById(R.id.textInputLayoutTournamentName);
                final TextInputEditText mNewLocation = mView.findViewById(R.id.textInputLayoutLocation);
                final TextInputEditText mNewKindOfSport = mView.findViewById(R.id.textInputLayoutKindOfSport);
                final TextInputEditText mNewDate = mView.findViewById(R.id.textInputLayoutDate);
                final TextInputEditText mNewResult = mView.findViewById(R.id.textInputLayoutResult);

                Button mEditCompetition = mView.findViewById(R.id.button_dialog_edit_competition);

                mEditCompetition.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mNewTournamentName.getText().toString().isEmpty()) {
                            mEditString = mNewTournamentName.getText().toString() + ":" +
                                    mNewLocation.getText().toString() + ":" +
                                    mNewKindOfSport.getText().toString() + ":" +
                                    mNewDate.getText().toString() + ":" +
                                    mNewResult.getText().toString();
                            new SendNewCompetitionAsyncTask().execute();

                        }
                    }
                });
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
            }
        });
    }

    private class SendCompetitionsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                sendMessage(Message.GET_SEATS);
                sendMessage(Message.GET_ATHLETES);
                sendMessage(Message.GET_TEAMS);
                sendMessage(Message.GET_COMPETITION);


            } catch (IOException e) {
                e.printStackTrace();
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mAdapter.setCompetitions(mCompetitions);
            mAdapter.sortByDate();
        }
    }

    private class SendNewCompetitionAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... aVoid) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                sendMessage(Message.SET_COMPETITION);


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
                Toast.makeText(CompetitionsActivity.this, mReceiveFromServer, Toast.LENGTH_SHORT).show();
                new SendCompetitionsAsyncTask().execute();

            } else
                Toast.makeText(CompetitionsActivity.this, "Ошибка в edit", Toast.LENGTH_SHORT).show();

        }
    }

    private void sendMessage(Message message) {

        switch (message) {
            case GET_SEATS:
                out.println(message);
                getSeats();
                break;
            case GET_ATHLETES:
                out.println(message);
                getAthlets();
                break;
            case GET_TEAMS:
                out.println(message);
                getTeams();
                break;
            case GET_COMPETITION:
                out.println(message);
                getCompetitions();
                break;
            case SET_COMPETITION:
                out.println(message + mEditString);
                getNewCompetition();
                break;

        }

    }

    public void getSeats() {
        try {
            String json = in.readLine();
            // Create parser and parsing recieved JSON
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(json);
            HashMap<String, String> clientData = new HashMap<>();
            mSeatsList.clear();
            for (int i = 0; i < element.getAsJsonArray().size(); i++) {
                clientData.put("seats_id", element.getAsJsonArray().get(i).getAsJsonObject().get("seats_id").getAsString());
                clientData.put("booked_seats", element.getAsJsonArray().get(i).getAsJsonObject().get("booked_seats").getAsString());
                clientData.put("free_seats", element.getAsJsonArray().get(i).getAsJsonObject().get("free_seats").getAsString());
                clientData.put("price_for_seat", element.getAsJsonArray().get(i).getAsJsonObject().get("price_for_seat").getAsString());

                Seats newSeats = new Seats(Integer.parseInt(clientData.get("seats_id")), Integer.parseInt(clientData.get("booked_seats")),
                        Integer.parseInt(clientData.get("free_seats")), Integer.parseInt(clientData.get("price_for_seat")));
                mSeatsList.add(newSeats);
            }
        } catch (IOException e) {

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

    public void getCompetitions() {
        try {
            String json = in.readLine();
            // Create parser and parsing recieved JSON
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(json);
            HashMap<String, String> clientData = new HashMap<>();
            mCompetitions.clear();
            for (int i = 0; i < element.getAsJsonArray().size(); i++) {
                clientData.put("competitions_id", element.getAsJsonArray().get(i).getAsJsonObject().get("competitions_id").getAsString());
                clientData.put("tournament_name", element.getAsJsonArray().get(i).getAsJsonObject().get("tournament_name").getAsString());
                clientData.put("locations", element.getAsJsonArray().get(i).getAsJsonObject().get("locations").getAsString());
                clientData.put("kind_of_sport", element.getAsJsonArray().get(i).getAsJsonObject().get("kind_of_sport").getAsString());
                clientData.put("time_of_comp", element.getAsJsonArray().get(i).getAsJsonObject().get("time_of_comp").getAsString());
                clientData.put("participating_teams", element.getAsJsonArray().get(i).getAsJsonObject().get("participating_teams").getAsString());
                clientData.put("participating_athletes", element.getAsJsonArray().get(i).getAsJsonObject().get("participating_athletes").getAsString());
                clientData.put("seats", element.getAsJsonArray().get(i).getAsJsonObject().get("seats").getAsString());
                clientData.put("results", element.getAsJsonArray().get(i).getAsJsonObject().get("results").getAsString());
                Competition newCompetitions = new Competition(Integer.parseInt(clientData.get("competitions_id")), clientData.get("tournament_name"),
                        clientData.get("locations"), clientData.get("kind_of_sport"), clientData.get("time_of_comp"), clientData.get("results"));
                // && seats.size() == competitions.size() && teams.size() == competitions.size() && athlets.size() == competitions.size
                if (!mSeatsList.isEmpty() && !mAthletesList.isEmpty() && !mTeamsList.isEmpty()) {
                    newCompetitions.setTAS(mTeamsList.get(Integer.parseInt(clientData.get("participating_teams")) - 1), mAthletesList.get(Integer.parseInt(clientData.get("participating_athletes")) - 1),
                            mSeatsList.get(Integer.parseInt(clientData.get("seats")) - 1));
                }
                mCompetitions.add(newCompetitions);
            }
        } catch (IOException e) {
            // System.out.println("Error in getAthlets");
        }
        // System.out.println(competitions.get(0).toString());

    }

    private void getNewCompetition() {
        try {
            mReceiveFromServer = in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}