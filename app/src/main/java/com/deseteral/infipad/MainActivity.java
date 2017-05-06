package com.deseteral.infipad;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.deseteral.infipad.storage.LocalStorage;
import com.deseteral.infipad.storage.StorageOrchestrator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private DriveFolder driveAppFolder;

    private StorageOrchestrator storage;
    private List<String> fileList;

    private static final String TAG = "MAIN_ACTIVITY";
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 3;
    private static final String DRIVE_APP_FOLDER_TITLE = "infipad app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        storage = new StorageOrchestrator(
                new LocalStorage(this)
        );

        fileList = storage.getList();

        ListView list = (ListView) findViewById(R.id.note_list);
        list.setAdapter(new ArrayAdapter<>(this, R.layout.element_note, fileList));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView textView = (TextView) view;
                final String noteName = textView.getText().toString();

                startNoteActivity(noteName, storage.loadNoteContent(noteName));
            }
        });
    }

    private void lookForApplicationFolder() {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, DRIVE_APP_FOLDER_TITLE))
                .build();

        Drive.DriveApi
                .getRootFolder(googleApiClient)
                .queryChildren(googleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                        MetadataBuffer bufferResult = result.getMetadataBuffer();
                        boolean isFolderCreated = bufferResult.getCount() > 0;

                        if (isFolderCreated) {
                            driveAppFolder = result
                                    .getMetadataBuffer()
                                    .get(0)
                                    .getDriveId()
                                    .asDriveFolder();

                            Log.i(TAG, "GDrive app folder found");
                        } else {
                            Log.i(TAG, "GDrive app folder not found. Creating...");

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(DRIVE_APP_FOLDER_TITLE)
                                    .build();

                            Drive.DriveApi
                                    .getRootFolder(googleApiClient)
                                    .createFolder(googleApiClient, changeSet)
                                    .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                                        @Override
                                        public void onResult(@NonNull DriveFolder.DriveFolderResult driveFolderResult) {
                                            Log.i(TAG, "GDrive app folder created");
                                            lookForApplicationFolder();
                                        }
                                    });
                        }
                    }
                });
    }

    public void onFabClick(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.new_note_dialog_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        final AlertDialog dialog = (AlertDialog) dialogInterface;
                        final EditText nameField = (EditText) dialog.findViewById(R.id.dialog_new_note_name_field);
                        final String name = nameField.getText().toString().trim();

                        newFile(name);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setView(getLayoutInflater().inflate(R.layout.dialog_new_note, null));

        builder.create().show();
    }

    private void newFile(String noteName) {
        Log.i(TAG, "Create new note with title: " + noteName);

        final String initialNoteContent = "# This is a new note";

        storage.saveNote(noteName, initialNoteContent);
        startNoteActivity(noteName, initialNoteContent);
    }

    private void startNoteActivity(String noteName, String noteContent) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(NoteActivity.NOTE_NAME, noteName);
        intent.putExtra(NoteActivity.NOTE_CONTENT, noteContent);

        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GoogleApiAvailability
                    .getInstance()
                    .getErrorDialog(this, connectionResult.getErrorCode(), 0)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    googleApiClient.connect();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * Google API connection callbacks
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to Google API services");
        lookForApplicationFolder();
    }

    @Override
    public void onConnectionSuspended(int i) { }
}
