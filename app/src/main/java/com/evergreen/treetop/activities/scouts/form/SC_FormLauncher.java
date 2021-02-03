package com.evergreen.treetop.activities.scouts.form;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.form.MatchID;

public class SC_FormLauncher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_launcher_sc);

        MatchID a = new MatchID(MatchID.MatchType.QUAL, 20);
        MatchID b = new MatchID(MatchID.MatchType.QUAL, 10);
        MatchID c = new MatchID(MatchID.MatchType.QUAL, 20);
        MatchID d = new MatchID(MatchID.MatchType.PLAYOFF, 10);

        Log.d("Testing", "===TESTING===");

        Log.d("Testing", a.toString() + " after " + b.toString() + ":" + a.after(b));
        Log.d("Testing", a.toString() + " before " + b.toString() + ":" + a.before(b));
        Log.d("Testing", a.toString() + " is " + b.toString() + ":" + a.equals(b));

        Log.d("Testing", a.toString() + " after " + c.toString() + ":" + a.after(c));
        Log.d("Testing", a.toString() + " before " + c.toString() + ":" + a.before(c));
        Log.d("Testing", a.toString() + " is " + c.toString() + ":" + a.equals(c));

        Log.d("Testing", a.toString() + " after " + d.toString() + ":" + a.after(d));
        Log.d("Testing", a.toString() + " before " + d.toString() + ":" + a.before(d));
        Log.d("Testing", a.toString() + " is " + d.toString() + ":" + a.equals(d));
    }



}