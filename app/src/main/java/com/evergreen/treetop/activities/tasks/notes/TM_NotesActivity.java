package com.evergreen.treetop.activities.tasks.notes;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;

import android.Manifest;
import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.tasks.utils.AndroidUtils;
import com.evergreen.treetop.ui.views.recycler.NotesRecycler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TM_NotesActivity extends AppCompatActivity {

    private NotesRecycler m_listNotes;

    private static final int STORAGE_ACCESS_REQUEST_CODE = 0;
    private static final int CAMERA_ACCESS_REQUEST_CODE = 1;

    public static final String NOTES_NUMBER_KEY  = "notes-num";
    public static final String NOTE_PREFIX_KEY = "note-";
    public static final String NOTE_TYPE_PREFIX_KEY  = "note-type-";



    private final ActivityResultLauncher<Intent> m_photoPicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    addImageNote(getImagePath(result.getData().getData()));
                }
            }
    );


    private final ActivityResultLauncher<Intent> m_photoTaker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    addImageNote(getCapturePath());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_tm);


        getSaveFile().edit().clear().apply();
        m_listNotes = findViewById(R.id.tm_notes_recycler);
        m_listNotes.load(getData());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_ACCESS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickPhoto();
                }
                break;
            case CAMERA_ACCESS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
                break;

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes_options, menu);

        // Currently camera is only supported for Android 10 and below, due to the heavier
        // Storage permissions.
        // TODO use file provider to support API 30 and up.
//        if (android.os.Build.VERSION.SDK_INT >= 30) {
//            menu.removeItem(R.id.tm_notes_options_meni_add_camera);
//        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.tm_notes_options_meni_add_text) {
            showAddDialog();
        } else if (itemId == R.id.tm_notes_options_meni_add_image) {
            pickPhoto();
        } else if (itemId == R.id.tm_notes_options_meni_add_camera) {
            takePhoto();
        }

        return true;
    }

    private void showAddDialog() {

        EditText dialogView = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Add a text note")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> addTextNote(dialogView.getText().toString()))
                .setNegativeButton("Cancel", null)
                .create().show();
    }

    private void takePhoto() {
        // Same thing - request for permissions if we don't have them.
        if (ActivityCompat.checkSelfPermission(this, permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    ) {
            ActivityCompat.requestPermissions(this,
                    new String[] {permission.CAMERA, permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE},
                    CAMERA_ACCESS_REQUEST_CODE);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = Uri.fromFile(new File(getCapturePath()));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            // Some requires security bullshit
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            m_photoTaker.launch(intent);
        }
    }

    private void pickPhoto() {
        // Same thing - request for permissions if we don't have them.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_ACCESS_REQUEST_CODE);
        } else {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            m_photoPicker.launch(photoPicker);
        }
    }

    private void addTextNote(String text) {
        saveNote(text, false);
    }

    private void addImageNote(String path) {
        saveNote(path, true);
    }

    private List<Pair<String, Boolean>> getData() {
        List<Pair<String, Boolean>> res = new ArrayList<>();

        for (int i = 0; i < getNotesCount(); i++) {
            res.add(new Pair<>(getNote(i), getNoteType(i)));
        }

        return res;
    }

    private int getNotesCount() {
        return getSaveFile().getInt(NOTES_NUMBER_KEY, 0);
    }

    private String getNote(int number) {
        return getSaveFile().getString(NOTE_PREFIX_KEY + number, "");
    }

    private boolean getNoteType(int position) {
        return getSaveFile().getBoolean(NOTE_TYPE_PREFIX_KEY + position, false);
    }

    private void saveNote(String noteData, boolean isImage) {
        SharedPreferences.Editor prefEditor = getSaveFile().edit();
        int noteNum = getNotesCount() + 1;
        prefEditor.putInt(NOTES_NUMBER_KEY, noteNum);
        prefEditor.putString(NOTE_PREFIX_KEY + noteNum, noteData);
        prefEditor.putBoolean(NOTE_TYPE_PREFIX_KEY + noteNum, isImage);
        prefEditor.apply();

        m_listNotes.getAdapter().add(noteData, isImage);
    }

    private String getImagePath(Uri imageUri) {
        // Honestly I am not sure wtf is going on here, but if it works, I guess it works!
        // Basically, the gallery picker returns a URI, which is an identifier/pointer of the image
        // Than you can use a query (represented by a Curesor object )to get some specific data.
        // It's basically an SQL query, but I am not at all sure wtf about an image is table-like.
        // We filter for "Media.DATA", whatever the hell that filters for,
        // And then the path seems to be in that column and the first row.
        // What ever.
        Cursor cursor = getContentResolver().query(
                imageUri,
                new String[] {Media.DATA},
                null, null, null);

        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(Media.DATA);
        return cursor.getString(columnIndex);
    }

    private String getCapturePath() {
        return  getExternalCacheDir().getAbsolutePath() + "/note-" + getNotesCount() + ".jpg";
    }

    public SharedPreferences getSaveFile() {
        return getSaveFile(this);
    }

    public static SharedPreferences getSaveFile(Context context) {
        return context.getSharedPreferences("notes", Context.MODE_PRIVATE);
    }

}