package competitions.domain.com.sportcompetitions;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import competitions.domain.com.sportcompetitions.Connection.Commands;
import competitions.domain.com.sportcompetitions.model.Athlet;
import competitions.domain.com.sportcompetitions.model.Competition;


public class CompetitionsActivity extends AppCompatActivity {
    private static final Gson GSON;
    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting().serializeNulls();
        GSON = builder.create();
    }

    public static final int ADD_NOTE_REQUEST = 1;

    private CompetitionAdapter mAdapter;
    private List<Competition> mCompetitions;


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
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }

        });
    }

    private void initViews() {
        mCompetitions = new ArrayList<>();
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
                            mEditString = mNewTournamentName.getText().toString() + "'" +
                                    mNewLocation.getText().toString() + "'" +
                                    mNewKindOfSport.getText().toString() + "'" +
                                    mNewDate.getText().toString() + "'" +
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
                DataOutputStream  outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                outputStream.writeUTF(Commands.GET_COMPETITIONS.toString());
                mCompetitions = Arrays.asList(GSON.fromJson(inputStream.readUTF(), Competition[].class));

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
            mAdapter.setCompetitions(mCompetitions);
            mAdapter.sortByDate();
        }
    }

    private class SendNewCompetitionAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... aVoid) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());


                String[] parts = mEditString.split("'");
                Competition competitionToAdd = new Competition(-1, parts[0], parts[1], parts[2], parts[3], parts[4]);
                outputStream.writeUTF(Commands.ADD_COMPETITION.toString() + "'" + GSON.toJson(competitionToAdd));
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
                Toast.makeText(CompetitionsActivity.this, mReceiveFromServer, Toast.LENGTH_SHORT).show();
                new SendCompetitionsAsyncTask().execute();
            } else
                Toast.makeText(CompetitionsActivity.this, "Ошибка в edit", Toast.LENGTH_SHORT).show();

        }
    }
}