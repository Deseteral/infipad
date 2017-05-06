package com.deseteral.infipad.storage;

import android.util.Log;

public class StorageOrchestrator implements Storage {
    private final LocalStorage localStorage;

    private static final String TAG = "STORAGE_ORCH";

    public StorageOrchestrator(LocalStorage localStorage) {
        this.localStorage = localStorage;
        Log.i(TAG, "Created storage orchestrator");
    }

    @Override
    public void saveNote(String name, String content) {
        localStorage.saveNote(name, content);
    }
}
