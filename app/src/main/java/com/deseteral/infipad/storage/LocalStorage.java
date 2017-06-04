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
    /**
     * Root folder for the app
     */
    private final File appFolder;

    /**
     * Application context reference
     */
    private final Context context;

    private final String TAG = "LOCAL_STORAGE";

    /**
     * Creates new local storage mounting point
     * @param context
     */
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
                final List<String> tags = readTagList(f);

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

    /**
     * Returns file handle for note file with specified name
     * @param name the name of the note
     * @return file handle
     */
    private File getFileForName(String name) {
        final String filename = String.format("%s.md", name);
        return new File(appFolder, filename);
    }

    /**
     * Reads the tag list for the note
     * @param file file handle for the note
     * @return list of tags
     */
    private List<String> readTagList(File file) {
        final String tagsLineStart = "tags: ";
        String line;
        String lastLine = "";
        BufferedReader input;

        try {
            input = new BufferedReader(new FileReader(file));

            while ((line = input.readLine()) != null) {
                lastLine = line;
            }

            input.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!lastLine.startsWith(tagsLineStart)) {
            return new ArrayList<>();
        }

        String[] tags = lastLine
                .substring(tagsLineStart.length())
                .split(",");

        for (int i = 0; i < tags.length; i++) {
            tags[i] = tags[i].trim();
        }

        return Arrays.asList(tags);
    }
}
