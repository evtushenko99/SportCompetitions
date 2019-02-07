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

public class OneAthlet extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;
    private static final Gson GSON;

    static{
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting().serializeNulls();
        GSON = builder.create();
    }
    private TextView mAthletFirstName;
    private TextView mAthletLastName;
    private TextView mAthletAge;
    private TextView mAthletTeam;

    private FloatingActionButton mAthletEdit;

    private String mAthlet_id;

    private Athlet mAthlet;

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
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                outputStream.writeUTF(Commands.DELETE_ATHLET.toString() + "'" + mAthlet.getAthlets_id());
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
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                outputStream.writeUTF(Commands.GET_ONE_ATHLET.toString() + "'" + mAthlet_id);
                mAthlet = GSON.fromJson(inputStream.readUTF(), Athlet.class);

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
            mAthletFirstName.setText(mAthlet.getAthlet_first_name());
            mAthletLastName.setText(mAthlet.getAthlet_last_name());
            mAthletAge.setText(mAthlet.getAthlet_age());
            mAthletTeam.setText(mAthlet.getHistory_of_team());
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

                mNewAthletFirstName.setText(mAthlet.getAthlet_first_name());
                mNewAthletLastName.setText((mAthlet.getAthlet_last_name()));
                mNewAthletAge.setText((mAthlet.getAthlet_age()));

                Button mEditCompetition = mView.findViewById(R.id.button_dialog_edit_athlet);

                mEditCompetition.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mNewAthletFirstName.getText().toString().isEmpty()) {
                            mEditString = mNewAthletFirstName.getText().toString() + "'" +
                                    mNewAthletLastName.getText().toString() + "'" +
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
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                String[] parts = mEditString.split("'");
                Athlet athletForUpdate = new Athlet(parts[0], parts[1], parts[2]);
                outputStream.writeUTF(Commands.UPDATE_ATHLET.toString() + "'" + mAthlet.getAthlet_last_name() + "'" + GSON.toJson(athletForUpdate));
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
                Toast.makeText(OneAthlet.this, mReceiveFromServer, Toast.LENGTH_SHORT).show();
                new SendOneAthletAsyncTask().execute();

            } else
                Toast.makeText(OneAthlet.this, "Ошибка в edit", Toast.LENGTH_SHORT).show();

        }
    }



    private void initViews() {
        mAthlet = null;

        mAthletFirstName = findViewById(R.id.textView_One_athlet_first);
        mAthletLastName = findViewById(R.id.textView_One_athlet_last);
        mAthletAge = findViewById(R.id.textView_One_athlet_age);
        mAthletTeam = findViewById(R.id.textView_One_athlet_team);

        mAthletEdit = findViewById(R.id.button_dialog_edit_athlet);
    }
}

