package com.deseteral.infipad;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private DriveFolder driveAppFolder;

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
        newFile();
    }

    private void newFile() {
        Intent intent = new Intent(this, NoteActivity.class);
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
