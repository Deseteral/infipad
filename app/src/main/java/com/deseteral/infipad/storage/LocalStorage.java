package com.deseteral.infipad.storage;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LocalStorage implements Storage {
    private final Context context;
    private final File appFolder;

    private final String TAG = "LOCAL_STORAGE";

    public LocalStorage(Context context) {
        this.context = context;
        this.appFolder = context.getFilesDir();

        Log.i(TAG, "Created local storage");
    }

    @Override
    public void saveNote(String name, String content) {
        final String filename = String.format("%s.md", name);

        try {
            final FileOutputStream os = context.openFileOutput(filename, Context.MODE_PRIVATE);

            os.write(content.getBytes());
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
