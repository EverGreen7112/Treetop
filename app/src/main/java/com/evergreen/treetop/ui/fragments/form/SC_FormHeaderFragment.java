package com.evergreen.treetop.ui.fragments.form;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.data.GameStage;
import com.evergreen.treetop.architecture.scouts.form.SequenceSwitch;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;

import java.util.Locale;

public class SC_FormHeaderFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisView =  inflater.inflate(R.layout.fragment_form_header, container, false);


        TextView matchTimer = thisView.findViewById(R.id.sc_form_auto_match_timer);
        Handler handler = new Handler();

        Runnable counter = new Runnable() {
            @Override
            public void run() {
                long matchSeconds = ScoutingMatch.getCurrent().getTimeSinceStart() / 1000;
                if (matchSeconds <= 3 * 60) {
                    matchTimer.setText(formatTimer(matchSeconds));
                    handler.postDelayed(this, 1000);
                    Log.v("UI_EVENT", "Set Match Timer (Auto) to  " + formatTimer(matchSeconds));
                    Log.v("UI_EVENT", "Posting autonomous Match Timer increment");
                }
            }
        };

        handler.postDelayed(counter, 1000);
        Log.v("UI_EVENT", "Posting autonomous Match Timer increment");
        return thisView;


        new SequenceSwitch(
                "Functions",
                GameStage.getCurrentStage().getName().toLowerCase() + "/function",
                thisView.findViewById(R.id.sc_form_auto_function_switch),
                thisView.findViewById(R.id.sc_form_auto_function_label)
        );
    }

    private String formatTimer(long seconds) {
        long mins = seconds / 60;
        long secs = seconds % 60;
        return String.format(Locale.ENGLISH, "%02d:%02d", mins, secs);
    }
}