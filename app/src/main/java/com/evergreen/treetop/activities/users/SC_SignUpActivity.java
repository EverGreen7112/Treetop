package com.evergreen.treetop.activities.users;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.activities.scouts.form.SC_FormLauncher;
import com.evergreen.treetop.activities.scouts.schedule.SC_ScheduleActivity;
import com.evergreen.treetop.architecture.users.UserDB;
import com.evergreen.treetop.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;
public class SC_SignUpActivity extends AppCompatActivity {

    int RC_SIGN_IN = 0;
    private static final boolean TEST = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (TEST) {
            startActivity(new Intent(this, SC_ScheduleActivity.class));
            return;
        }

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().setRequireName(false).build(),
                //  new AuthUI.IdpConfig.PhoneBuilder().setWhitelistedCountries(Collections.singletonList("IL")).build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Log.i("UI_EVENT|DB_EVENT", "Redirected to sign in/sign up activity");

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.circle)
                        .build(),
                RC_SIGN_IN,
                null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                Log.i("DB_EVENT", "User connected: " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                if (response.isNewUser()) {
                    UserDB.getInstance().registerCurrent();
                    Log.i("DB_EVENT", "New User Registered!");
                    // TODO send to input extra user details
                }

                startActivity(new Intent(this, SC_ScheduleActivity.class));
            } else {
                if (response.getError() != null) {
                    Log.w("DB_FAILURE", "Logging failed: " + response.getError().getMessage());
                    Toast.makeText(
                            getApplicationContext(),
                            "Log in failure: code " + response.getError().getErrorCode(),
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Log.w("DB_FAILURE", "Logging failed.");
                    Toast.makeText(
                            getApplicationContext(),
                            "Log in failure, code " + resultCode,
                            Toast.LENGTH_SHORT
                    ).show();
                }

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(
                                        Arrays.asList(
                                                new AuthUI.IdpConfig.EmailBuilder().setRequireName(false).build(),
                                                new AuthUI.IdpConfig.GoogleBuilder().build()
                                        )
                                )
                                .setLogo(R.drawable.circle)
                                .build(),
                        RC_SIGN_IN,
                        null);
            }
        }
    }
}