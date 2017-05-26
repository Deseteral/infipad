package com.deseteral.infipad.storage;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.List;

public class StorageOrchestrator implements Storage {
    private final LocalStorage localStorage;

    private static final String TAG = "STORAGE_ORCH";

    public StorageOrchestrator(Context context) {
        this.localStorage = new LocalStorage(context);
        Log.i(TAG, "Created storage orchestrator");
    }

    public void synchronize(final OnSynchronizeFinishedCallback callback) {
        Log.i(TAG, "Synchronizing remote and local storage");

        // give some time for animation to end
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onSynchronizeFinished();
            }
        }, 1500);
    }

    @Override
    public void saveNote(String name, String content) {
        localStorage.saveNote(name, content);
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
