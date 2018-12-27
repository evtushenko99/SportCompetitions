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

public class OneCompetition extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;
    private TextView mCompetitionName;
    private TextView mCompetitionLocation;
    private TextView mCompetitionKindOFSport;
    private TextView mCompetitionDate;
    private TextView mCompetitionResult;

    private Button mButtonCompetitionTickets;
    private FloatingActionButton mCompetitionEdit;

    private Competition mCompetition;
    private String mCompetition_id;
    private String mSize;

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
        setContentView(R.layout.activity_one_competition);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        mCompetition_id = bundle.get("Competition_ID").toString();
        mSize = bundle.get("Size").toString();
        initViews();
        initListeners();
        new SendOneCompetitionAsyncTask().execute();
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
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(OneCompetition.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_del_competition, null);

                Button mDelete = mView.findViewById(R.id.button_dialog_delete_competition);
                mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SendDeleteCompetitionAsyncTask().execute();
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

    private class SendDeleteCompetitionAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... aVoid) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                sendMessage(Message.DELETE_COMPETITION);

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
                Toast.makeText(OneCompetition.this, mReceiveFromServer, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OneCompetition.this, CompetitionsActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);

            } else
                Toast.makeText(OneCompetition.this, "Ошибка в delete", Toast.LENGTH_SHORT).show();

        }
    }

    private class SendOneCompetitionAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                sendMessage(Message.GET_SEATS);
                sendMessage(Message.GET_ONE_COMPETITION);



            } catch (IOException e) {
                e.printStackTrace();
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mCompetitionName.setText(mCompetitionList.get(0).getTournament_name());
            mCompetitionLocation.setText(mCompetitionList.get(0).getLocation());
            mCompetitionKindOFSport.setText(mCompetitionList.get(0).getKind_of_sport());
            mCompetitionDate.setText(mCompetitionList.get(0).getTime_of_comp());
            mCompetitionResult.setText(mCompetitionList.get(0).getResults());

        }
    }

    private void sendMessage(Message message) {

        switch (message) {
            case GET_ONE_COMPETITION:
                out.println(message + mCompetition_id);
                getOneCompetition();
                break;
            case UPDATE_COMPETITION:
                out.println(message + mCompetition_id + ":" + mEditString);
                getUpdateCompetition();
                break;
            case GET_SEATS:
                out.println(message);
                getSeats();
                break;
            case DELETE_COMPETITION:
                out.println(message + mCompetitionList.get(0).getTournament_name());
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

    public void getOneCompetition() {
        try {
            String json = in.readLine();
            // Create parser and parsing recieved JSON
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(json);
            HashMap<String, String> clientData = new HashMap<>();
            mCompetitionList.clear();
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

                if (!mSeatsList.isEmpty()) {
                    newCompetitions.setSeat(mSeatsList.get(Integer.parseInt(clientData.get("seats"))));
                }

                mCompetitionList.add(newCompetitions);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initListeners() {
        mButtonCompetitionTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mSeatsList.size(); i++) {
                    if (mSeatsList.get(i).getSeats_id() == mCompetitionList.get(0).getSeats().getSeats_id()) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(OneCompetition.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialog_tickets_competition, null);
                        final TextView mNewPriceForSeat = mView.findViewById(R.id.textView_dialog_Price_For_Ticket);
                        final TextView mNewFreeSeats = mView.findViewById(R.id.textView_dialog_Free_Tickets);
                        final TextView mNewBookedSeats = mView.findViewById(R.id.textView_dialog_Booked_Tickets);
                        Button mBuyTickets = mView.findViewById(R.id.button_dialog_buy_ticket);

                        mNewPriceForSeat.setText(mSeatsList.get(i-1).getPrice_for_seat() + " р");
                        mNewBookedSeats.setText(mSeatsList.get(i-1).getBooked_seats() + "");
                        mNewFreeSeats.setText(mSeatsList.get(i-1).getFree_seats() + "");

                        mBuyTickets.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        mBuilder.setView(mView);
                        dialog = mBuilder.create();
                        dialog.show();
                    }
                }
            }
        });
        mCompetitionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(OneCompetition.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_competition, null);
                final TextInputEditText mNewTournamentName = mView.findViewById(R.id.textInputLayoutTournamentName);
                final TextInputEditText mNewLocation = mView.findViewById(R.id.textInputLayoutLocation);
                final TextInputEditText mNewKindOfSport = mView.findViewById(R.id.textInputLayoutKindOfSport);
                final TextInputEditText mNewDate = mView.findViewById(R.id.textInputLayoutDate);
                final TextInputEditText mNewResult = mView.findViewById(R.id.textInputLayoutResult);
                mNewTournamentName.setText(mCompetitionList.get(0).getTournament_name());
                mNewLocation.setText(mCompetitionList.get(0).getLocation());
                mNewKindOfSport.setText(mCompetitionList.get(0).getKind_of_sport());
                mNewDate.setText(mCompetitionList.get(0).getTime_of_comp());
                mNewResult.setText(mCompetitionList.get(0).getResults());

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
                            new SendEditCompetitionAsyncTask().execute();

                        }
                    }
                });
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
            }
        });
    }

    private class SendEditCompetitionAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... aVoid) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                sendMessage(Message.UPDATE_COMPETITION);


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
                Toast.makeText(OneCompetition.this, mReceiveFromServer, Toast.LENGTH_SHORT).show();
                new SendOneCompetitionAsyncTask().execute();

            } else
                Toast.makeText(OneCompetition.this, "Ошибка в edit", Toast.LENGTH_SHORT).show();

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
            e.printStackTrace();
        }
    }

    private void initViews() {
        mCompetitionList = new ArrayList<>();
        mSeatsList = new ArrayList<>();
        mTeamsList = new ArrayList<>();
        mAthletesList = new ArrayList<>();

        mCompetitionName = findViewById(R.id.textView_One_tournamentName);
        mCompetitionLocation = findViewById(R.id.textView_One_location);
        mCompetitionKindOFSport = findViewById(R.id.textView_One_kinOfSport);
        mCompetitionDate = findViewById(R.id.textView_One_date);
        mCompetitionResult = findViewById(R.id.textView_One_result);

        mButtonCompetitionTickets = findViewById(R.id.button_one_tickets);
        mCompetitionEdit = findViewById(R.id.button_dialog_edit_competition);
    }
}
