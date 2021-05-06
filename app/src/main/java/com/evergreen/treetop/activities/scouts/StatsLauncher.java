package com.evergreen.treetop.activities.scouts;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.handlers.TeamDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class StatsLauncher extends AppCompatActivity {

    private final String TAG = "StatsLauncher_sc";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_launcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_global_options_menu, menu);
        menu.setGroupVisible(R.id.global_menu_tm, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private List<String> getRegisteredTeams() {
        List<String> teams = new ArrayList<>();

        db.collection("Scouting").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            teams.add(document.getId());
                            Log.i(TAG, "included team: " + document.getId());
                        }
                    }
                    else {
                        Log.d(TAG, "task failed");
                    }
                });

        return teams;
    }
}
