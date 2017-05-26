package com.deseteral.infipad.storage;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class LocalStorage implements Storage {
    private final Context context;
    private final File appFolder;

    private final String TAG = "LOCAL_STORAGE";

    LocalStorage(Context context) {
        this.context = context;
        this.appFolder = context.getFilesDir();

        Log.i(TAG, "Created local storage");
    }

    @Override
    public void saveNote(String name, String content) {
        final String filename = getFileForName(name).getName();

        try {
            final FileOutputStream os = context.openFileOutput(filename, Context.MODE_PRIVATE);

            os.write(content.getBytes());
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String loadNoteContent(String name) {
        final File file = getFileForName(name);
        final StringBuilder text = new StringBuilder();

        try {
            final BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return text.toString();
    }

    @Override
    public void getList(OnListFetchedCallback callback) {
        final List<String> list = new ArrayList<>();

        for (File f : appFolder.listFiles()) {
            final String filename = f.getName();
            final String extension = filename.substring(filename.length() - 3);
            if (extension.equals(".md")) {
                list.add(filename.substring(0, filename.length() - 3));
            }
        }

        callback.onListFetched(list);
    }

    @Override
    public void deleteNote(String name) {
        getFileForName(name).delete();
    }

    private File getFileForName(String name) {
        final String filename = String.format("%s.md", name);
        return new File(appFolder, filename);
    }
}
