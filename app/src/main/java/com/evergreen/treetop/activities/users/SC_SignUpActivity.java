package com.evergreen.treetop.activities.users;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.evergreen.treetop.R;
import com.evergreen.treetop.test.TestActivity;

public class SC_SignUpActivity extends AppCompatActivity {

    int RC_SIGN_IN = 0;
    private static final boolean TEST = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (TEST) {
            startActivity(new Intent(this, TestActivity.class));
            return;
        }

    }
}