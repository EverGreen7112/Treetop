package com.evergreen.treetop.activities.scouts.stats;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.scouts.form.SC_FormLauncher;
import com.evergreen.treetop.activities.tasks.users.TM_SignUpActivity;
import com.evergreen.treetop.activities.users.SignUpActivity;
import com.evergreen.treetop.architecture.scouts.handlers.MatchDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class StatsLauncher extends AppCompatActivity {

    private final String TAG = "StatsLauncher_sc";

    public static DocumentReference scoutDataDoc;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ListView teamList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        scoutDataDoc = new MatchDB(7112).getRef();
//        startActivity(new Intent(this, PowerCellStats.class));

        setContentView(R.layout.activity_stats_launcher);

        teamList = findViewById(R.id.stats_launcher_list);

        teamList.setOnItemClickListener((parent, view, position, id) -> {
            String team = (String)parent.getItemAtPosition(position);
            scoutDataDoc = new MatchDB(Integer.parseInt(team)).getRef();
            startActivity(new Intent(this, GeneralStats.class));
        });

        getRegisteredTeams();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_global_options_menu, menu);
        menu.setGroupVisible(R.id.global_menu_tm, false);
        menu.setGroupVisible(R.id.global_menu_sc, false);
        menu.setGroupVisible(R.id.global_menu_stats_general, false);
        menu.setGroupVisible(R.id.global_menu_stats_pc, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_stats_scout:
                startActivity(new Intent(this, SC_FormLauncher.class));
                break;
            case R.id.menu_stats_tm:
                startActivity(new Intent(this, TM_SignUpActivity.class));
                break;
        }
        return true;
    }

    private void getRegisteredTeams() {
        List<String> teams = new ArrayList<>();

        db.collection("Scouting").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "data retrieval task successful.");
                        for (QueryDocumentSnapshot team : task.getResult()) {
                            teams.add(team.getId());
                        }
                        Log.v(TAG, teams.toString());
                        teamList.setAdapter(new ArrayAdapter<>(this, R.layout.listrow_stats_teams_picker, teams));
                    }
                    else {
                        Log.d(TAG, "data retrieval task failed.");
                    }
                });
    }
}
