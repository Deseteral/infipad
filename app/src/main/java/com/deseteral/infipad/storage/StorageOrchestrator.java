package com.deseteral.infipad.storage;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveFolder;

import java.util.List;

public class StorageOrchestrator implements Storage {
    private final LocalStorage localStorage;
    private final DriveStorage driveStorage;

    private static final String TAG = "STORAGE_ORCH";

    public StorageOrchestrator(Context context, GoogleApiClient googleApiClient, DriveFolder rootFolder) {
        this.localStorage = new LocalStorage(context);
        this.driveStorage = new DriveStorage(googleApiClient, rootFolder);
        Log.i(TAG, "Created storage orchestrator");
    }

    public void synchronize(final OnSynchronizeFinishedCallback callback) {
        Log.i(TAG, "Synchronizing remote and local storage");

        driveStorage.getList(new OnListFetchedCallback() {
            @Override
            public void onListFetched(List<String> list) {
                Log.i(TAG, "" + list.size());
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onSynchronizeFinished();
            }
        }, 2000);
    }

    @Override
    public void saveNote(String name, String content) {
        localStorage.saveNote(name, content);
        driveStorage.saveNote(name, content);
    }

    @Override
    public String loadNoteContent(String name) {
        return localStorage.loadNoteContent(name);
    }

    @Override
    public void getList(final OnListFetchedCallback callback) {
        localStorage.getList(new OnListFetchedCallback() {
            @Override
            public void onListFetched(List<String> list) {
                callback.onListFetched(list);
            }
        });
    }

    @Override
    public void deleteNote(String name) {
        localStorage.deleteNote(name);
    }

    public interface OnSynchronizeFinishedCallback {
        void onSynchronizeFinished();
    }

    private static StorageOrchestrator instance;

    public static void setInstance(StorageOrchestrator storageOrchestrator) {
        instance = storageOrchestrator;
    }

    public static StorageOrchestrator getInstance() {
        return instance;
    }
}
