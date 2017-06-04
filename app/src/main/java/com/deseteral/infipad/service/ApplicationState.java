package com.deseteral.infipad.service;

import android.content.Context;
import android.util.Log;

import com.deseteral.infipad.domain.Notepad;
import com.deseteral.infipad.storage.LocalStorage;

public class ApplicationState {
    private Context context;
    private LocalStorage storage;
    private Notepad notepad;

    private static final String TAG = "APP_STATE";

    private ApplicationState(Context context) {
        this.context = context;
        storage = new LocalStorage(context);
        notepad = new Notepad();

        Log.i(TAG, "Constructor invoked");
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public LocalStorage getStorage() {
        return storage;
    }

    public Notepad getNotepad() {
        return notepad;
    }

    /*
     * singleton
     */
    private static ApplicationState state = null;
    public static void createState(Context context) {
        if (state == null) {
            state = new ApplicationState(context);
            Log.i(TAG, "Created Application State");
        }
    }

    public static ApplicationState getState() {
        return state;
    }
}
