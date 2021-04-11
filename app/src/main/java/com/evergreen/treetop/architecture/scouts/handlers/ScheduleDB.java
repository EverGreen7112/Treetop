package com.evergreen.treetop.architecture.scouts.handlers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
import com.evergreen.treetop.architecture.users._User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ScheduleDB {


    private ScheduleDB() { }

    private static ScheduleDB m_instance = new ScheduleDB();
    public static ScheduleDB getInstance() {
        return m_instance;
    }



    CollectionReference m_ref = FirebaseFirestore.getInstance().collection("scouting-schedule");


    public void onMatches(Consumer<List<ScoutingMatch>> action, Consumer<Exception> onFailure, Runnable onCanceled) {

        Task<QuerySnapshot> request = m_ref.get();

        request.addOnSuccessListener(snapshot -> {

            List<ScoutingMatch> matches = new ArrayList<>();

            for (DocumentSnapshot doc : snapshot) {
                matches.add(doc.toObject(ScoutingMatch.class));
            }

            action.accept(matches);
        });

        request.addOnFailureListener(onFailure::accept);
        request.addOnCanceledListener(onCanceled::run);

    }


    public void onMatches(Consumer<List<ScoutingMatch>> action, Consumer<Exception> onFailure) {
        onMatches(action, onFailure, () -> {});
    }

    public void addMatches(ScoutingMatch... matches) {

        Log.i("DB_EVENT", "Adding matches to ScheduleDB: " + Utilities.stringify(matches));

        for (ScoutingMatch match : matches) {
            m_ref.document(match.getID().toString()).set(match);
        }

    }


    public void assignUsers(@NonNull _User[] users, int rounds, @NonNull  Consumer<Exception> onFailure, @NonNull  Runnable onCanceled) {
        onMatches(
                matches -> {

                    // TODO currently, upon receiving a group of 7 people, this algorithm will take
                    //  two scouters to divy up the work (such that each does half the rounds), and
                    //  make the other 5 do all rounds. There are better ways to do this.

                    // Breaks the users to groups of 6, with the remaining creating another
                    // group with a few unlucky members of the ones already slotted
                    // Will divide up the rounds as equally as possible between them
                    // (with the first groups taking one extra each until it
                    // until they run out),
                    //
                    // If there are less then 6 scouters, then they will each take an arbitrary team
                    // each round.

                    // --- LOGGING--
                    Log.i("DB_EVENT","Starting user-match assignment");
                    Log.v(
                            "USER_ASSIGN",
                            "Parameters: " +
                                    "\nUsers: " + Utilities.stringify(users) +
                                    "\nRounds: " + rounds +
                                    "\nOnFailure Consumer<Exception>" +
                                    "\nOnCanceled Runnable"
                    );

                    Log.v("USER_ASSIGN", "DB Matches: " + Utilities.stringify(matches));

                    // ---

                    matches = matches.stream().limit(rounds).collect(Collectors.toList());

                    // -- LOGGING ---
                    Log.v("USER_ASSIGN|VAR_MOD", "Limited matches to " + rounds +
                            " rounds: " + Utilities.stringify(matches));
                    //------
                    Random rng = new Random();
                    Log.v("USER_ASSIGN", "Initialized RNG.");
                    //------


                    if (users.length < 6) {


                        // -- LOGGING ---
                        Log.v("USER_ASSIGN|CONDITION", "Less than 6 users given; not all teams will be scouted.");
                        //    For each match matches:
                        Log.v("USER_ASSIGN", "Iterating over each match");
                        //------

                        for (ScoutingMatch match : matches) {

                            // -- LOGGING ---
                            Log.v("USER_ASSIGN|ITERATION|PARAM",
                                  "Match " + match.toString() + " in matches " + Utilities.stringify(matches));
                            Log.v("USER_ASSIGN|ITERATION", "Matching teams to users");
                            //------

                            for (int i = 0; i < users.length; i++)  {
                                // -- LOGGING ---
                                Log.v("USER_ASSIGN|ITERATION2", "Params: i=" + i);
                                //------

                               match.getTeams().get(i).setUser(users[i]);

                                // -- LOGGING ---
                               Log.v("USER_ASSIGN|VAR_MOD",
                                        "Set user "
                                        + users[i].toString() + "on match " + match.toString());
                                //------
                            }
                        }


                    } else { // If $users > 6:
                        Log.v("USER_ASSIGN|CONDITION", "More than 6 users given");
                        // let groups = ceil($users / 6)
                        int groups =  (int)Math.ceil(users.length / 6.0);
                        Log.v("USER_ASSIGN|VAR_IN", "Group size (from " + users.length
                                        + " users): " + groups;

                        // let scouting be an empty list
                        List<_User> scouters = Arrays.asList(users);
                        Log.v("USER_ASSIGN|VAR_IN", "Scouters: " + Utilities.stringify(scouters));
                        //      let cycle be a [$groups, 6] Array
                        _User[][] cycle = new _User[groups][6];
                        Log.v("USER_ASSIGN|VAR_IN", "Created an empty array " + groups + ", 6");
                        //      for group in cycle:
                        for (int groupIndex = 0; groupIndex < groups; groupIndex++) {
                            Log.v("USER_ASSIGN|ITERATION", "Creating groups; groupIndex=" + groupIndex);
                            //  pop min(6, stack size) users, put in $group and scouting
                            int groupSize = Math.min(6, scouters.size();
                            Log.v("USER_ASSIGN|VAR_IN", "Group size: " + groupSize);
                            for (int userIndex = 0; userIndex < groupSize; userIndex++) {
                                Log.v("USER_ASSIGN|ITERATION",
                                        "Filling group; groupIndex=" + groupIndex +
                                                ", userIndex=" + userIndex);
                                int position = rng.nextInt(scouters.size());
                                cycle[groupIndex][userIndex] = scouters.get(position);
                                scouters.remove(position);
                            }
                        }

                        //      if cycle[-1] is not 6:
                        int lastGroupLength = cycle[cycle.length - 1].length
                        if (cycle[cycle.length - 1].length != 6) {
                            Log.v("USER_ASSIGN|CONDITION", "Last group has gaps (" + lastGroupLength  + ")");
                            // let remain = 6 - cycle[-1]
                            // pick $remain arbitrary users from scouters
                            //  complete cycle[-1]
                            scouters = Arrays.asList(users);
                            for ( ; lastGroupLength < 6 ; lastGroupLength++) {
                                Log.v("USER_ASSIGN|ITERATION", "Fillin last group; size=" + lastGroupLength);
                                int position = rng.nextInt(scouters.size());
                                Log.v("USER_ASSIGN|VAR_IN", "Picking user num " + position + ", 6");
                                cycle[cycle.length - 1][lastGroupLength] = scouters.get(position);
                                scouters.remove(position);
                            };

                        }


                        int roundIndex = 0;
                        int groupIndex = 0;
                        int roundsPerGroupFloor = rounds / groups;
                        int remainderCount = rounds % groups;

                        for (int matchIndex = 0; matchIndex < matches.size(); matchIndex++) {

                            if (roundIndex == roundsPerGroupFloor + 1 && remainderCount > 0
                                || roundIndex == roundsPerGroupFloor && remainderCount <= 0) {
                                groupIndex++;
                            }

                            for (int teamIndex = 0; teamIndex < 6; teamIndex++) {
                                matches.get(matchIndex) // The currently iterated match
                                        .getTeams() // The teams of the currently iterated match
                                        .get(teamIndex) // at the currently iterated team:
                                        .setUser(cycle[groupIndex][teamIndex]); // Set to the matching user in the currently iterated grou

                                if (roundIndex == roundsPerGroupFloor) {
                                    remainderCount--;
                                }

                                roundIndex++;

                            }

                        }

                        for (ScoutingMatch match : matches) {
                            m_ref.document(match.getID().toString()).set(match);
                        }

                    }
                },

                onFailure,
                onCanceled);
    }

    private void setMatches(List<ScoutingMatch> matches) {
        m_ref.
    }

    /**
     * Much more complicated; TODO to use later
     * @param users
     * @param groups
     * @param matchesPerCycle
     * @param matches
     */
    public void _assignScouters(_User[] users, int groups, int matchesPerCycle, int matches) {
        // If $users <= 6:
        //    For each match matches:
        //          arbitrarily pick $users teams
        //          assign distinct users for each match.
        //          return;
        //
        // let size = $users // $groups; This is how many users are in each group, omitting extras
        // Arbitrarily divide users into $groups groups $size users each, leaving extras out
        // Assign each extra (Totaling $users % $groups) to a different group arbitrarily.
        // If $groups > 1, $size < 5:
        //    let lack = $groups*(6 - $size)
        //    foreach
        //    if $users >= $lack:
        //        Arbitrarily pick $lack users.
        //        Assign each user to a
        //
        //
        //
        //
        // Divide matches into m groups sized matches per cycle
        // Alternate between the n user-groups throughout the m match-groups
        // If a given user group has k more  members than 6:
        //    If k > 6:
        //        Divide rounds into k//6 groups
        //        If k//6 > rounds:
        //             Arbitrarily omit groups of 6 until k//6 <= rounds A
        //        Arbitrarily divide users into k groups of 6 and
        //        alternate them between the k//6 groups of rounds.
        //    If k < 6:
        //        If rounds < 2:
        //            Arbitrarily omit k-members
        //        If rounds >= 2:.
        //            Arbitrarily pick two groups of k members,
        //            Divide matches into two groups of following matches; extras in the later one
        //            Alternate the k-member groups in the
    }
}
