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
import java.util.HashMap;
import java.util.List;

public class OneTeam extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;
    private static final Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting().serializeNulls();
        GSON = builder.create();
    }

    private TextView mTeamName;
    private TextView mTeamCoach;
    private TextView mTeamCaptain;


    private FloatingActionButton mTeamEdit;

    private String mTeam_id;


    private Team mTeam;


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


                mNewTeamName.setText(mTeam.getName_of_team());
                mNewTeamCoach.setText((mTeam.getCoach()));


                Button mEditCompetition = mView.findViewById(R.id.button_dialog_edit_team);

                mEditCompetition.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mNewTeamName.getText().toString().isEmpty()) {
                            mEditString = mNewTeamName.getText().toString() + "'" +
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
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                outputStream.writeUTF(Commands.DELETE_TEAM.toString() + "'" + mTeam.getTeams_id());
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
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                outputStream.writeUTF(Commands.GET_ONE_TEAM.toString() + "'" + mTeam_id);
                mTeam = GSON.fromJson(inputStream.readUTF(), Team.class);

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
            mTeamName.setText(mTeam.getName_of_team());
            mTeamCoach.setText(mTeam.getCoach());
            mTeamCaptain.setText(mTeam.getCaptain());
        }
    }

    private class SendEditTeamAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... aVoid) {
            try {
                Socket socket = new Socket("194.58.96.249", 4026);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                String[] parts = mEditString.split("'");
                Team teamForUpdate = new Team(Integer.valueOf(mTeam_id), parts[0], parts[1], "1");
                outputStream.writeUTF(Commands.UPDATE_TEAM.toString() + "'" + mTeam.getName_of_team() + "'" + GSON.toJson(teamForUpdate));
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
                Toast.makeText(OneTeam.this, mReceiveFromServer, Toast.LENGTH_SHORT).show();
                new SendOneTeamAsyncTask().execute();

            } else
                Toast.makeText(OneTeam.this, "Ошибка в edit", Toast.LENGTH_SHORT).show();

        }
    }


    private void initViews() {
        mTeam = null;

        mTeamName = findViewById(R.id.textView_One_team_name);
        mTeamCoach = findViewById(R.id.textView_One_team_coach);
        mTeamCaptain = findViewById(R.id.textView_One_name_captain);


        mTeamEdit = findViewById(R.id.button_dialog_edit_team);
    }
}
