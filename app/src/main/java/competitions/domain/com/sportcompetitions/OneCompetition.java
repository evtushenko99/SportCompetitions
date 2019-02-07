package competitions.domain.com.sportcompetitions;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import competitions.domain.com.sportcompetitions.Connection.Commands;
import competitions.domain.com.sportcompetitions.model.Athlet;
import competitions.domain.com.sportcompetitions.model.Competition;
import competitions.domain.com.sportcompetitions.model.Message;
import competitions.domain.com.sportcompetitions.model.Seat;
import competitions.domain.com.sportcompetitions.model.Team;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class OneCompetition extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;
    private static final Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting().serializeNulls();
        GSON = builder.create();
    }


    private TextView mCompetitionName;
    private TextView mCompetitionLocation;
    private TextView mCompetitionKindOFSport;
    private TextView mCompetitionDate;
    private TextView mCompetitionResult;

    private TextView mPriceForSeat;
    private TextView mFreeSeats;
    private TextView mBookedSeats;

    private Button mButtonCompetitionTickets;
    private FloatingActionButton mCompetitionEdit;

    private String mCompetition_id;

    private Competition mCompetition;
    private List<Seat> mSeatList;


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
        initViews();
        new SendOneCompetitionAsyncTask().execute();
        initListeners();

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
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                outputStream.writeUTF(Commands.DELETE_COMPETITION.toString() + "'" + mCompetition.getCompetitions_id());
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
                Toast.makeText(OneCompetition.this, "Deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OneCompetition.this, CompetitionsActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            } else Toast.makeText(OneCompetition.this, "Not Deleted", Toast.LENGTH_SHORT).show();


        }
    }

    private class SendOneCompetitionAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                outputStream.writeUTF(Commands.GET_ONE_COMPETITION.toString() + "'" + Integer.valueOf(mCompetition_id));
                mCompetition = GSON.fromJson(inputStream.readUTF(), Competition.class);

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
            mCompetitionName.setText(mCompetition.getTournament_name());
            mCompetitionLocation.setText(mCompetition.getLocation());
            mCompetitionKindOFSport.setText(mCompetition.getKind_of_sport());
            mCompetitionDate.setText(mCompetition.getTime_of_comp());
            mCompetitionResult.setText(mCompetition.getResults());
            new SendGetSeatsAsyncTask().execute();

        }
    }

    private class SendGetSeatsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                outputStream.writeUTF(Commands.GET_SEATS.toString());
                mSeatList = Arrays.asList(GSON.fromJson(inputStream.readUTF(), Seat[].class));

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
            for (int i = 0; i < mSeatList.size(); i++) {
                if (mSeatList.get(i).getPrice_for_seat() == Integer.valueOf(mCompetition.getSeat())) {
                    mBookedSeats.setText(String.valueOf(mSeatList.get(i).getBooked_seats()));
                    mPriceForSeat.setText(String.valueOf(mSeatList.get(i).getPrice_for_seat()));
                    mFreeSeats.setText(String.valueOf(mSeatList.get(i).getFree_seats()));
                }
            }

        }
    }


    private void initListeners() {
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
                mNewTournamentName.setText(mCompetition.getTournament_name());
                mNewLocation.setText(mCompetition.getLocation());
                mNewKindOfSport.setText(mCompetition.getKind_of_sport());
                mNewDate.setText(mCompetition.getTime_of_comp());
                mNewResult.setText(mCompetition.getResults());

                Button mEditCompetition = mView.findViewById(R.id.button_dialog_edit_competition);

                mEditCompetition.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mNewTournamentName.getText().toString().isEmpty()) {
                            mEditString = mNewTournamentName.getText().toString() + "'" +
                                    mNewLocation.getText().toString() + "'" +
                                    mNewKindOfSport.getText().toString() + "'" +
                                    mNewDate.getText().toString() + "'" +
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
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                String[] parts = mEditString.split("'");
                Competition competitionForUpdate = new Competition(-1, parts[0], parts[1], parts[2], parts[3], parts[4]);
                outputStream.writeUTF(Commands.UPDATE_COMPETITION.toString() + "'" + mCompetition_id + "'" + GSON.toJson(competitionForUpdate));
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
            new SendOneCompetitionAsyncTask().execute();
            if (mReceiveFromServer.equals("Done")) {
                Toast.makeText(OneCompetition.this, "Update", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(OneCompetition.this, "Mistake in update method", Toast.LENGTH_SHORT).show();

        }
    }

    private void initViews() {
                mSeatList = new ArrayList<>();
                mCompetition = null;

                mCompetitionName = findViewById(R.id.textView_One_tournamentName);
                mCompetitionLocation = findViewById(R.id.textView_One_location);
                mCompetitionKindOFSport = findViewById(R.id.textView_One_kinOfSport);
                mCompetitionDate = findViewById(R.id.textView_One_date);
                mCompetitionResult = findViewById(R.id.textView_One_result);
                mPriceForSeat = findViewById(R.id.textView_dialog_Price_For_Ticket);
                mFreeSeats = findViewById(R.id.textView_dialog_Free_Tickets);
                mBookedSeats = findViewById(R.id.textView_dialog_Booked_Tickets);


                mCompetitionEdit = findViewById(R.id.button_dialog_edit_competition);
            }

}
