package com.evergreen.treetop.activities.users;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.tasks.TM_TaskEditorActivity;
import com.evergreen.treetop.architecture.tasks.handlers.UserDB;
import com.evergreen.treetop.test.TestActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.lang3.exception.ExceptionUtils;

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
            startActivity(new Intent(this, TestActivity.class));
            return;
        }

        List<IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().setRequireName(false).build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Log.i("UI_EVENT|DB_EVENT", "Redirected to sign in/sign up activity");

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_app)
                .build();

        ActivityResultLauncher<Intent> signLauncher  = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleSignIn
        );
    }


    private void handleSignIn(ActivityResult result) {

        IdpResponse resultData = IdpResponse.fromResultIntent(result.getData());

        if (result.getResultCode() == RESULT_OK) {
            Log.i("DB_EVENT", "User connected: " + FirebaseAuth.getInstance().getCurrentUser().getEmail());

            if (resultData.isNewUser()) {
                UserDB.getInstance().registerCurrent();
                Log.i("DB_EVENT", "New User Registered!");
                // TODO send to input extra user details
            } else {
                launchHomepage();
            }


        } else {

            FirebaseUiException error = resultData.getError();

            if (error != null) {
                Log.w("DB_FAILURE", "Logging failed: " + error.getMessage() + "\n" + ExceptionUtils.getStackTrace(error));
                Toast.makeText(
                        this,
                        "Log in failure (error code " + resultData.getError().getErrorCode() + ")",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Log.w("DB_FAILURE", "Logging failed: result code " + result.getResultCode());
                Toast.makeText(
                        this,
                        "Log in failed (error code " + result.getResultCode() + ")",
                        Toast.LENGTH_SHORT
                ).show();
            }

        }

    }


    private void launchHomepage() {
        startActivity(new Intent(this, TestActivity.class));
    }
}