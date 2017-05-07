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
    }

    @Override
    public String loadNoteContent(String name) {
        return localStorage.loadNoteContent(name);
    }

    @Override
    public List<String> getList() {
        return localStorage.getList();
    }

    @Override
    public void deleteNote(String name) {
        localStorage.deleteNote(name);
    }

    public interface OnSynchronizeFinishedCallback {
        void onSynchronizeFinished();
    }
}
