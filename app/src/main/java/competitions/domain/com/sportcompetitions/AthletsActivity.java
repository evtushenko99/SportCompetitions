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

public class AthletsActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;
    private static final Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting().serializeNulls();
        GSON = builder.create();
    }

    private AthletAdapter mAdapter;

    private List<Athlet> mAthletList;

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

        mAdapter = new AthletAdapter(mAthletList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AthletAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(AthletsActivity.this, OneAthlet.class);
                intent.putExtra("Athlet_ID", mAthletList.get(position).getAthlets_id());
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }

        });
    }

    private void initViews() {
        mAthletList = new ArrayList<>();
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
                            mEditString = mNewAthletFirstName.getText().toString() + "'" +
                                    mNewAthletLastName.getText().toString() + "'" +
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
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                outputStream.writeUTF(Commands.GET_ATHLETES.toString());
                mAthletList = Arrays.asList(GSON.fromJson(inputStream.readUTF(), Athlet[].class));

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
            mAdapter.setAthletes(mAthletList);
        }
    }

    private class SendNewAthletAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... aVoid) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                String[] parts = mEditString.split("'");
                Athlet athletToAdd = new Athlet(parts[0], parts[1], parts[2]);
                outputStream.writeUTF(Commands.ADD_ATHLET.toString() + "'" + GSON.toJson(athletToAdd));
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
                Toast.makeText(AthletsActivity.this, mReceiveFromServer, Toast.LENGTH_SHORT).show();
                new SendAthletAsyncTask().execute();

            } else {
                Toast.makeText(AthletsActivity.this, "Ошибка в edit", Toast.LENGTH_SHORT).show();
            }
        }

    }


}




