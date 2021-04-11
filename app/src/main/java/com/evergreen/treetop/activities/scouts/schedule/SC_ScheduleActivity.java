package com.evergreen.treetop.activities.scouts.schedule;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.data.MatchTeam;
import com.evergreen.treetop.architecture.scouts.utils.MatchID;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
import com.evergreen.treetop.ui.custom.recycler.MatchAdapter;

import java.util.Arrays;

public class SC_ScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_sc);

        RecyclerView matchListView = findViewById(R.id.sc_rec_sched_main_list);
        matchListView.setAdapter(
                new MatchAdapter(
                        this,

                        new ScoutingMatch(
                                new MatchID(MatchID.MatchType.QUAL, 1),
                                Arrays.asList(
                                        new MatchTeam(1, "First FIRST", true, "abc1"),
                                        new MatchTeam(2, "second FIRST", true, "abc2"),
                                        new MatchTeam(3, "third FIRST", true, "abc3"),
                                        new MatchTeam(4, "fourth FIRST", false, "abc4"),
                                        new MatchTeam(5, "fifth FIRST", false, "abc5"),
                                        new MatchTeam(6, "sixth FIRST", false, "abc6")
                                )
                        ),

                        new ScoutingMatch(
                                new MatchID(MatchID.MatchType.QUAL, 2),
                                Arrays.asList(
                                        new MatchTeam(1, "First FIRST", true, "abc1"),
                                        new MatchTeam(5, "Fifth FIRST", true, "abc2"),
                                        new MatchTeam(9, "Ninth FIRST", true, "abc3"),
                                        new MatchTeam(4, "fourth FIRST", false, "abc4"),
                                        new MatchTeam(10, "Tenth FIRST", false, "abc5"),
                                        new MatchTeam(11, "Eleventh FIRST", false, "abc6")
                                )
                        ),

                        new ScoutingMatch(
                                new MatchID(MatchID.MatchType.PLAYOFF, 1),
                                Arrays.asList(
                                        new MatchTeam(1, "First", true, "abc1"),
                                        new MatchTeam(23, "TWENTY THREE", true, "abc2"),
                                        new MatchTeam(30, "NAMES", true, "abc3"),
                                        new MatchTeam(34, "fourth Thirtieth", false, "abc4"),
                                        new MatchTeam(54, "fifth Fourth", false, "abc5"),
                                        new MatchTeam(69, "Nice.", false, "abc6")
                                )
                        )
                )
        );


        matchListView.setLayoutManager(new LinearLayoutManager(this));


    }
}