package com.deseteral.infipad.storage;

import android.content.Context;
import android.util.Log;

import com.deseteral.infipad.domain.Note;
import com.deseteral.infipad.service.ApplicationState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalStorage implements Storage {
    private final File appFolder;
    private final Context context;

    private final String TAG = "LOCAL_STORAGE";

    public LocalStorage(Context context) {
        this.context = context;
        this.appFolder = context.getFilesDir();

        Log.i(TAG, "Created local storage");
    }

    @Override
    public void saveNote(Note note) {
        final String filename = getFileForName(note.getName()).getName();

        try {
            final FileOutputStream os = context.openFileOutput(filename, Context.MODE_PRIVATE);

            os.write(note.getContent().getBytes());
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void loadNoteContent(Note note) {
        final File file = getFileForName(note.getName());
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

        note.setContent(text.toString());
    }

    @Override
    public List<Note> getList() {
        final List<Note> list = new ArrayList<>();

        for (File f : appFolder.listFiles()) {
            final String filename = f.getName();
            final String extension = filename.substring(filename.length() - 3);
            if (extension.equals(".md")) {
                final String name = filename.substring(0, filename.length() - 3);
                final List<String> tags = new ArrayList<>();

                final Note n = new Note(name, null, tags);
                list.add(n);
            }
        }

        return list;
    }

    @Override
    public void deleteNote(Note note) {
        getFileForName(note.getName()).delete();
    }

    private File getFileForName(String name) {
        final String filename = String.format("%s.md", name);
        return new File(appFolder, filename);
    }

    public File getAppFolder() {
        return appFolder;
    }
}
